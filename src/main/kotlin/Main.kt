import Back.ChatLogic
import TTSLogic
import Back.AudioPlayer
import UI.ChatUI
import UI.MenuScreen
import UI.Screen
import UI.TTSUI
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import Back.DatabaseConfig
import Back.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun main() = application {
    // Подключаемся к базе данных при запуске приложения
    DatabaseConfig.connect()

    var currentScreen by remember { mutableStateOf(Screen.MENU) }
    val scope = CoroutineScope(Dispatchers.IO)
    // Файл, куда будет сохранено аудио; в данном случае создается в корневой папке приложения
    val audioFile = File("speech.mp3")

    Window(onCloseRequest = ::exitApplication, title = "Приложение") {
        MaterialTheme {
            when (currentScreen) {
                Screen.MENU -> {
                    MenuScreen(
                        onChatClick = { currentScreen = Screen.CHAT },
                        onVoiceClick = { currentScreen = Screen.VOICE },
                        onVisualizationClick = { currentScreen = Screen.VISUALIZATION },
                        onSomethingElseClick = { /* другое действие */ }
                    )
                }
                Screen.CHAT -> {
                    ChatUI(
                        sendMessage = { input ->
                            // Сохраняем сообщение пользователя в БД
                            MessageRepository.insertMessage(input, "User", System.currentTimeMillis())
                            // Получаем ответ от AI
                            val response = ChatLogic().getChatResponse(input)
                            // Сохраняем ответ AI в БД
                            MessageRepository.insertMessage(response, "AI", System.currentTimeMillis())
                            response
                        },
                        onBackClick = { currentScreen = Screen.MENU }
                    )
                }
                Screen.VOICE -> {
                    // Берем последний текст из базы данных для озвучки
                    val textToSpeech = MessageRepository.getChatHistory().lastOrNull()?.text ?: "Нет текста"

                    var isGenerating by remember { mutableStateOf(false) }
                    var isPlaying by remember { mutableStateOf(false) }

                    TTSUI(
                        textToSpeech = textToSpeech,
                        onGenerateAudio = {
                            isGenerating = true
                            scope.launch {
                                // Генерируем аудио через Amazon Polly
                                val success = TTSLogic.generateSpeech(textToSpeech, audioFile)
                                isGenerating = false
                                if (success) {
                                    println("Аудио успешно сгенерировано: ${audioFile.absolutePath}")
                                } else {
                                    println("Ошибка генерации аудио.")
                                }
                            }
                        },
                        onPlayAudio = {
                            isPlaying = !isPlaying
                            if (isPlaying) {
                                AudioPlayer.playAudio(audioFile)
                            } else {
                                AudioPlayer.stopAudio()
                            }
                        },
                        isGenerating = isGenerating,
                        isPlaying = isPlaying,
                        onBackClick = { currentScreen = Screen.MENU }
                    )
                }
                Screen.VISUALIZATION -> {
                    // Пока пустой экран визуализации
                }
            }
        }
    }
}
