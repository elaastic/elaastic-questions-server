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

import org.elaastic.questions.player.components.explanationViewer.ExplanationData

data class RecommendationModel(
        val message: String? = "",
        val noCorrectExplanation: Boolean = false,

        /* explanation for the consistency of learners agreement with peers' rationales */
        val explanationPPeer: ExplanationPPeer? = null,

        /* explanation for the consistency of learners confidence degree */
        val explanationPConf: ExplanationPConf? = null,

        /* explanation for the proportion of correct answers during the first vote*/
        val explanationP1: ExplanationP1? = null,

        /* explanation for the effect size of the confrontation phase on the proportion of correct answers */
        val explanationD: ExplanationD? = null,

//        val correctAnswersMeanConfidenceDegree: Float? = null,
//        val incorrectAnswersMeanConfidenceDegree: Float? = null,
        val popupDetailedExplanation: PopupDetailedExplanation? = null,

        val recommendedExplanationsComparator: Comparator<ExplanationData> = CorrectAndMeanGradeComparator()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecommendationModel

        if (message != other.message) return false
        if (noCorrectExplanation != other.noCorrectExplanation) return false
        if (explanationPPeer != other.explanationPPeer) return false
        if (explanationPConf != other.explanationPConf) return false
        if (explanationP1 != other.explanationP1) return false
        if (explanationD != other.explanationD) return false
        if (popupDetailedExplanation != other.popupDetailedExplanation) return false
        if (recommendedExplanationsComparator.javaClass != other.recommendedExplanationsComparator.javaClass) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message?.hashCode() ?: 0
        result = 31 * result + noCorrectExplanation.hashCode()
        result = 31 * result + (explanationPPeer?.hashCode() ?: 0)
        result = 31 * result + (explanationPConf?.hashCode() ?: 0)
        result = 31 * result + (explanationP1?.hashCode() ?: 0)
        result = 31 * result + (explanationD?.hashCode() ?: 0)
        result = 31 * result + (popupDetailedExplanation?.hashCode() ?: 0)
        result = 31 * result + recommendedExplanationsComparator.hashCode()
        return result
    }
}

enum class ExplanationP1(val propertyString: String) {
    ADEQUATE(""),
    VERY_HIGH("player.sequence.recommendation.p1VeryHigh.message"),
    TOO_LOW("player.sequence.recommendation.p1TooLow.message"),
    NULL("player.sequence.recommendation.p1NoAnswers.message"),
    VERY_HIGH_SKIP("player.sequence.recommendation.p1VeryHigh_skip.message"),
    TOO_LOW_SKIP("player.sequence.recommendation.p1TooLow_skip.message")
}

enum class ExplanationPConf(val propertyString: String) {
    PCONF_NEG("player.sequence.recommendation.pConfNeg.message"),
    PCONF_POS("player.sequence.recommendation.pConfPos.message"),
    PCONF_ZERO("player.sequence.recommendation.pConfZero.message"),
    PCONF_NULL(""),
    PCONF_NEG_SKIP("player.sequence.recommendation.pConfNegSkip.message"),
    PCONF_POS_SKIP("player.sequence.recommendation.pConfPosSkip.message"),
    PCONF_ZERO_SKIP("player.sequence.recommendation.pConfZeroSkip.message"),
    PCONF_NULL_SKIP("")
}


enum class ExplanationPPeer(val propertyString: String) {
    PPEER_NEG("player.sequence.recommendation.pPeerNeg.message"),
    PPEER_POS("player.sequence.recommendation.pPeerPos.message"),
    PPEER_ZERO("player.sequence.recommendation.pPeerZero.message"),
    PPEER_NULL("")
}

enum class ExplanationD(val propertyString: String) {
    D_NEG("player.sequence.recommendation.dNeg.message"),
    D_POS("player.sequence.recommendation.dPos.message"),
    D_ZERO("player.sequence.recommendation.dZero.message"),
    D_NULL("")
}

enum class PopupDetailedExplanation(val propertyString: String) {
    NON_SIGNIFICANT_BENEFITS("player.sequence.recommendation.expected_benefits.non_significant"),
    WEAK_BENEFITS("player.sequence.recommendation.expected_benefits.weak"),
    NO_EXPLANATION_FOR_CORRECT_ANSWERS("player.sequence.recommendation.explanation.unsatisfying_condition"),
    NO_ANSWERS("player.sequence.recommendation.explanation.no_answers"),
    POPULAR_ANSWERS_CORRECT("player.sequence.recommendation.explanation.discussion_correct"),
    POPULAR_ANSWERS_INCORRECT("player.sequence.recommendation.explanation.discussion_incorrect")
}
