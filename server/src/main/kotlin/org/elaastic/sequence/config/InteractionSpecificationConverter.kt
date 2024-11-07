package org.elaastic.sequence.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class InteractionSpecificationConverter : AttributeConverter<InteractionSpecification?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )

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