package Back

data class MessageEntity(
    val id: Int,
    val text: String,
    val sender: String,
    val timestamp: Long
)
