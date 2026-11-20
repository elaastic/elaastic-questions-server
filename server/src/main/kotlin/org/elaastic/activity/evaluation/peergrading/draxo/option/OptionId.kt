/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.activity.evaluation.peergrading.draxo.option

/**
 * OptionId is an enumeration of the possible values for a DraxoPeerGrading criteria.
 *
 * @property YES
 * @property NO
 * @property PARTIALLY
 * @property DONT_KNOW
 * @property NO_OPINION
 *
 * @see org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
 */
enum class OptionId(val codeI18n: String) {
    YES("common.yes"),
    NO("common.no"),
    PARTIALLY("common.partially"),
    DONT_KNOW("draxo.value.dontKnow"),
    NO_OPINION("draxo.value.noOpinion"),

}