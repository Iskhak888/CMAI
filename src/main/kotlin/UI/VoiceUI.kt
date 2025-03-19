package UI

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TTSUI(
    textToSpeech: String,
    onGenerateAudio: () -> Unit,
    onPlayAudio: () -> Unit,
    isGenerating: Boolean,
    isPlaying: Boolean,
    onBackClick: () -> Unit
) {
    // Градиентный фон
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF121212),
            Color(0xFF1E1E2E)
        )
    )

    // Акцентные цвета
    val accentGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF8338EC), // Фиолетовый
            Color(0xFF3A86FF)  // Синий
        )
    )

    val scope = rememberCoroutineScope()
    var audioProgress by remember { mutableStateOf(0f) }
    var showVisualizer by remember { mutableStateOf(false) }

    // Анимация для визуализатора звука
    val infiniteTransition = rememberInfiniteTransition()
    val visualizerBars = 10
    val barHeights = List(visualizerBars) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2000
                    0.1f at 0
                    0.8f at (index * 100)
                    0.4f at (index * 100 + 300)
                    0.1f at 2000
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            showVisualizer = true
            // Имитация прогресса воспроизведения
            audioProgress = 0f
            while (audioProgress < 1f && isPlaying) {
                delay(100)
                audioProgress += 0.005f
            }
        } else {
            showVisualizer = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            // Верхняя панель
            TopAppBar(
                title = {
                    Text(
                        "Голосовой синтез",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("← Назад", color = Color.White)
                    }
                },
                backgroundColor = Color(0xFF2A2A3C),
                elevation = 8.dp
            )

            // Основной контент
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Заголовок
                Text(
                    text = "Озвучивание текста",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Текст для озвучивания
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp,
                    backgroundColor = Color(0xFF2A2A3C)
                ) {
                    Column {
                        // Заголовок карточки
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(accentGradient)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Текст для озвучивания",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Содержимое текста
                        SelectionContainer {
                            Text(
                                text = textToSpeech,
                                modifier = Modifier.padding(16.dp),
                                fontSize = 16.sp,
                                color = Color.White,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Визуализатор при воспроизведении
                if (showVisualizer && isPlaying) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        barHeights.forEach { animatedValue ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 2.dp)
                                    .fillMaxHeight(animatedValue.value)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(accentGradient)
                            )
                        }
                    }

                    // Прогресс воспроизведения
                    LinearProgressIndicator(
                        progress = audioProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
                        color = Color(0xFF8338EC),
                        backgroundColor = Color(0xFF444444)
                    )
                } else {
                    Spacer(modifier = Modifier.height(100.dp))
                }

                // Кнопки управления
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Кнопка "Озвучить"
                    Button(
                        onClick = {
                            scope.launch {
                                onGenerateAudio()
                            }
                        },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF8338EC),
                            disabledBackgroundColor = Color(0xFF444444)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "🎙️",
                                    fontSize = 20.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (isGenerating) "Генерация..." else "Озвучить",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Кнопка "Воспроизвести"
                    Button(
                        onClick = {
                            scope.launch {
                                onPlayAudio()
                            }
                        },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isPlaying) Color(0xFFE63946) else Color(0xFF3A86FF),
                            disabledBackgroundColor = Color(0xFF444444)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isPlaying) "⏸️" else "▶️",
                                fontSize = 20.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (isPlaying) "Пауза" else "Воспроизвести",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Дополнительные опции
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFF2A2A3C),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Настройки голоса",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Настройки голоса (можно добавить слайдеры, селекторы и т.д.)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Тембр голоса:",
                                color = Color.White,
                                modifier = Modifier.width(120.dp)
                            )

                            Slider(
                                value = 0.7f,
                                onValueChange = {},
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF8338EC),
                                    activeTrackColor = Color(0xFF8338EC)
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Скорость:",
                                color = Color.White,
                                modifier = Modifier.width(120.dp)
                            )

                            Slider(
                                value = 0.5f,
                                onValueChange = {},
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF8338EC),
                                    activeTrackColor = Color(0xFF8338EC)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}