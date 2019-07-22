package org.elaastic.questions.assignment.sequence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.ExplanationRecommendationMapping
import javax.persistence.AttributeConverter

/**
 * @author John Tranier
 */
class ExplanationRecommendationMappingConverter :
        AttributeConverter<ExplanationRecommendationMapping?, String?> {

    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: ExplanationRecommendationMapping?): String? {
        return when(attribute) {
            null -> null
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): ExplanationRecommendationMapping? {
        return when(dbData) {
            null -> null
            else -> mapper.readValue(dbData, ExplanationRecommendationMapping::class.java)
        }
    }
}