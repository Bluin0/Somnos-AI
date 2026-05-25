package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.api.Content
import com.example.api.GeminiClient
import com.example.api.GenerateContentRequest
import com.example.api.GenerationConfig
import com.example.api.InlineData
import com.example.api.Part
import com.example.api.SleepExtractedData
import com.example.api.getGeminiApiKey
import com.example.data.ChatMessage
import com.example.data.SleepEntry
import com.example.data.SleepRepository
import com.example.util.MedicationSanitizer
import com.example.util.encodeBase64
import com.example.util.getCurrentTimestamp
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepViewModel(
    private val repository: SleepRepository,
    private val settings: Settings
) : ViewModel() {

    // --- State Observables ---
    val allSleepEntries: StateFlow<List<SleepEntry>> = repository.allSleepEntries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allChatMessages: StateFlow<List<ChatMessage>> = repository.allChatMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Configuración de retención de datos
    private val _selectedRetention = MutableStateFlow("1_week")
    val selectedRetention: StateFlow<String> = _selectedRetention.asStateFlow()

    // Estado de la extracción de medios (videos / imágenes)
    private val _isAnalyzingMedia = MutableStateFlow(false)
    val isAnalyzingMedia: StateFlow<Boolean> = _isAnalyzingMedia.asStateFlow()

    // Estado de sincronización con Salud
    private val _isSyncingHealth = MutableStateFlow(false)
    val isSyncingHealth: StateFlow<Boolean> = _isSyncingHealth.asStateFlow()

    private val _analyzedResult = MutableStateFlow<SleepExtractedData?>(null)
    val analyzedResult: StateFlow<SleepExtractedData?> = _analyzedResult.asStateFlow()

    private val _analysisError = MutableStateFlow<String?>(null)
    val analysisError: StateFlow<String?> = _analysisError.asStateFlow()

    // Estado del Chat
    private val _isGeneratingResponse = MutableStateFlow(false)
    val isGeneratingResponse: StateFlow<Boolean> = _isGeneratingResponse.asStateFlow()

    private val _chatError = MutableStateFlow<String?>(null)
    val chatError: StateFlow<String?> = _chatError.asStateFlow()

    init {
        // Cargar configuración de retención guardada (por defecto 1 semana)
        try {
            val savedRetention = settings.getString("retention_policy", "1_week")
            _selectedRetention.value = savedRetention
            applyRetentionPolicy()
        } catch (e: Exception) {
            println("SleepViewModel: Error loading retention configuration: ${e.message}")
        }
    }

    /**
     * Actualiza la configuración de retención y purga inmediatamente de la bd lo viejo.
     */
    fun updateRetentionSetting(setting: String) {
        viewModelScope.launch {
            try {
                settings.putString("retention_policy", setting)
                _selectedRetention.value = setting
                applyRetentionPolicy()
            } catch (e: Exception) {
                println("SleepViewModel: Error saving retention state: ${e.message}")
            }
        }
    }

    /**
     * Aplica la política de retención eliminando registros más antiguos que la fecha calculada.
     */
    fun applyRetentionPolicy() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val setting = _selectedRetention.value
                if (setting == "indefinite") return@launch

                val days = when (setting) {
                    "1_week" -> 7
                    "2_weeks" -> 14
                    "1_month" -> 30
                    else -> 7
                }
                val cutoffTime = getCurrentTimestamp() - (days.toLong() * 24 * 60 * 60 * 1000L)
                repository.deleteOldSleepEntries(cutoffTime)
            } catch (e: Exception) {
                println("SleepViewModel: Error applying retention policy: ${e.message}")
            }
        }
    }

    /**
     * Calcula una puntuación de sueño del 1 al 100 con base en criterios de calidad médica.
     */
    fun calculateSleepScore(
        sleepHours: Int,
        sleepMinutes: Int,
        awakePct: Float,
        remPct: Float,
        deepPct: Float
    ): Int {
        val totalMinutes = sleepHours * 60 + sleepMinutes

        // 1. Duración total (máx 40 puntos): Ideal entre 7h (420 min) y 9h (540 min)
        val durationPoints = when {
            totalMinutes >= 420 && totalMinutes <= 540 -> 40f
            totalMinutes < 420 -> {
                val diff = 420 - totalMinutes
                (40f - diff * 0.15f).coerceAtLeast(10f)
            }
            else -> {
                val diff = totalMinutes - 540
                (40f - diff * 0.1f).coerceAtLeast(15f)
            }
        }

        // 2. Sueño Profundo (máx 25 puntos): Ideal 15% - 25% del tiempo de sueño total
        val deepPoints = when {
            deepPct >= 15f && deepPct <= 25f -> 25f
            deepPct < 15f -> {
                (deepPct / 15f * 25f).coerceAtLeast(5f)
            }
            else -> 25f
        }

        // 3. Sueño REM (máx 25 puntos): Ideal 20% - 25%
        val remPoints = when {
            remPct >= 20f && remPct <= 25f -> 25f
            remPct < 20f -> {
                (remPct / 20f * 25f).coerceAtLeast(5f)
            }
            else -> 25f
        }

        // 4. Vigilia / Despertares (máx 10 puntos): Menos del 8% es ideal
        val awakePoints = when {
            awakePct < 5f -> 10f
            awakePct < 12f -> 7f
            awakePct < 20f -> 4f
            else -> 1f
        }

        val totalScore = (durationPoints + deepPoints + remPoints + awakePoints).toInt()
        return totalScore.coerceIn(10, 100)
    }

    /**
     * Agrega un nuevo registro de sueño en la base de datos, aplicando sanitización local de privacidad médica previa.
     */
    fun saveSleepEntry(
        dateString: String,
        bedtime: String,
        wakeupTime: String,
        sleepHours: Int,
        sleepMinutes: Int,
        awakeHours: Int,
        awakeMinutes: Int,
        remHours: Int,
        remMinutes: Int,
        essentialHours: Int,
        essentialMinutes: Int,
        deepHours: Int,
        deepMinutes: Int,
        awakePct: Float,
        remPct: Float,
        essentialPct: Float,
        deepPct: Float,
        hasMedicationToggle: Boolean,
        notesText: String,
        guessedScore: Int? = null,
        leaderboardOptIn: Boolean = false,
        leaderboardNickname: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Sanitizar las notas por si el usuario menciona marcas de pastillas o drogas
                val sanitization = MedicationSanitizer.sanitize(notesText)
                val isMedication = hasMedicationToggle || sanitization.wasMedicationDetected

                val computedScore = calculateSleepScore(
                    sleepHours = sleepHours,
                    sleepMinutes = sleepMinutes,
                    awakePct = awakePct,
                    remPct = remPct,
                    deepPct = deepPct
                )

                val entry = SleepEntry(
                    dateString = dateString,
                    bedtime = bedtime,
                    wakeupTime = wakeupTime,
                    sleepDurationHours = sleepHours,
                    sleepDurationMinutes = sleepMinutes,
                    awakeHours = awakeHours,
                    awakeMinutes = awakeMinutes,
                    remHours = remHours,
                    remMinutes = remMinutes,
                    essentialHours = essentialHours,
                    essentialMinutes = essentialMinutes,
                    deepHours = deepHours,
                    deepMinutes = deepMinutes,
                    awakePercentage = awakePct,
                    remPercentage = remPct,
                    essentialPercentage = essentialPct,
                    deepPercentage = deepPct,
                    medicationLogged = isMedication,
                    notes = sanitization.sanitizedText,
                    sleepScore = computedScore,
                    guessedScore = guessedScore,
                    leaderboardOptIn = leaderboardOptIn,
                    leaderboardNickname = leaderboardNickname
                )
                repository.insertSleepEntry(entry)
                applyRetentionPolicy() // Garantizar limpieza después de insertar
            } catch (e: Exception) {
                println("SleepViewModel: Error saving sleep entry: ${e.message}")
            }
        }
    }

    /**
     * Elimina una entrada de la base de datos local
     */
    fun deleteSleepEntry(entryId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteSleepEntryById(entryId)
            } catch (e: Exception) {
                println("SleepViewModel: Error deleting sleep entry: ${e.message}")
            }
        }
    }

    /**
     * Extrae métricas de un vídeo / imagen seleccionado (bytes) a través de la API de Gemini.
     */
    fun extractSleepDataFromMedia(bytes: ByteArray, mimeType: String) {
        _isAnalyzingMedia.value = true
        _analysisError.value = null
        _analyzedResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Validar tamaño aproximado para alertar al usuario
                if (bytes.size > 15 * 1024 * 1024) { // mayor que 15MB
                    withContext(Dispatchers.Main) {
                        _analysisError.value = "El archivo seleccionado es demasiado grande. Por favor, selecciona un clip de pantalla o captura más corta."
                        _isAnalyzingMedia.value = false
                    }
                    return@launch
                }

                val base64Data = encodeBase64(bytes)

                // Prompt de extracción de alta fidelidad
                val extractionPrompt = """
                    Estás procesando un archivo de tipo ${if (mimeType.startsWith("video")) "vÍdeo de pantalla grabada" else "imagen de captura"} que contiene datos de sueño de un smartwatch (por ejemplo: Garmin, Apple Health, Fitbit, Samsung Health o Huawei).
                    Debes identificar los indicadores y fases del sueño. Extrae la información con suma precisión y lIena los parámetros que correspondan en este esquema JSON.
                    
                    Esquema JSON a retornar:
                    {
                      "bedtime": "HH:MM",  (Hora de acostarse, en formato 24h, ej: "23:15")
                      "wakeupTime": "HH:MM", (Hora de despertarse, en formato 24h, ej: "07:30")
                      "sleepDurationHours": horas, (Por ejemplo si durmió 7h 45m es 7)
                      "sleepDurationMinutes": minutos, (Ej: 45)
                      "awakeHours": horas, (Tiempo despierto/vigilia, por ejemplo 0)
                      "awakeMinutes": minutos, (Por ejemplo, 35)
                      "remHours": horas, (Tiempo REM, ej: 1)
                      "remMinutes": minutos, (Ej: 20)
                      "essentialHours": horas, (Tiempo esencial/ligero, ej: 4)
                      "essentialMinutes": minutos, (Ej: 10)
                      "deepHours": horas, (Tiempo profundo, ej: 1)
                      "deepMinutes": minutos, (Ej: 40)
                      "awakePercentage": porcentaje, (Porcentaje de vigilia, ej: 7.5; ingresa sólo números)
                      "remPercentage": porcentaje, (Porcentaje de REM, ej: 17.0)
                      "essentialPercentage": porcentaje, (Porcentaje de esencial/ligero, ej: 52.5)
                      "deepPercentage": porcentaje (Porcentaje de profundo, ej: 23.0)
                    }
                    
                    REGLAS ADICIONALES:
                    - Devuelve EXCLUSIVAMENTE el objeto JSON sin envolver en bloques de código markdown, sin palabras como ```json y sin explicaciones externas para ser parsed de forma inmediata y directa.
                    - Si falta algún dato de fase o duración pero se deducen a nivel general, calcúlalos o repórtalos como 0 si no son visibles.
                    - No inventes números aleatorios que no existan en el medio.
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = extractionPrompt),
                                Part(inlineData = InlineData(mimeType = mimeType, data = base64Data))
                            )
                        )
                    ),
                    generationConfig = GenerationConfig(responseMimeType = "application/json")
                )

                val response = GeminiClient.generateContent(getGeminiApiKey(), request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (responseText != null) {
                    val cleanedJson = cleanJsonBlock(responseText)
                    val extracted = GeminiClient.jsonConfig.decodeFromString<SleepExtractedData>(cleanedJson)

                    withContext(Dispatchers.Main) {
                        if (extracted != null) {
                            _analyzedResult.value = extracted
                        } else {
                            _analysisError.value = "No se pudieron decodificar los datos extraídos por la IA."
                        }
                        _isAnalyzingMedia.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _analysisError.value = "La IA respondió con un mensaje vacío."
                        _isAnalyzingMedia.value = false
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _analysisError.value = "Error al comunicarse con la IA: ${e.message}"
                    _isAnalyzingMedia.value = false
                }
            }
        }
    }

    /**
     * Limpia envolturas markdown del JSON si son devueltas a pesar de las instrucciones.
     */
    private fun cleanJsonBlock(rawText: String): String {
        var text = rawText.trim()
        if (text.startsWith("```json")) {
            text = text.substring(7)
        } else if (text.startsWith("```")) {
            text = text.substring(3)
        }
        if (text.endsWith("```")) {
            text = text.substring(0, text.length - 3)
        }
        return text.trim()
    }

    /**
     * Borra el historial de extracción temporal del UI
     */
    fun clearExtractedResult() {
        _analyzedResult.value = null
        _analysisError.value = null
    }

    fun startHealthSync() {
        _isSyncingHealth.value = true
        _analysisError.value = null
        _analyzedResult.value = null
    }

    fun completeHealthSync(data: SleepExtractedData) {
        _analyzedResult.value = data
        _isSyncingHealth.value = false
    }

    fun failHealthSync(errorMsg: String) {
        _analysisError.value = errorMsg
        _isSyncingHealth.value = false
    }

    fun setAnalyzedResult(data: SleepExtractedData?) {
        _analyzedResult.value = data
    }

    // --- Chat Logic ---

    fun clearChat() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.clearChatHistory()
            } catch (e: Exception) {
                println("SleepViewModel: Error clearing chat history: ${e.message}")
            }
        }
    }

    /**
     * Envía una pregunta o instrucción al chat analítico del sueño, inyectando historial reciente y protegiendo de fármacos.
     */
    fun sendUserMessage(messageText: String) {
        if (messageText.isBlank()) return

        _isGeneratingResponse.value = true
        _chatError.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Sanitizar localmente cualquier mención de medicación en la pregunta para NUNCA guardarla en BD
                val sanitization = MedicationSanitizer.sanitize(messageText)

                // 2. Guardar mensaje de usuario ya protegido en la base de datos local
                repository.insertChatMessage(
                    ChatMessage(sender = "user", text = sanitization.sanitizedText)
                )

                // 3. Recuperar el historial reciente de chats sanitizados
                val dbHistory = repository.getAllMessagesList()

                // 4. Formatear el historial de logs de sueño recientes de la base de datos para darle contexto sobre el patrón de sueño real
                val currentLogs = allSleepEntries.value
                val sleepHistoryContext = formatSleepLogsForAi(currentLogs)

                // 5. Mapear el chat persistido al formato de historiales de Gemini
                val contentsList = mutableListOf<Content>()

                // Primero, inyectamos el contexto de logs de sueño y las instrucciones como turnos o introducciones
                val contextIntroPrompt = """
                    INFORMACIÓN DE CONTEXTO REAL SOBRE EL SUEÑO DEL USUARIO:
                    $sleepHistoryContext
                    
                    Por favor, usa esta información para responder a las preguntas y resolver sus dudas con sumas bases de datos del usuario.
                """.trimIndent()

                // Creamos los contenidos para el chat
                contentsList.add(
                    Content(role = "user", parts = listOf(Part(text = contextIntroPrompt)))
                )
                contentsList.add(
                    Content(role = "model", parts = listOf(Part(text = "Entendido. He analizado todos tus datos recientes de fases de sueño, vigilia, coberturas de descanso y registros de medicamentos. Estoy listo para ayudarte a interpretar tu descanso en español sin inventar datos y cuidando de tu privacidad.")))
                )

                // Mapeamos los mensajes de chat reales en la bd
                dbHistory.forEach { chatMsg ->
                    contentsList.add(
                        Content(
                            role = if (chatMsg.sender == "user") "user" else "model",
                            parts = listOf(Part(text = chatMsg.text))
                        )
                    )
                }

                // Generamos System Instructions para asegurar un tono científico, preventivo y libre de alucinaciones
                val systemInstruction = Content(
                    parts = listOf(
                        Part(
                            text = """
                            Eres "Scribe de Sueño", un somnólogo digital experto y consejero científico del sueño.
                            Estás diseñado para interpretar las gráficas y estadísticas de sueño de relojes inteligentes y orientar al usuario en su descanso diario.
                            
                            REGLAS DE COMPORTAMIENTO MANDATORIAS:
                            1. RESPONDE SIEMPRE EN ESPAÑOL.
                            2. NO INVENTES NI ALUCINES DATOS. Si el usuario te hace una pregunta pero te faltan datos específicos de su rutina o historiales, debes PREGUNTARLE cordialmente en vez de dar por sentado los números (Ejemplo exacto: "¿A qué horas de la noche has estado en vigilia? ¿Recuerdas si ha sido porque te has despertado para ir al baño o beber agua?").
                            3. POLÍTICA DE PRIVACIDAD DE MEDICAMENTOS: Si el usuario menciona medicamentos en sus prompts (por ejemplo ansiolíticos, melatonina, medicamentos con receta o dosis), NUNCA expongas en tus respuestas finales ni repitas los nombres comerciales o principios activos de dichos fármacos. En tu lugar, refiérete siempre a ellos usando la leyenda genérica "[Información Sensible: Medicación de Sueño Detectada y Asegurada]" y notifica explícitamente al usuario que has registrado confidencialmente el recordatorio médico manteniendo un estricto resguardo sobre los nombres exactos debido a políticas de privacidad y seguridad digital.
                            4. Brinda consejos prácticos basados en higiene del sueño, exposición lumínica diurna/nocturna, ingesta de cafeína, actividad física y ritmos circadianos. No recetes ni des tratamientos clínicos invasivos.
                            """.trimIndent()
                        )
                    )
                )

                val request = GenerateContentRequest(
                    contents = contentsList,
                    systemInstruction = systemInstruction
                )

                val response = GeminiClient.generateContent(getGeminiApiKey(), request)
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                if (replyText != null) {
                    repository.insertChatMessage(
                        ChatMessage(sender = "model", text = replyText)
                    )
                } else {
                    withContext(Dispatchers.Main) {
                        _chatError.value = "Error: Respuesta de IA nula"
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _chatError.value = e.message ?: "Error desconocido al procesar el chat."
                }
            } finally {
                withContext(Dispatchers.Main) {
                    _isGeneratingResponse.value = false
                }
            }
        }
    }

    /**
     * Convierte el historial en formato string legible de apoyo contextual.
     */
    private fun formatSleepLogsForAi(entries: List<SleepEntry>): String {
        if (entries.isEmpty()) return "El usuario no ha registrado noches de sueño recientes todavía en la base de datos."
        val sb = StringBuilder()
        sb.append("Historial de noches registradas:\n")
        entries.take(15).forEach { entry ->
            sb.append("- Fecha: ${entry.dateString} | Duración de Sueño: ${entry.sleepDurationHours}h ${entry.sleepDurationMinutes}m | Horarios: Acostarse ${entry.bedtime} - Despertar ${entry.wakeupTime}\n")
            sb.append("  Fases -> Vigilia/Despierto: ${entry.awakeHours}h ${entry.awakeMinutes}m (${entry.awakePercentage}%), REM: ${entry.remHours}h ${entry.remMinutes}m (${entry.remPercentage}%), Esencial/Ligero: ${entry.essentialHours}h ${entry.essentialMinutes}m (${entry.essentialPercentage}%), Profundo: ${entry.deepHours}h ${entry.deepMinutes}m (${entry.deepPercentage}%)\n")
            if (entry.medicationLogged) {
                sb.append("  Medicación: [Dosis de medicación de sueño indicada y guardada de forma segura]\n")
            }
            if (entry.notes.isNotBlank()) {
                sb.append("  Notas: ${entry.notes}\n")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

/**
 * Factory simple para instanciar el ViewModel sin inyecciones complejas.
 */
fun sleepViewModelFactory(
    repository: SleepRepository,
    settings: Settings
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        SleepViewModel(repository, settings)
    }
}
