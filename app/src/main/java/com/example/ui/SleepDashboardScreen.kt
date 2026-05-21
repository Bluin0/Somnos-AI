package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SleepEntry
import com.example.viewmodel.SleepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepDashboardScreen(
    viewModel: SleepViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sleepEntries by viewModel.allSleepEntries.collectAsState()
    val selectedRetention by viewModel.selectedRetention.collectAsState()

    var showDeleteConfirmDialog by remember { mutableStateOf<SleepEntry?>(null) }
    var activeTab by remember { mutableStateOf(0) } // 0: Métricas, 1: Ranking Público

    // Calcular las entradas que el usuario optó por compartir con la comunidad
    val optInEntries = remember(sleepEntries) {
        sleepEntries.filter { it.leaderboardOptIn && it.leaderboardNickname.isNotBlank() }
    }

    // Datos simulados de amigos y otros integrantes públicos de la comunidad para gamificación amigable
    val mockCompetitors = remember {
        listOf(
            SleepEntry(id = -1, dateString = "Hoy", bedtime = "23:00", wakeupTime = "07:15",
                sleepDurationHours = 8, sleepDurationMinutes = 15, awakeHours = 0, awakeMinutes = 15,
                remHours = 2, remMinutes = 0, essentialHours = 4, essentialMinutes = 30, deepHours = 1, deepMinutes = 45,
                awakePercentage = 3f, remPercentage = 22f, essentialPercentage = 52f, deepPercentage = 23f,
                medicationLogged = false, notes = "", sleepScore = 94, guessedScore = 90,
                leaderboardOptIn = true, leaderboardNickname = "Marmota_Sideral"
            ),
            SleepEntry(id = -2, dateString = "Hoy", bedtime = "22:15", wakeupTime = "06:00",
                sleepDurationHours = 7, sleepDurationMinutes = 45, awakeHours = 0, awakeMinutes = 40,
                remHours = 1, remMinutes = 40, essentialHours = 4, essentialMinutes = 15, deepHours = 1, deepMinutes = 50,
                awakePercentage = 7f, remPercentage = 21f, essentialPercentage = 48f, deepPercentage = 24f,
                medicationLogged = false, notes = "", sleepScore = 88, guessedScore = 85,
                leaderboardOptIn = true, leaderboardNickname = "Koala_Dormilón"
            ),
            SleepEntry(id = -3, dateString = "Hoy", bedtime = "23:45", wakeupTime = "07:15",
                sleepDurationHours = 7, sleepDurationMinutes = 30, awakeHours = 0, awakeMinutes = 50,
                remHours = 1, remMinutes = 30, essentialHours = 4, essentialMinutes = 0, deepHours = 1, deepMinutes = 20,
                awakePercentage = 9f, remPercentage = 19f, essentialPercentage = 50f, deepPercentage = 22f,
                medicationLogged = false, notes = "", sleepScore = 81, guessedScore = 80,
                leaderboardOptIn = true, leaderboardNickname = "Erizo_Veloz_Zzz"
            ),
            SleepEntry(id = -4, dateString = "Hoy", bedtime = "23:15", wakeupTime = "06:00",
                sleepDurationHours = 6, sleepDurationMinutes = 45, awakeHours = 1, awakeMinutes = 10,
                remHours = 1, remMinutes = 10, essentialHours = 3, essentialMinutes = 45, deepHours = 1, deepMinutes = 0,
                awakePercentage = 15f, remPercentage = 15f, essentialPercentage = 55f, deepPercentage = 15f,
                medicationLogged = false, notes = "", sleepScore = 67, guessedScore = 75,
                leaderboardOptIn = true, leaderboardNickname = "Búho_Desvelado"
            )
        )
    }

    // Combinar, eliminar duplicados por nickname si fuera el caso, y ordenar
    val rankingList = remember(optInEntries) {
        val combined = (optInEntries + mockCompetitors).distinctBy { it.leaderboardNickname.lowercase() }
        combined.sortedByDescending { it.sleepScore }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ENCABEZADO GEOMETRIC BALANCE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Somnos AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "POWERED BY GEMINI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF44474E)
                    )
                }
            }

            IconButton(
                onClick = { /* Decorativo */ },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // SEGMENTED TAB SELECTOR (Geometric Balance)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFFF3F3FA), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "Mis Métricas" to Icons.Default.BarChart,
                "Ranking Público" to Icons.Default.Leaderboard
            ).forEachIndexed { index, (label, icon) ->
                val isSelected = activeTab == index
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { activeTab = index }
                        .padding(vertical = 12.dp)
                        .testTag("dashboard_tab_$index"),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (activeTab == 0) {
            // LISTA 1: MIS MÉTRICAS PERSONALES
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // SECCIÓN: Configuración de Retención de Datos
                item {
                    RetentionConfigCard(
                        currentSetting = selectedRetention,
                        onSettingSelected = { viewModel.updateRetentionSetting(it) }
                    )
                }

                // Si no hay registros
                if (sleepEntries.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Bedtime,
                                        contentDescription = null,
                                        modifier = Modifier.size(36.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Text(
                                text = "Sin registros de sueño",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aún no has guardado ninguna noche. Dirígete a la pestaña de 'Registrar' para ingresar tus datos de forma manual o utilizando la subida inteligente de vídeo.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                } else {
                    // SECCIÓN: Última noche de sueño registrada (Geometric Balance style)
                    val latestEntry = sleepEntries.first()
                    item {
                        LatestNightSummaryCard(entry = latestEntry)
                    }

                    // NUEVA SECCIÓN: Gemini Analyst Advisory Card
                    item {
                        GeminiAdvisoryCard(entry = latestEntry, onNavigateToChat = onNavigateToChat)
                    }

                    // Dotted Area decorative
                    item {
                        DashboardUploadDottedBanner()
                    }

                    // Título: Historial anterior
                    if (sleepEntries.size > 1) {
                        item {
                            Text(
                                text = "Historial Reciente",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        // Lista de registros anteriores
                        items(sleepEntries.drop(1)) { entry ->
                            RecentSleepEntryRow(
                                entry = entry,
                                onDeleteClick = { showDeleteConfirmDialog = entry }
                            )
                        }
                    }
                }
            }
        } else {
            // LISTA 2: RANKING / CLASIFICACIÓN PÚBLICA (HUELLA DE JUEGO)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Tarjeta promocional explicativa del juego
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Liga del Sueño Somnos AI",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "Compite de forma amigable con amigos y peers globales. Registra tu noche, adivina tu puntuación y lánzate a liderar el podio de descanso.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                // Listado de competidores en el podio
                itemsIndexed(rankingList) { index, entry ->
                    val isCurrentUser = entry.id >= 0
                    val rankNum = index + 1

                    val cardBorder = if (isCurrentUser) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, Color(0xFFE1E2EC))
                    }

                    val cardBackground = if (isCurrentUser) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                    } else {
                        Color.White
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBackground),
                        shape = RoundedCornerShape(16.dp),
                        border = cardBorder
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                // Indicador de Rango con medallero para el Top 3
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (rankNum) {
                                                1 -> Color(0xFFFFD700) // Oro
                                                2 -> Color(0xFFC0C0C0) // Plata
                                                3 -> Color(0xFFCD7F32) // Bronce
                                                else -> Color(0xFFF3F3FA)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$rankNum",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (rankNum <= 3) Color(0xFF191C1E) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Avatar circular con la inicial
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(
                                            if (isCurrentUser) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = entry.leaderboardNickname.take(1).uppercase(),
                                        fontWeight = FontWeight.Black,
                                        color = if (isCurrentUser) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                                        fontSize = 15.sp
                                    )
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = entry.leaderboardNickname,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        if (isCurrentUser) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.primary)
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "TÚ",
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "Percepción de sueño: ${if (entry.guessedScore == null) "Omitida" else "${entry.guessedScore} pts"}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Badge de puntuación calculada
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${entry.sleepScore} pts",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO CONFIRMACIÒN ELIMINACIÓN
    showDeleteConfirmDialog?.let { entry ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Eliminar Registro") },
            text = { Text("¿Estás seguro de que quieres eliminar el registro de sueño de la fecha ${entry.dateString}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSleepEntry(entry.id)
                        showDeleteConfirmDialog = null
                    },
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Tarjeta para cambiar el periodo de almacenamiento de registros.
 */
@Composable
fun RetentionConfigCard(
    currentSetting: String,
    onSettingSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE1E2EC))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Retención de Registros",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Para asegurar la privacidad total, define cuánto persistir tus registros en este dispositivo antes de ser purgados de forma permanente.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Fila de botones segmentados de retención
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "1_week" to "1 Sem",
                    "2_weeks" to "2 Sem",
                    "1_month" to "1 Mes",
                    "indefinite" to "Siempre"
                ).forEach { (policy, label) ->
                    val isSelected = currentSetting == policy
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else Color(0xFFF3F3FA)
                            )
                            .clickable { onSettingSelected(policy) }
                            .padding(vertical = 10.dp)
                            .testTag("retention_btn_$policy"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Panel de Resumen Principal (última noche).
 */
@Composable
fun LatestNightSummaryCard(entry: SleepEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE1E2EC)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Última Noche",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191C1E)
                    )
                    Text(
                        text = entry.dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                // Badge de tiempo en grande
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE8DEF8))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${entry.sleepDurationHours}h ${entry.sleepDurationMinutes}m",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D192B)
                    )
                }
            }

            // Grid 2x2 para las métricas de fases
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Fila 1: Vigilia & REM
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhaseGridItem(
                        label = "VIGILIA",
                        dotColor = Color(0xFFB3261E),
                        durationText = formatHoursMinutes(entry.awakeHours, entry.awakeMinutes),
                        percentageText = "${String.format("%.0f", entry.awakePercentage)}%",
                        modifier = Modifier.weight(1f)
                    )
                    PhaseGridItem(
                        label = "REM",
                        dotColor = Color(0xFF6750A4),
                        durationText = formatHoursMinutes(entry.remHours, entry.remMinutes),
                        percentageText = "${String.format("%.0f", entry.remPercentage)}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Fila 2: Esencial & Profundo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhaseGridItem(
                        label = "ESENCIAL",
                        dotColor = Color(0xFF3F51B5),
                        durationText = formatHoursMinutes(entry.essentialHours, entry.essentialMinutes),
                        percentageText = "${String.format("%.0f", entry.essentialPercentage)}%",
                        modifier = Modifier.weight(1f)
                    )
                    PhaseGridItem(
                        label = "PROFUNDO",
                        dotColor = Color(0xFF001E2F),
                        durationText = formatHoursMinutes(entry.deepHours, entry.deepMinutes),
                        percentageText = "${String.format("%.0f", entry.deepPercentage)}%",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Horas acostado y levantado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Acostado: " + entry.bedtime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF44474E)
                )
                Text(
                    text = "Despertado: " + entry.wakeupTime,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF44474E)
                )
            }

            // Datos de Privacidad o anotaciones adicionales
            if (entry.medicationLogged || entry.notes.isNotBlank()) {
                HorizontalDivider(color = Color(0xFFE1E2EC))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (entry.medicationLogged) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color(0xFF3F51B5),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Medicación bajo encriptación activa de seguridad.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF3F51B5)
                            )
                        }
                    }

                    if (entry.notes.isNotBlank()) {
                        Text(
                            text = "Anotaciones:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = entry.notes,
                            fontSize = 12.sp,
                            color = Color(0xFF44474E)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseGridItem(
    label: String,
    dotColor: Color,
    durationText: String,
    percentageText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3FA))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(dotColor)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF44474E)
                )
            }
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = durationText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191C1E)
                )
                Text(
                    text = percentageText,
                    fontSize = 11.sp,
                    color = Color(0xFF191C1E).copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 1.dp)
                )
            }
        }
    }
}

