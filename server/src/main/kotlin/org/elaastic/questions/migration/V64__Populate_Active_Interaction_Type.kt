package org.elaastic.questions.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context

class V64__Populate_Active_Interaction_Type : BaseJavaMigration() {
    override fun migrate(context: Context) {
        val connection = context.connection
        val updateQuery =
            "UPDATE sequence " +
            "SET active_interaction_type = (SELECT interaction_type FROM interaction WHERE active_interaction_id = id) " +
            "WHERE active_interaction_id IS NOT NULL AND active_interaction_type IS NULL"
        connection.prepareStatement(updateQuery).use { updateStatement ->
            updateStatement.executeUpdate()
        }
    }
}