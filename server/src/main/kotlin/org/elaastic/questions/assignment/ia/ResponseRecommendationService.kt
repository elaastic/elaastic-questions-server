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

import org.elaastic.sequence.interaction.ExplanationRecommendationMapping
import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.interaction.response.Response
import org.elaastic.sequence.interaction.response.ResponseRepository
import org.elaastic.activity.results.AttemptNum
import org.elaastic.user.User
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.*
import javax.persistence.EntityManager
import kotlin.Comparator
import kotlin.collections.ArrayList

@Service
class ResponseRecommendationService(
        val entityManager: EntityManager,
        val responseRepository: ResponseRepository
) {

    fun computeRecommendations(responseList: List<Response>,
                               nbEvaluation: Int): ExplanationRecommendationMapping {
        responseList.map { ResponseInfo(it) }.let { allResponse ->
            ExplanationRecommendationMapping(allResponse.map { it.id }).let { recommendationsMapping ->
                RecommendationResponsePool(
                        allResponse.filter { it.evaluable },
                        INCORRECT_RESPONSE_FIRST
                ).let { recommendationResponsePool ->
                    val correctResponseList = allResponse.filter { it.correct }.shuffled()
                    val incorrectResponseList = allResponse.filter { !it.correct }.shuffled()
                    repeat(nbEvaluation) { i ->
                        computeRecommandations(
                                forResponseList = correctResponseList,
                                responsePool = recommendationResponsePool.comparator(
                                        if (i % 2 == 0) INCORRECT_RESPONSE_FIRST else CORRECT_RESPONSE_FIRST
                                ),
                                recommendationsMapping = recommendationsMapping
                        )

                        computeRecommandations(
                                forResponseList = incorrectResponseList,
                                responsePool = recommendationResponsePool.comparator(
                                        if (i % 2 == 0) CORRECT_RESPONSE_FIRST else INCORRECT_RESPONSE_FIRST
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
                                       recommendationsMapping: ExplanationRecommendationMapping
    ) {
        forResponseList.forEach { forResponse ->
            var except = ArrayList<Long>(recommendationsMapping[forResponse.id])
            if (!except.contains(forResponse.id)) { // to avoid self-evaluation
                except.add(forResponse.id)
            }
            responsePool.next(except = except)?.let { recommendedResponse ->
                recommendationsMapping.addRecommandation(forResponse.id, recommendedResponse.id)
            }
        }
    }

    fun findAllResponsesOrderedByEvaluationCount(evaluator: User,
                                                 interaction: Interaction,
                                                 attemptNum: AttemptNum,
                                                 limit: Int,
                                                 excludedIds: List<Long> = listOf(),
                                                 seed: Long = System.nanoTime()): List<Response> =
            entityManager.createNativeQuery("""
        SELECT 
            cir.id as responseId, 
            (select count(*) from peer_grading pg where pg.response_id = cir.id) as evalCount,
            rand() as randomIndex
        FROM choice_interaction_response cir 
        WHERE cir.interaction_id = :interactionId 
              and cir.learner_id != :evaluatorId
              and cir.attempt = :attempt
              and cir.explanation is not null and CHAR_LENGTH(cir.explanation) > $MIN_SIZE_OF_EXPLANATION_TO_BE_EVALUATED
              
              """.trimIndent()+
                    if(excludedIds.isNotEmpty()) {
                        "and cir.id not in (${excludedIds.joinToString()})" } else { "" } +
                    """
        ORDER BY evalCount ASC, randomIndex ASC LIMIT :limit            
        """.trimIndent())
                    .setParameter("interactionId", interaction.id)
                    .setParameter("evaluatorId", evaluator.id)
                    .setParameter("attempt", attemptNum)
                    .setParameter("limit", limit)
                    .resultList.let { rawData ->
                responseRepository.getAllByIdIn(
                        rawData.map {
                            if (it is Array<*>) {
                                (it[0] as BigInteger).toLong()
                            } else error("Expect an array but got a ${it?.javaClass}")
                        }
                ).shuffled(random = Random(seed))

            }

    companion object {
        const val MIN_SIZE_OF_EXPLANATION_TO_BE_EVALUATED = 10

        val INCORRECT_RESPONSE_FIRST: Comparator<ResponseInfo> = kotlin.Comparator<ResponseInfo> { r1, r2 ->
            when (Pair(r1.correct, r2.correct)) {
                Pair(true, false) -> -1
                Pair(false, true) -> 1
                else -> 0
            }
        }
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.nbSelection.compareTo(r2.nbSelection) })
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.id.compareTo(r2.id) })

        val CORRECT_RESPONSE_FIRST: Comparator<ResponseInfo> = kotlin.Comparator<ResponseInfo> { r1, r2 ->
            when (Pair(r1.correct, r2.correct)) {
                Pair(true, false) -> 1
                Pair(false, true) -> -1
                else -> 0
            }
        }
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.nbSelection.compareTo(r2.nbSelection) })
                .then(kotlin.Comparator<ResponseInfo> { r1, r2 -> -r1.id.compareTo(r2.id) })
    }
}