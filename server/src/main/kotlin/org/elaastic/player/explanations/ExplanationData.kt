package org.elaastic.player.explanations

import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.response.Response
import java.math.BigDecimal
import java.math.RoundingMode

open class ExplanationData(
    val responseId: Long,
    val content: String? = null,
    val author: String? = null,
    val nbEvaluations: Int = 0,
    val nbDraxoEvaluations: Int = 0,
    meanGrade: BigDecimal? = null,
    val confidenceDegree: ConfidenceDegree? = null,
    val score: BigDecimal? = null,
    val correct: Boolean? = (score?.compareTo(BigDecimal(100)) == 0),
    val choiceList: LearnerChoice? = null,
    val hiddenByTeacher: Boolean = false,
    val recommendedByTeacher: Boolean = false,
    val nbDraxoEvaluationsHidden: Int = 0,
    val userId: Long? = null,
    val sequenceId: Long? = null,
    val explanationHasChatGPTEvaluation: Boolean = false,
) {
    /**
     * Constructor for the ExplanationData class
     * @param response the response to create the ExplanationData from
     * @param explanationHasChatGPTEvaluation true if the explanation has a ChatGPT evaluation
     */
    constructor(response: Response, explanationHasChatGPTEvaluation: Boolean) : this(
        responseId = response.id!!,
        content = response.explanation,
        author = response.learner.getDisplayName(),
        nbEvaluations = response.evaluationCount,
        nbDraxoEvaluations = response.draxoEvaluationCount,
        meanGrade = response.meanGrade,
        confidenceDegree = response.confidenceDegree,
        correct = response.score?.compareTo(BigDecimal(100)) == 0,
        score = response.score,
        choiceList = response.learnerChoice,
        hiddenByTeacher = response.hiddenByTeacher,
        recommendedByTeacher = response.recommendedByTeacher,
        nbDraxoEvaluationsHidden = response.draxoEvaluationHiddenCount,
        userId = response.learner.id,
        sequenceId = response.interaction.sequence.id,
        explanationHasChatGPTEvaluation = explanationHasChatGPTEvaluation,
    )

    val meanGrade = meanGrade
        ?.setScale(2, RoundingMode.CEILING)
        ?.stripTrailingZeros()

    open val fromTeacher = false

    /**
     * Number of evaluation visible to the user
     * A student can only see the evaluations if they are not hidden by the teacher
     * @param isTeacher true if the user is a teacher false otherwise
     * @return the number of evaluations
     */
    fun getNbEvaluation(isTeacher: Boolean): Int {
        return if (isTeacher) nbEvaluations else nbEvaluations - nbDraxoEvaluationsHidden
    }

    /**
     * Number of Draxo evaluation visible to the user
     * A student can only see the Draxo evaluations if they are not hidden by the teacher
     * @param isTeacher true if the user is a teacher false otherwise
     * @return the number of Draxo evaluations
     */
    fun getNbDraxoEvaluation(isTeacher: Boolean): Int {
        return if (isTeacher) nbDraxoEvaluations else nbDraxoEvaluations - nbDraxoEvaluationsHidden
    }
}