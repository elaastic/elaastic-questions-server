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

package org.elaastic.questions.assignment.choice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javax.persistence.AttributeConverter
import javax.persistence.Converter


@Converter
class ChoiceSpecificationConverter :
        AttributeConverter<ChoiceSpecification, String?> {

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
