package org.elaastic.questions.assignment.choice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter


class ChoiceSpecificationConverter :
        AttributeConverter<ChoiceSpecification, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())


    override fun convertToDatabaseColumn(attribute: ChoiceSpecification?): String? {
        return mapper.writeValueAsString(attribute?.toLegacy())
    }

    override fun convertToEntityAttribute(dbData: String?): ChoiceSpecification? {
        return when (dbData) {
            null -> null
            else -> mapper.readValue(
                    dbData,
                    org.elaastic.questions.assignment.choice.legacy.ChoiceSpecification::class.java
            )?.let {

                val choiceType = parseChoiceType(it.choiceInteractionType)
                when (choiceType) {
                    null -> null
                    ChoiceType.EXCLUSIVE ->
                        return ExclusiveChoiceSpecification(
                                nbCandidateItem = it.itemCount,
                                expectedChoice = it.expectedChoiceList.first(),
                                explanationChoiceList = it.explanationChoiceList
                        )
                    ChoiceType.MULTIPLE ->
                        return MultipleChoiceSpecification(
                                nbCandidateItem = it.itemCount,
                                expectedChoiceList = it.expectedChoiceList,
                                explanationChoiceList = it.explanationChoiceList
                        )
                }
            }
        }
    }

    private fun parseChoiceType(value: String): ChoiceType? {
        return when (value) {
            ChoiceType.EXCLUSIVE.name -> ChoiceType.EXCLUSIVE
            ChoiceType.MULTIPLE.name -> ChoiceType.MULTIPLE
            else -> null
        }
    }
}