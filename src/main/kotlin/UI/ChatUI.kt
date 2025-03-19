package UI

import Back.MessageRepository
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun ChatUI(
    sendMessage: suspend (String) -> String,
    onBackClick: () -> Unit
) {
    var chatHistory by remember { mutableStateOf(listOf<Message>()) }
    var inputText by remember { mutableStateOf("") }
    val scope = CoroutineScope(Dispatchers.IO)
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    var isTyping by remember { mutableStateOf(false) }

    // Градиентный фон
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF121212),
            Color(0xFF1E1E2E)
        )
    )

    // Загружаем историю
    LaunchedEffect(Unit) {
        val history = MessageRepository.getChatHistory()
        chatHistory = history.map {
            Message(
                text = it.text,
                isUser = it.sender == "User",
                time = LocalDateTime.now()
            )
        }

        // Автоматический фокус на поле ввода при загрузке
        focusRequester.requestFocus()
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
            // Верхняя панель (AppBar)
            TopAppBar(
                title = {
                    Text(
                        "AI Сценарист",
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

            // История сообщений
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(chatHistory) { message ->
                        ChatBubble(message)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // Индикатор "печатает..."
                if (isTyping) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 24.dp, bottom = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF8338EC),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ИИ печатает...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Прокрутка вниз
            LaunchedEffect(chatHistory) {
                if (chatHistory.isNotEmpty()) {
                    listState.animateScrollToItem(chatHistory.size - 1)
                }
            }

            // Поле ввода
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = 4.dp,
                backgroundColor = Color(0xFF2A2A3C)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                "Введите сообщение...",
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF8338EC),
                            textColor = Color.White
                        ),
                        singleLine = false,
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Кнопка отправки
                    val buttonColor = animateFloatAsState(
                        targetValue = if (inputText.isBlank()) 0.4f else 1f,
                        animationSpec = tween(200)
                    )

                    Button(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                scope.launch {
                                    val userMessage = inputText.trim()
                                    inputText = ""
                                    chatHistory = chatHistory + Message(userMessage, isUser = true)

                                    // Показываем индикатор "печатает..."
                                    isTyping = true

                                    val response = sendMessage(userMessage)

                                    // Скрываем индикатор "печатает..."
                                    isTyping = false

                                    chatHistory = chatHistory + Message(response, isUser = false)
                                }
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF8338EC).copy(alpha = buttonColor.value),
                            disabledBackgroundColor = Color(0xFF444444)
                        )
                    ) {
                        Text("➤", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    val bubbleGradient = if (message.isUser)
        Brush.linearGradient(
            colors = listOf(Color(0xFF8338EC), Color(0xFF3A86FF))
        )
    else
        Brush.linearGradient(
            colors = listOf(Color(0xFF2A2A3C), Color(0xFF3C3C4C))
        )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 0.dp,
                bottomEnd = if (message.isUser) 0.dp else 18.dp
            ),
            elevation = 2.dp,
            backgroundColor = Color.Transparent,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(bubbleGradient)
                    .padding(12.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = message.text,
                        fontSize = 16.sp,
                        color = if (message.isUser) Color.White else Color.White,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Text(
            text = message.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontWeight = FontWeight.Light
        )
    }
}

data class Message(
    val text: String,
    val isUser: Boolean,
    val time: LocalDateTime = LocalDateTime.now()
)