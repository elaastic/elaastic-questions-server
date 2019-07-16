package org.elaastic.questions.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter

/**
 * @author John Tranier
 */
class ChoiceSpecificationConverter :
        AttributeConverter<ChoiceSpecification, String?> {

    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())


    override fun convertToDatabaseColumn(attribute: ChoiceSpecification?): String? {
        return mapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): ChoiceSpecification? {
          return when(dbData) {
              null -> null
              else -> mapper.readValue(dbData, ChoiceSpecification::class.java)
          }
    }
}