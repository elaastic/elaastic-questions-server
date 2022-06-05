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

package org.elaastic.questions.onboarding

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import javax.persistence.AttributeConverter


class OnboardingChapterConverter : AttributeConverter<MutableSet<OnboardingChapter>?, String?> {

    private val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    override fun convertToDatabaseColumn(attribute: MutableSet<OnboardingChapter>?): String? {
        return when(attribute) {
            null -> "empty"
            else -> mapper.writeValueAsString(attribute)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): MutableSet<OnboardingChapter>? {
        if (dbData in arrayOf(null, "", "empty")) {
            return null
        }

        return mapper.readValue(dbData, object : TypeReference<MutableSet<OnboardingChapter>>(){})
    }
}
