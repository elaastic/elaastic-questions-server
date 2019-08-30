package org.elaastic.questions.assignment.choice.legacy

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter


class ChoiceListSpecificationConverter :
        AttributeConverter<ChoiceListSpecification?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: ChoiceListSpecification?): String? {
        return when(attribute) {
            null -> null
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): ChoiceListSpecification? {
        return when(dbData) {
            null -> null
            else -> mapper.readValue(dbData, ChoiceListSpecification::class.java)
        }
    }
}