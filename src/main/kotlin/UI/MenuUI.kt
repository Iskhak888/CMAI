package UI

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

// Вместо использования импортированных иконок создадим текстовые иконки
@Composable
fun MenuScreen(
    onChatClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onVisualizationClick: () -> Unit,
    onSomethingElseClick: () -> Unit
) {
    // Создаем градиентный фон
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF121212),
            Color(0xFF1E1E2E)
        )
    )

    // Определяем акцентные цвета для кнопок
    val primaryGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF8338EC), // Фиолетовый
            Color(0xFF3A86FF)  // Синий
        )
    )

    var showTitle by remember { mutableStateOf(false) }

    // Анимированный эффект для заголовка
    LaunchedEffect(Unit) {
        showTitle = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Анимированный заголовок
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn() + expandVertically()
            ) {
                Text(
                    text = "Viral Creator",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = "Создавайте трендовые ролики для TikTok и Shorts",
                fontSize = 16.sp,
                color = Color(0xFFAAAAAA),
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )

            // Кнопки меню
            MenuButton(
                text = "Чат с ИИ",
                description = "Генерация идей и сценариев для роликов",
                icon = "💬",
                gradient = primaryGradient,
                onClick = onChatClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Озвучивание",
                description = "Преобразование текста в профессиональную озвучку",
                icon = "🎙️",
                gradient = primaryGradient,
                onClick = onVoiceClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Визуализация",
                description = "Создание и редактирование видеоконтента",
                icon = "🎬",
                gradient = primaryGradient,
                onClick = onVisualizationClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Дополнительно",
                description = "Другие инструменты и настройки",
                icon = "⚙️",
                gradient = primaryGradient,
                onClick = onSomethingElseClick
            )
        }

        // Версия приложения
        Text(
            text = "v1.0.0",
            color = Color(0xFF666666),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    description: String,
    icon: String,
    gradient: Brush,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val elevation by animateFloatAsState(if (isPressed) 2f else 8f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = elevation.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
            elevation = null,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradient)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = icon,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    Column {
                        Text(
                            text = text,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text = description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}