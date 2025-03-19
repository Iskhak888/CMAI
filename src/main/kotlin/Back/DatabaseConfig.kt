package Back

import org.jetbrains.exposed.sql.Database

object DatabaseConfig {
    fun connect() {
        Database.connect(
            url = "jdbc:postgresql://localhost:5432/CMAI",
            driver = "org.postgresql.Driver",
            user = "postgres",
            password = "postgres"
        )
    }
}
