package org.elaastic.questions.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.sql.Connection
import java.sql.SQLException
import java.util.*


class V42__Populate_UUID : BaseJavaMigration() {

    override fun migrate(context: Context) {
        val connection = context.connection
        listOf(
            "attachement",
            "choice_interaction_response",
            "interaction",
            "sequence",
            "statement",
            "user",
        ).forEach { tableName -> updateUuids(connection, tableName) }
    }

    @Throws(SQLException::class)
    private fun updateUuids(connection: Connection, tableName: String) {
        val selectQuery = "SELECT * FROM $tableName"
        val updateQuery = "UPDATE $tableName SET uuid = ? WHERE id = ?"
        connection.prepareStatement(selectQuery).use { selectStatement ->
            connection.prepareStatement(updateQuery).use { updateStatement ->
                val resultSet = selectStatement.executeQuery()
                while (resultSet.next()) {
                    val id = resultSet.getString("id")
                    val uuid = UUID.randomUUID()
                    updateStatement.setString(1, uuid.toString())
                    updateStatement.setString(2, id)
                    updateStatement.executeUpdate()
                }
            }
        }
    }
}