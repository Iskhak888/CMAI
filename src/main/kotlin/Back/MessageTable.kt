package Back

import org.jetbrains.exposed.sql.Table

object MessagesTable : Table("messages") {
    val id = integer("id").autoIncrement()
    val text = varchar("text", 10000) // Ограничение на 10000 символов
    val sender = varchar("sender", 5000) // User
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}
