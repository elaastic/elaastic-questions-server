package org.elaastic.questions.assignment.sequence.interaction

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter


class InteractionResultConverter : AttributeConverter<InteractionResult?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: InteractionResult?): String? {
        return when(attribute) {
            null -> null
            else -> mapper.writeValueAsString(attribute.toLegacyFormat())
        }
    }

    override fun convertToEntityAttribute(dbData: String?): InteractionResult? {
        if(dbData == null) return null

        val map = mapper.readValue<Map<String, List<Float>>>(
                dbData,
                mapper.typeFactory.constructParametricType(Map::class.java, String::class.java, List::class.java)
        )

        return InteractionResult(
                OneAttemptResult(map["1"] ?: listOf()),
                when {
                    map.containsKey("2") -> OneAttemptResult(map["2"] ?: listOf())
                    else -> null
                }
        )
    }
}