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
package org.elaastic.questions.assignment.ia

import org.elaastic.questions.assignment.sequence.interaction.ExplanationRecommendationMapping
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.springframework.stereotype.Service

@Service
class ResponseRecommendationService {

    fun computeRecommendations(responseList: List<Response>,
                               nbEvaluation: Int): ExplanationRecommendationMapping {
        responseList.map { ResponseInfo(it) }.let { allResponse ->
            ExplanationRecommendationMapping(allResponse.map { it.id }).let { recommendationsMapping ->
                RecommendationResponsePool(
                        allResponse.filter { it.evaluable },
                        INCORRECT_RESPONSE_FIRST
                ).let { recommendationResponsePool ->
                    repeat(nbEvaluation) { i ->
                        computeRecommandations(
                                forResponseList = allResponse.filter { it.correct }.shuffled(), // TODO Don't do it for each iteration
                                responsePool = recommendationResponsePool.comparator(
                                        if(i % 2 == 0) INCORRECT_RESPONSE_FIRST else CORRECT_RESPONSE_FIRST
                                ),
                                recommendationsMapping = recommendationsMapping
                        )

                        computeRecommandations(
                                forResponseList = allResponse.filter { !it.correct }.shuffled(),
                                responsePool = recommendationResponsePool.comparator(
                                        if(i % 2 == 0) CORRECT_RESPONSE_FIRST else INCORRECT_RESPONSE_FIRST
                                ),
                                recommendationsMapping = recommendationsMapping
                        )
                    }
                }

                return recommendationsMapping
            }
        }
    }

    /**
     * Add a recommendation (when possible) for each response in forResponseList
     * using the responsePool (which stores candidates recommendationsMapping and the selection mechanism)
     *
     * Recommendations are stored into the recommendationsMapping mapping
     */
    private fun computeRecommandations(forResponseList: List<ResponseInfo>,
                                       responsePool: RecommendationResponsePool,
                                       recommendationsMapping: ExplanationRecommendationMapping) {
        forResponseList.forEach { forResponse ->
            responsePool.next(except = recommendationsMapping[forResponse.id])?.let { recommendedResponse ->
                recommendationsMapping.addRecommandation(forResponse.id, recommendedResponse.id)
            }
        }
    }

    fun getRecommendationsForOpenQuestion(responseList: List<Response>,
                                          nbRecommendation: Int) {
           // TODO("Requirements")

        
        
    }

    companion object {
        val INCORRECT_RESPONSE_FIRST: Comparator<ResponseInfo> = kotlin.Comparator<ResponseInfo> { r1, r2 ->
            when (Pair(r1.correct, r2.correct)) {
                Pair(true, false) -> -1
                Pair(false, true) -> 1
                else -> 0
            }
        }
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.nbSelection.compareTo(r2.nbSelection) })
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> r1.id.compareTo(r2.id) })

        val CORRECT_RESPONSE_FIRST: Comparator<ResponseInfo> = kotlin.Comparator<ResponseInfo> { r1, r2 ->
            when (Pair(r1.correct, r2.correct)) {
                Pair(true, false) -> 1
                Pair(false, true) -> -1
                else -> 0
            }
        }
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.nbSelection.compareTo(r2.nbSelection) })
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> r1.id.compareTo(r2.id) })
    }
}