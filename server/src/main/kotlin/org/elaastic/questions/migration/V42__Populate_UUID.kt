/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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