import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.unit.dp
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import javax.swing.JPanel

@Composable
fun VideoPlayer(videoPath: String) {
    val mediaPlayerComponent = remember { EmbeddedMediaPlayerComponent() }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        NativeDiscovery().discover()
        onDispose {
            mediaPlayerComponent.mediaPlayer().controls().stop()
            mediaPlayerComponent.release()
        }
    }

    Column {
        // Видео плеер
        SwingPanel(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            factory = {
                mediaPlayerComponent
            }
        )

        // Кнопки управления
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (isPlaying) {
                    mediaPlayerComponent.mediaPlayer().controls().pause()
                } else {
                    if (mediaPlayerComponent.mediaPlayer().status().isPlayable()) {
                        mediaPlayerComponent.mediaPlayer().controls().play()
                    } else {
                        mediaPlayerComponent.mediaPlayer().media().play(videoPath)
                    }
                }
                isPlaying = !isPlaying
            }) {
                Text(if (isPlaying) "Пауза" else "Воспроизвести")
            }

            Button(onClick = {
                mediaPlayerComponent.mediaPlayer().controls().stop()
                isPlaying = false
            }) {
                Text("Стоп")
            }
        }
    }
}