package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.viewmodel.SleepViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: SleepViewModel,
    modifier: Modifier = Modifier
) {
    val chatMessages by viewModel.allChatMessages.collectAsState()
    val isGeneratingResponse by viewModel.isGeneratingResponse.collectAsState()
    val chatError by viewModel.chatError.collectAsState()

    var inputPhrase by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Sugerencias rápidas de preguntas científicas sobre el sueño
    val currentSuggestions = listOf(
        "¿Cómo mejoro mi sueño profundo?",
        "Analiza mi historial de esta semana",
        "¿Evitar luz azul de noche me ayuda?",
        "¿Qué significa tener mucho REM?"
    )

    // Desplazar automáticamente al fondo cuando hay nuevos mensajes
    LaunchedEffect(chatMessages.size, isGeneratingResponse) {
        if (chatMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ENCABEZADO
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "Scribe",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Consejero Scribe de Sueño",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "IA Somnóloga de Precisión",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            actions = {
                if (chatMessages.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.clearChat() },
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Borrar Chat",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )

        // LISTADO DE MENSAJES
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mensaje de bienvenida inicial si la conversación está vacía
            if (chatMessages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "Hola, soy Scribe de Sueño",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "He analizado tus métricas de smartwatch cargadas. Pregúntame sobre tus fases de sueño profundo o REM, consejos personalizados de higiene nocturna o dinámicas de descanso cotidiano.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            items(chatMessages) { msg ->
                ChatBubble(message = msg)
            }

            // Indicador de respuesta de IA en progreso
            if (isGeneratingResponse) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 40.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                            modifier = Modifier.padding(end = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Scribe de Sueño está analizando...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Error
            chatError?.let { err ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Error del Asistente: $err",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // SUGERENCIAS RÁPIDAS (CHIPS)
        AnimatedVisibility(visible = !isGeneratingResponse) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Sugerencias científicas:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentSuggestions.take(2).forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                inputPhrase = suggestion
                                viewModel.sendUserMessage(suggestion)
                                inputPhrase = ""
                            },
                            label = { Text(suggestion, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentSuggestions.drop(2).forEach { suggestion ->
                        SuggestionChip(
                            onClick = {
                                inputPhrase = suggestion
                                viewModel.sendUserMessage(suggestion)
                                inputPhrase = ""
                            },
                            label = { Text(suggestion, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // BARRA DE ESCRIBIR
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputPhrase,
                    onValueChange = { inputPhrase = it },
                    placeholder = { Text("Escribe tus dudas sobre sueño...", fontSize = 14.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    singleLine = false,
                    maxLines = 4,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                IconButton(
                    onClick = {
                        if (inputPhrase.isNotBlank()) {
                            viewModel.sendUserMessage(inputPhrase)
                            inputPhrase = ""
                        }
                    },
                    enabled = inputPhrase.isNotBlank() && !isGeneratingResponse,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (inputPhrase.isNotBlank() && !isGeneratingResponse) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                        .size(48.dp)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = if (inputPhrase.isNotBlank() && !isGeneratingResponse) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 290.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer,
                shape = if (isUser) {
                    RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                } else {
                    RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                },
                tonalElevation = if (isUser) 0.dp else 2.dp
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}
