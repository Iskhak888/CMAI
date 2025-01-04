import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

class ChatLogic {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Игнорировать неизвестные ключи
            })
        }
    }





    val apiKey = "YOUR_API_KEY"
    private val apiUrl = "https://api.openai.com/v1/chat/completions"

    suspend fun getChatResponse(message: String): String {
        return try {
            val response: OpenAIResponse = client.post(apiUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $apiKey")
                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                }
                setBody(
                    OpenAIRequest(
                        model = "gpt-4o-2024-11-20",
                        messages = listOf(OpenAIMessage(role = "user", content = message))
                    )
                )
            }.body()
            val reply = response.choices.firstOrNull()?.message?.content ?: "Ответ отсутствует"
            val tokenUsage = response.usage?.total_tokens ?: 0
            println("Токены, использованные в запросе: $tokenUsage")
            reply
        } catch (e: Exception) {
            println(e.message)
            "Ошибка при вызове API: ${e.message}"
        }
    }
}

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>
)

@Serializable
data class OpenAIResponse(
    val id: String,
    val `object`: String, // Исправлено для совпадения с JSON
    val created: Long,
    val model: String,
    val choices: List<OpenAIChoice>,
    val usage: Usage? = null
)

@Serializable
data class OpenAIChoice(
    val index: Int,
    val message: OpenAIMessage,
    val finish_reason: String? = null,
    val logprobs: String? = null
)

@Serializable
data class OpenAIMessage(
    val role: String,
    val content: String,
    val refusal: String? = null // Добавлено поле для ключа 'refusal'
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int,
    val prompt_tokens_details: TokenDetails? = null, // Новое поле
    val completion_tokens_details: TokenDetails? = null // Новое поле
)

@Serializable
data class TokenDetails(
    val cached_tokens: Int = 0,
    val audio_tokens: Int = 0,
    val reasoning_tokens: Int = 0,
    val accepted_prediction_tokens: Int = 0,
    val rejected_prediction_tokens: Int = 0
)
