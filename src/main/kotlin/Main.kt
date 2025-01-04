import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Чат") {
        MaterialTheme {
            // Передаём логику в UI
            ChatUI(sendMessage = { input ->
                ChatLogic().getChatResponse(input)
            })
        }
    }
}
