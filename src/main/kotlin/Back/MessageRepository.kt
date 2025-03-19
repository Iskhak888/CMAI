package Back

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object MessageRepository {
    fun insertMessage(text: String, sender: String, timestamp: Long) {
        transaction {
            MessagesTable.insert {
                it[MessagesTable.text] = text
                it[MessagesTable.sender] = sender
                it[MessagesTable.timestamp] = timestamp
            }
        }
    }

    fun getAllMessages(): List<MessageEntity> {
        return transaction {
            MessagesTable.selectAll().map {
                MessageEntity(
                    id = it[MessagesTable.id],
                    text = it[MessagesTable.text],
                    sender = it[MessagesTable.sender],
                    timestamp = it[MessagesTable.timestamp]
                )
            }
        }
    }

    fun getChatHistory(): List<MessageEntity> {
        return transaction {
            MessagesTable.selectAll().orderBy(MessagesTable.timestamp).map {
                MessageEntity(
                    id = it[MessagesTable.id],
                    text = it[MessagesTable.text],
                    sender = it[MessagesTable.sender],
                    timestamp = it[MessagesTable.timestamp]
                )
            }
        }
    }

}
