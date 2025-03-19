import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.polly.PollyClient
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest
import software.amazon.awssdk.services.polly.model.OutputFormat
import software.amazon.awssdk.services.polly.model.VoiceId
import software.amazon.awssdk.core.sync.ResponseTransformer
import java.io.File
import java.io.FileOutputStream

object TTSLogic {
    private val credentialsProvider = ProfileCredentialsProvider.create("default")
    private val region = Region.US_EAST_1
    private val pollyClient: PollyClient = PollyClient.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build()

    fun generateSpeech(
        text: String,
        audioFile: File,
        voiceId: VoiceId = VoiceId.MAXIM // Замените голос, если нужно
    ): Boolean {
        return try {
            val request = SynthesizeSpeechRequest.builder()
                .text(text)
                .voiceId(voiceId)
                .outputFormat(OutputFormat.MP3)
                .build()

            // Используем ResponseTransformer для получения байтов
            val responseBytes = pollyClient.synthesizeSpeech(request, ResponseTransformer.toBytes())
            FileOutputStream(audioFile).use { fos ->
                fos.write(responseBytes.asByteArray())
            }
            println("Аудио сохранено: ${audioFile.absolutePath}")
            true
        } catch (e: Exception) {
            println("Ошибка при генерации речи: ${e.message}")
            false
        }
    }
}
