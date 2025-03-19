package Back

import java.io.File
import java.io.FileInputStream
import javazoom.jl.decoder.Bitstream
import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.Player

object AudioPlayer {
    private var player: Player? = null

    fun playAudio(file: File) {
        stopAudio() // Останавливаем, если уже играет
        try {
            val inputStream = FileInputStream(file)
            player = Player(inputStream)
            Thread {
                try {
                    player?.play()
                } catch (e: JavaLayerException) {
                    println("Ошибка воспроизведения аудио: ${e.message}")
                }
            }.start()
        } catch (e: Exception) {
            println("Ошибка открытия файла: ${e.message}")
        }
    }

    fun stopAudio() {
        player?.close()
        player = null
    }
}
