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

package org.elaastic.sequence.interaction


typealias ResponseId = Long

class ExplanationRecommendationMapping(val recommendationMap: Map<ResponseId, MutableList<ResponseId>>) {

    constructor(responses: List<ResponseId>) : this(
        responses.associate { it to mutableListOf<ResponseId>() }
    )

    fun addRecommandation(forResponse: ResponseId, recommendedResponse: ResponseId) =
        (recommendationMap[forResponse]
            ?: error("This responseId ($recommendedResponse) does not belong to this mapping"))
            .add(recommendedResponse)


    fun getRecommandation(forResponse: ResponseId) =
        recommendationMap[forResponse]

    operator fun get(forResponse: ResponseId) =
        getRecommandation(forResponse) ?: error("this response ($forResponse) does not belong to this mapping")

    override fun toString() = recommendationMap.entries.map {
        "[${it.key} => ${it.value.map { it }}]\n"
    }.joinToString(", ")

    override fun equals(other: Any?) =
        other?.javaClass == this.javaClass && toString() == other.toString()


    override fun hashCode(): Int {
        return toString().hashCode()
    }


}
