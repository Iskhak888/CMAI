import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.background

@Composable
fun ChatUI(sendMessage: suspend (String) -> String) {
    var chatHistory by remember { mutableStateOf(listOf<Message>()) }
    var inputText by remember { mutableStateOf("") }
    val scope = CoroutineScope(Dispatchers.IO)
    val listState = rememberLazyListState() // Состояние для управления прокруткой

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E)) // Устанавливаем фон на #dedee0
                .padding(16.dp)
        ) {
            // Область для чата
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState, // Используем состояние прокрутки
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(chatHistory) { message ->
                    ChatBubble(message)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Прокрутка вниз при добавлении нового сообщения
            LaunchedEffect(chatHistory) {
                if (chatHistory.isNotEmpty()) {
                    listState.animateScrollToItem(chatHistory.size - 1)
                }
            }

            // Поле ввода и кнопка отправки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Введите сообщение...") },
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color(0xFF6c757d),
                        focusedIndicatorColor = Color(0xFF7F52FF),
                        unfocusedIndicatorColor = Color(0xFF3D77FF)
                    )
                )

                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            scope.launch {
                                chatHistory = chatHistory + Message(inputText, isUser = true)
                                val response = sendMessage(inputText)
                                chatHistory = chatHistory + Message(response, isUser = false)
                                inputText = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF8338ec))
                ) {
                    Text("Отправить", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isUser) Brush.linearGradient(
        colors = listOf(Color(0xFF47126B), Color(0xFF3D77FF))
    ) else Brush.linearGradient(
        colors = listOf(Color(0xFF390099), Color(0xFFba181b))
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp,
            backgroundColor = Color.Transparent,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Box(
                modifier = Modifier.background(bubbleColor).padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 16.sp,
                    color = if (message.isUser) Color.White else Color.White
                )
            }
        }
        Text(
            text = message.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontWeight = FontWeight.Light
        )
    }
}

data class Message(
    val text: String,
    val isUser: Boolean,
    val time: LocalDateTime = LocalDateTime.now()
)
