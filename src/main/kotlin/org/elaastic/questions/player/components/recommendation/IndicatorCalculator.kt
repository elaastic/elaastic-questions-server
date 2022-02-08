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

package org.elaastic.questions.player.components.recommendation

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseSet
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import kotlin.math.ln

object IndicatorCalculator {

    const val HIGH_THRESHOLD = 0.7f
    const val LOW_THRESHOLD = 0.3f

    fun computeP(responseList: List<Response>?): Float? {
        val notNullAnswers = responseList?.filter { response -> response.score != null}
        var res: Float? = null
        if(notNullAnswers != null && !notNullAnswers.isEmpty()){
            val nbCorrectAnswers = notNullAnswers.filter { response -> response.score!!.toInt() == 100 }.size
            res = nbCorrectAnswers.toFloat()/notNullAnswers.size.toFloat()
        }
        return res
    }

    fun computePConf(responseList: List<Response>?): Float? {
        /* Since no library for polychoric correlation was found, pconf is not returned but only its sign, which is enough for now... */
        val correctAnswers = responseList?.filter { response -> response.score?.toInt() == 100 && response.confidenceDegree != null}
        val incorrectAnswers = responseList?.filter { response -> response.score?.toInt() != 100  && response.confidenceDegree != null}
        val correctAnswersMeanConfidenceDegree = correctAnswers?.map { response -> response.confidenceDegree!!.ordinal }?.average()
        val incorrectAnswersMeanConfidenceDegree = incorrectAnswers?.map { response -> response.confidenceDegree!!.ordinal }?.average()
        return if (correctAnswersMeanConfidenceDegree == null || incorrectAnswersMeanConfidenceDegree == null || correctAnswersMeanConfidenceDegree.isNaN() || incorrectAnswersMeanConfidenceDegree.isNaN()) {
            null
        } else {
            correctAnswersMeanConfidenceDegree.compareTo(incorrectAnswersMeanConfidenceDegree).toFloat()
        }
    }

    fun computePPeer(peerGradings: List<PeerGrading>?): Float? {
        // TODO : Could be simplified by eliminating null value at the beginning, no?
        /* Since no library for polychoric correlation was found, ppeer is not returned but only its sign, which is enough for now... */
        val correctAnswers = peerGradings?.filter { pg -> pg.response.score!!.toInt() == 100 && pg.grade != null}
        val incorrectAnswers = peerGradings?.filter { pg -> pg.response.score!!.toInt() != 100 && pg.grade != null}
        val correctAnswersMeanGrade = correctAnswers?.map { pg -> pg.grade!!.toInt() }?.average()
        val incorrectAnswersMeanGrade = incorrectAnswers?.map { pg -> pg.grade!!.toInt() }?.average()
        return if (correctAnswersMeanGrade == null || incorrectAnswersMeanGrade == null || correctAnswersMeanGrade.isNaN() || incorrectAnswersMeanGrade.isNaN()) {
            null
        } else {
            correctAnswersMeanGrade.compareTo(incorrectAnswersMeanGrade).toFloat()
        }
    }

    fun computeD(responseSet: ResponseSet): Float? {
        val p1 = computeP(responseSet[1].filter { response -> !response.fake })
        val p2 = computeP(responseSet[2].filter { response -> !response.fake })
        return if (p1 != null && p2 != null){
                if(p1 != 0f && p2 != 1f) (0.6f * ln((p2 / (1f - p2)) * ((1f - p1) / p1)))
                else
                    if(p1 > p2) -1f
                        else if (p1 < p2) 1f
                        else 0f
                }
               else if(p1 == p2) 0f else null
    }

    fun adequate(p1: Float): Boolean =
            p1 in LOW_THRESHOLD..HIGH_THRESHOLD

    fun tooLow(p1: Float): Boolean = p1 < LOW_THRESHOLD

    fun veryHigh(p1: Float): Boolean = p1 > HIGH_THRESHOLD


}
