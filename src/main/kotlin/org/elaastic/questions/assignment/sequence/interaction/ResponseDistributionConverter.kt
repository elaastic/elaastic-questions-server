package org.elaastic.questions.assignment.sequence.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.results.ResponsesDistribution
import javax.persistence.AttributeConverter


class ResponseDistributionConverter : AttributeConverter<ResponsesDistribution?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: ResponsesDistribution?): String? {
        return when (attribute) {
            null -> null
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): ResponsesDistribution? {
        return when (dbData) {
            null, "" -> null
            else -> mapper.readValue(dbData, ResponsesDistribution::class.java)
        }
    }
}