@Composable
fun GeminiAdvisoryCard(entry: SleepEntry, onNavigateToChat: () -> Unit) {
    val adviceText = remember(entry) {
        when {
            entry.deepPercentage < 15f -> {
                "He notado un déficit en tu fase de sueño profundo (${String.format("%.1f", entry.deepPercentage)}%). ¿Consumiste cafeína tarde por la noche o realizaste ejercicio de alta intensidad antes de acostarte?"
            }
            entry.awakePercentage > 10f -> {
                "He detectado picos de vigilia inusuales durante la noche. ¿Recuerdas si fue por deshidratación, algún ruido externo o molestias corporales?"
            }
            else -> {
                "¡Excelente calidad de descanso! Tus ciclos de sueño REM e individualizado profundo reflejan una buena sincronización y excelente higiene nocturna."
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToChat() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD3E4FF)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFBAC7DB))
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
                    imageVector = Icons.Default.ChatBubble,
                    contentDescription = null,
                    tint = Color(0xFF001D36),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Analista Scribe AI",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001D36)
                )
            }

            Surface(
                color = Color.White.copy(alpha = 0.82f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = adviceText,
                    fontSize = 13.sp,
                    color = Color(0xFF191C1E),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onNavigateToChat() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF001D36),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Responder chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFBAC7DB), RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable { onNavigateToChat() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Videollamada",
                        tint = Color(0xFF001D36),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardUploadDottedBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(1.5.dp, Color(0xFFC4C6CF)),
                shape = RoundedCornerShape(16.dp)
            )
            .background(Color.Transparent)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = null,
                tint = Color(0xFF44474E),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "SUBIR GRABACIÓN DE PANTALLA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = Color(0xFF44474E)
            )
        }
    }
}

/**
 * Renglón de elemento histórico reciente.
 */
@Composable
fun RecentSleepEntryRow(
    entry: SleepEntry,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFFE1E2EC)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = entry.dateString,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (entry.medicationLogged) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Privado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Duración: ${entry.sleepDurationHours}h ${entry.sleepDurationMinutes}m",
                        fontSize = 12.sp,
                        color = Color(0xFF44474E)
                    )
                    Text(
                        text = "Rango: ${entry.bedtime} - ${entry.wakeupTime}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.testTag("delete_entry_btn_${entry.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Borrar",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }
}

fun formatHoursMinutes(h: Int, m: Int): String {
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
