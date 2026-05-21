package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.SleepViewModel
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSleepEntryScreen(
    viewModel: SleepViewModel,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados observados del ViewModel
    val isAnalyzingMedia by viewModel.isAnalyzingMedia.collectAsState()
    val analyzedResult by viewModel.analyzedResult.collectAsState()
    val analysisError by viewModel.analysisError.collectAsState()

    // Formulario de entrada
    var dateString by remember { mutableStateOf(com.example.util.getCurrentFormattedDate()) }
    var bedtime by remember { mutableStateOf("23:15") }
    var wakeupTime by remember { mutableStateOf("07:30") }

    // Horas y minutos de cada fase
    var awakeH by remember { mutableStateOf("0") }
    var awakeM by remember { mutableStateOf("30") }
    var remH by remember { mutableStateOf("1") }
    var remM by remember { mutableStateOf("45") }
    var essentialH by remember { mutableStateOf("4") }
    var essentialM by remember { mutableStateOf("30") }
    var deepH by remember { mutableStateOf("1") }
    var deepM by remember { mutableStateOf("15") }

    // Toggle de medicación y notas generales
    var mentionsMedication by remember { mutableStateOf(false) }
    var notesText by remember { mutableStateOf("") }

    // Estados para el Juego de Percepción de Puntuación (M3 styling)
    var showGuessGameStep by remember { mutableStateOf(false) }
    var gameStep by remember { mutableStateOf(1) } // 1: Estimación, 2: Revelación y Clasificación
    var guessedScoreInput by remember { mutableStateOf(75) }
    var skippedGuess by remember { mutableStateOf(false) }
    var leaderboardOptIn by remember { mutableStateOf(true) }
    var leaderboardNickname by remember { mutableStateOf("") }

    // Si la IA nos extrajo datos, los cargamos automáticamente en el formulario
    LaunchedEffect(analyzedResult) {
        analyzedResult?.let { result ->
            bedtime = result.bedtime ?: bedtime
            wakeupTime = result.wakeupTime ?: wakeupTime
            awakeH = (result.awakeHours ?: 0).toString()
            awakeM = (result.awakeMinutes ?: 0).toString()
            remH = (result.remHours ?: 0).toString()
            remM = (result.remMinutes ?: 0).toString()
            essentialH = (result.essentialHours ?: 0).toString()
            essentialM = (result.essentialMinutes ?: 0).toString()
            deepH = (result.deepHours ?: 0).toString()
            deepM = (result.deepMinutes ?: 0).toString()
            notesText = "Datos extraídos automáticamente mediante IA."
        }
    }

    // Calculadoras dinámicas
    val awakeMin = (awakeH.toIntOrNull() ?: 0) * 60 + (awakeM.toIntOrNull() ?: 0)
    val remMin = (remH.toIntOrNull() ?: 0) * 60 + (remM.toIntOrNull() ?: 0)
    val essentialMin = (essentialH.toIntOrNull() ?: 0) * 60 + (essentialM.toIntOrNull() ?: 0)
    val deepMin = (deepH.toIntOrNull() ?: 0) * 60 + (deepM.toIntOrNull() ?: 0)

    val totalMin = awakeMin + remMin + essentialMin + deepMin
    val sleepMin = remMin + essentialMin + deepMin // El sueño neto (REM + Ligero + Profundo, excluyendo despertar nocturno)

    val sleepHoursResult = sleepMin / 60
    val sleepMinutesResult = sleepMin % 60

    val awakePct = if (totalMin > 0) (awakeMin.toFloat() / totalMin * 100) else 0f
    val remPct = if (totalMin > 0) (remMin.toFloat() / totalMin * 100) else 0f
    val essentialPct = if (totalMin > 0) (essentialMin.toFloat() / totalMin * 100) else 0f
    val deepPct = if (totalMin > 0) (deepMin.toFloat() / totalMin * 100) else 0f

    // Lanzadores de media (vídeo y foto)
    val mediaPicker = rememberPlatformMediaPicker { bytes, mimeType ->
        viewModel.extractSleepDataFromMedia(bytes, mimeType)
    }

    if (showGuessGameStep) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ENCABEZADO GEOMETRIC
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Juego",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Juego de Puntuación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "COMPARA TU INTUICIÓN CON LA CIENCIA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color(0xFF44474E)
                        )
                    }
                }
            }

            if (gameStep == 1) {
                // PASO 1: ESTIMAR LA PUNTUACIÓN DE SUEÑO
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFE1E2EC))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = "¿Qué puntuación crees que obtuviste?",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Selecciona de 10 a 100 el puntaje que crees que calcula el sistema analítico de Somnos AI para tu noche de sueño basada en tus fases y duración.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        // Score Visual Big Badge
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(32.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$guessedScoreInput",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Slider
                        Slider(
                            value = guessedScoreInput.toFloat(),
                            onValueChange = { guessedScoreInput = it.toInt() },
                            valueRange = 10f..100f,
                            steps = 90,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .testTag("guess_score_slider")
                        )

                        // Precision Adjusters
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { guessedScoreInput = (guessedScoreInput - 5).coerceAtLeast(10) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("-5 pts", fontWeight = FontWeight.Bold)
                            }
                            FilledTonalButton(
                                onClick = { guessedScoreInput = (guessedScoreInput + 5).coerceAtMost(100) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+5 pts", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                skippedGuess = false
                                gameStep = 2
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("confirm_guess_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Celebration, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("¡Revelar Mi Puntuación Real!", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                skippedGuess = true
                                gameStep = 2
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("skip_guess_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Omitir juego y guardar directo", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                // PASO 2: REVELACIÓN DEL CÁLCULO CIENTÍFICO + LEADERBOARD OPT-IN
                val calculatedScore = viewModel.calculateSleepScore(
                    sleepHours = sleepHoursResult,
                    sleepMinutes = sleepMinutesResult,
                    awakePct = awakePct,
                    remPct = remPct,
                    deepPct = deepPct
                )

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color(0xFFE1E2EC))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "Puntuaciones del Descanso",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "TU ESTIMACIÓN",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (skippedGuess) "--" else "$guessedScoreInput",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CompareArrows,
                                contentDescription = "comparación",
                                tint = Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "CÁLCULO REAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "$calculatedScore",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Retroalimentación de la percepción
                        val explanation = remember(calculatedScore, guessedScoreInput, skippedGuess) {
                            if (skippedGuess) {
                                "Calculamos que tu puntuación real de descanso fue de $calculatedScore/100, enfocada principalmente en las proporciones de sueño profundo y duraciones biológicas totales correspondientes."
                            } else {
                                val diff = kotlin.math.abs(calculatedScore - guessedScoreInput)
                                when {
                                    diff == 0 -> "🔮 ¡Increíble! Tu percepción es impecable. Tienes una conexión perfecta con tus ritmos biológicos y necesidades de descanso."
                                    diff <= 5 -> "🦉 ¡Casi perfecto! Sincronización magnífica entre tu autopercepción y las métricas científicas de tu reloj smart."
                                    diff <= 12 -> "⚖️ ¡Muy buen intento! Tu percepción coincide bastante bien con los microsensores metabólicos de tu descanso."
                                    else -> "🎢 ¡Curiosidad! Existe un desfase de $diff puntos entre cómo percibes el sueño y los datos fisiológicos analizados de forma computacional."
                                }
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }

                        Divider(color = Color(0xFFE1E2EC))

                        // CLASIFICACIÓN PÚBLICA / RANKING (HUELLA ANONIMA)
                        Text(
                            text = "🏆 Clasificación Global",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Acepta entrar al ranking público descentralizado con amigos y la comunidad. Solo se compartirá tu apodo auto-generado o ingresado junto a la puntuación calculada de tu sueño, manteniendo a salvo tus notas de salud.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Unirse al Ranking Público",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Switch(
                                checked = leaderboardOptIn,
                                onCheckedChange = { leaderboardOptIn = it },
                                modifier = Modifier.testTag("leaderboard_switch")
                            )
                        }

                        AnimatedVisibility(visible = leaderboardOptIn) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = leaderboardNickname,
                                    onValueChange = { leaderboardNickname = it },
                                    label = { Text("Tu Apodo para el Ranking") },
                                    placeholder = { Text("Ej: Oso_Perezoso_72") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("leaderboard_nickname_input")
                                )

                                Button(
                                    onClick = {
                                        val cuteAnimals = listOf(
                                            "Koala_Estrella", "Marmota_Gamer", "Gatito_Zzz", "León_Soñador",
                                            "Búho_Tranquilo", "Oso_Perezoso", "Zorro_Nocturno", "Erizo_Dormilón"
                                        )
                                        val randomAnimal = cuteAnimals.random()
                                        val randomNum = (10..99).random()
                                        leaderboardNickname = "${randomAnimal}_$randomNum"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Generar Apodo Divertido", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // BOTÓN GUARDAR FINAL
                        Button(
                            onClick = {
                                val nicknameToSave = if (leaderboardOptIn) {
                                    if (leaderboardNickname.isBlank()) "Dormilón_Anónimo" else leaderboardNickname
                                } else ""

                                viewModel.saveSleepEntry(
                                    dateString = dateString,
                                    bedtime = bedtime,
                                    wakeupTime = wakeupTime,
                                    sleepHours = sleepHoursResult,
                                    sleepMinutes = sleepMinutesResult,
                                    awakeHours = awakeH.toIntOrNull() ?: 0,
                                    awakeMinutes = awakeM.toIntOrNull() ?: 0,
                                    remHours = remH.toIntOrNull() ?: 0,
                                    remMinutes = remM.toIntOrNull() ?: 0,
                                    essentialHours = essentialH.toIntOrNull() ?: 0,
                                    essentialMinutes = essentialM.toIntOrNull() ?: 0,
                                    deepHours = deepH.toIntOrNull() ?: 0,
                                    deepMinutes = deepM.toIntOrNull() ?: 0,
                                    awakePct = awakePct,
                                    remPct = remPct,
                                    essentialPct = essentialPct,
                                    deepPct = deepPct,
                                    hasMedicationToggle = mentionsMedication,
                                    notesText = notesText,
                                    guessedScore = if (skippedGuess) null else guessedScoreInput,
                                    leaderboardOptIn = leaderboardOptIn,
                                    leaderboardNickname = nicknameToSave
                                )

                                // Limpiar IA
                                viewModel.clearExtractedResult()

                                // Completar
                                onSuccess()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("save_game_results_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Guardar y Publicar Resultados", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        TextButton(
                            onClick = {
                                showGuessGameStep = false
                                gameStep = 1
                            }
                        ) {
                            Text("Regresar al formulario")
                        }
                    }
                }
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título de la sección
            Text(
                text = "Registrar Noche de Sueño",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // PANEL 1: Extracción inteligente mediante IA de Gemini
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "IA",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Extracción Inteligente por IA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Text(
                        text = "Graba la pantalla de tu móvil o toma una captura en la app de tu smartwatch (Apple, Garmin, Fitbit, Samsung), súbelo aquí y Gemini extraerá las métricas automáticamente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { mediaPicker.launch("video/*") },
                            enabled = !isAnalyzingMedia,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_video_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.VideoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Subir Vídeo", fontSize = 13.sp)
                        }

                        FilledTonalButton(
                            onClick = { mediaPicker.launch("image/*") },
                            enabled = !isAnalyzingMedia,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_image_button")
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Subir Captura", fontSize = 13.sp)
                        }
                    }

                    // Indicador de análisis con IA
                    if (isAnalyzingMedia) {
                        Spacer(Modifier.height(4.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(strokeWidth = 3.dp)
                            Text(
                                text = "Analizando vídeo/captura con la IA de Gemini...",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Error en análisis
                    analysisError?.let { err ->
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                                Text(
                                    text = err,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Éxito en análisis
                    if (analyzedResult != null) {
                        Spacer(Modifier.height(4.dp))
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(8.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Completado", tint = Color(0xFF2E7D32))
                                Text(
                                    text = "¡Datos extraídos con éxito! Revisa los campos y presiona 'Guardar' abajo.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF1B5E20),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // PANEL 2: Formulario Manuel de Registro
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Datos de Registro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Campo Fecha
                    OutlinedTextField(
                        value = dateString,
                        onValueChange = { dateString = it },
                        label = { Text("Fecha de la noche (AAAA-MM-DD)") },
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Grid de Horarios (Bedtime & Wakeuptime)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = bedtime,
                            onValueChange = { bedtime = it },
                            label = { Text("Acostado (HH:MM)") },
                            leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("bedtime_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = wakeupTime,
                            onValueChange = { wakeupTime = it },
                            label = { Text("Despertado (HH:MM)") },
                            leadingIcon = { Icon(Icons.Default.WbSunny, contentDescription = null) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("wakeup_input"),
                            singleLine = true
                        )
                    }

                    Divider()

                    // Sección De Fases de Sueño
                    Text(
                        text = "Fases de Sueño (Smartwatch)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // FASE 1: Vigilia (Awake)
                    PhaseInputRow(
                        label = "Vigilia / Despierto",
                        colorIndicator = Color(0xFFE57373),
                        hoursValue = awakeH,
                        minutesValue = awakeM,
                        onHoursChange = { awakeH = it },
                        onMinutesChange = { awakeM = it },
                        calculatedPct = awakePct
                    )

                    // FASE 2: REM
                    PhaseInputRow(
                        label = "Fase REM",
                        colorIndicator = Color(0xFF9575CD),
                        hoursValue = remH,
                        minutesValue = remM,
                        onHoursChange = { remH = it },
                        onMinutesChange = { remM = it },
                        calculatedPct = remPct
                    )

                    // FASE 3: Esencial / Ligero
                    PhaseInputRow(
                        label = "Sueño Esencial / Ligero",
                        colorIndicator = Color(0xFF4FC3F7),
                        hoursValue = essentialH,
                        minutesValue = essentialM,
                        onHoursChange = { essentialH = it },
                        onMinutesChange = { essentialM = it },
                        calculatedPct = essentialPct
                    )

                    // FASE 4: Profundo
                    PhaseInputRow(
                        label = "Sueño Profundo",
                        colorIndicator = Color(0xFF3F51B5),
                        hoursValue = deepH,
                        minutesValue = deepM,
                        onHoursChange = { deepH = it },
                        onMinutesChange = { deepM = it },
                        calculatedPct = deepPct
                    )

                    Divider()

                    // Resumen Calculado
                    Surface(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tiempo neto de sueño calculado:",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${sleepHoursResult}h ${sleepMinutesResult}m",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Divider()

                    // PRIVACIDAD MÉDICA Y MEDICACIÓN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(0.85f)) {
                            Text(
                                text = "¿Tomó medicación antes de dormir?",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Por privacidad, el nombre exacto de la medicación NUNCA se guardará.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = mentionsMedication,
                            onCheckedChange = { mentionsMedication = it },
                            modifier = Modifier.testTag("medication_switch")
                        )
                    }

                    // Campo Notas / Comentarios
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Notas de la noche (Ej: cafeína tarde, ejercicio, estrés)") },
                        placeholder = { Text("Si ingresas algún fármaco o dosis aquí, el sistema lo redactará automáticamente por tu privacidad.") },
                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("notes_input"),
                        maxLines = 3
                    )

                    // Aviso de Privacidad Adicional
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrivacyTip,
                                contentDescription = "Privacidad",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Privacidad encriptada localmente: Si escribes nombres como 'Lorazepam', 'Zolpidem', 'Valium' o dosis en miligramos en este formulario o en el chat, se filtrarán localmente. En su lugar se guardará como '[Información Sensible Asegurada]' guardando únicamente el recordatorio seguro.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botón Enviar / Guardar (Redirigido al juego de estimación primero)
                    Button(
                        onClick = {
                            showGuessGameStep = true
                            gameStep = 1
                            guessedScoreInput = 75
                            skippedGuess = false
                            leaderboardNickname = ""
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_sleep_entry_button")
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Siguiente: Juego de Percepción", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseInputRow(
    label: String,
    colorIndicator: Color,
    hoursValue: String,
    minutesValue: String,
    onHoursChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    calculatedPct: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Indicador de color flotante
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(colorIndicator)
        )

        // Nombre de la fase
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(130.dp)
        )

        // Campo Horas
        OutlinedTextField(
            value = hoursValue,
            onValueChange = onHoursChange,
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(60.dp)
                .height(48.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Text("h", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        // Campo Minutos
        OutlinedTextField(
            value = minutesValue,
            onValueChange = onMinutesChange,
            placeholder = { Text("0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(60.dp)
                .height(48.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, textAlign = TextAlign.Center),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )

        Text("m", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(Modifier.weight(1f))

        // Porcentaje auto-calculado
        Surface(
            color = colorIndicator.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "${(calculatedPct * 10).roundToInt() / 10.0}%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorIndicator,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

