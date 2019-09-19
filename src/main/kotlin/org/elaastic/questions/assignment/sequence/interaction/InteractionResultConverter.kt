package org.elaastic.questions.assignment.sequence.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter


class InteractionResultConverter : AttributeConverter<InteractionResult?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: InteractionResult?): String? {
        return when (attribute) {
            null -> null
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): InteractionResult? {
        return when (dbData) {
            null, "" -> null
            else -> mapper.readValue(dbData, InteractionResult::class.java)
        }
    }
}