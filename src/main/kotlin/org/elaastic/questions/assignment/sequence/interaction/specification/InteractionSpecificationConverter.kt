/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.questions.assignment.sequence.interaction.specification

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.persistence.AttributeConverter


class InteractionSpecificationConverter : AttributeConverter<InteractionSpecification?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: InteractionSpecification?): String? {
        return when(attribute) {
            null -> "empty"
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): InteractionSpecification? {
        if (dbData in arrayOf(null, "", "empty")) {
            return null
        }

        val map = mapper.readValue(dbData, Map::class.java)

        return when {
            isEvaluationSpecification(map) -> mapper.readValue(dbData, EvaluationSpecification::class.java)
            isResponseSubmissionSpecification(map) -> mapper.readValue(dbData, ResponseSubmissionSpecification::class.java)
            isReadSpecification(map) -> ReadSpecification()
            else -> error("The type of interaction specification could not be determined: \"$dbData\"")
        }
    }

    private fun isEvaluationSpecification(data: Map<*, *>): Boolean {
        return if (data.containsKey("type")) {
            data["type"] == InteractionType.Evaluation.name
        } else data.containsKey("responseToEvaluateCount")
    }

    private fun isResponseSubmissionSpecification(data: Map<*, *>): Boolean {
        return if (data.containsKey("type")) {
            data["type"] == InteractionType.ResponseSubmission.name
        } else data.containsKey("studentsProvideExplanation")
    }

    private fun isReadSpecification(data: Map<*, *>): Boolean {
        return data["type"] == InteractionType.Read.name
    }
}
