package org.elaastic.questions.assignment.sequence.interaction.specification

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.persistence.AttributeConverter

/**
 * @author John Tranier
 */
class InteractionSpecificationConverter : AttributeConverter<InteractionSpecification?, String?> {

    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

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
            else -> throw IllegalStateException("The type of interaction specification could not be determined: \"$dbData\"")
        }
    }

    private fun isEvaluationSpecification(data: Map<*, *>): Boolean {
        if (data.containsKey("type")) {
            return data["type"] == InteractionType.Evaluation.name
        } else return data.containsKey("responseToEvaluateCount")
    }

    private fun isResponseSubmissionSpecification(data: Map<*, *>): Boolean {
        if (data.containsKey("type")) {
            return data["type"] == InteractionType.ResponseSubmission.name
        } else return data.containsKey("studentsProvideExplanation")
    }
}