package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.choice.legacy.LearnerChoice
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import java.math.BigDecimal

class TeacherExplanationData(
    responseId: Long,
    content: String? = null,
    author: String? = null,
    nbEvaluations: Int = 0,
    nbDraxoEvaluations: Int = 0,
    meanGrade: BigDecimal? = null,
    confidenceDegree: ConfidenceDegree? = null,
    score: BigDecimal? = null,
    correct: Boolean? = (score?.compareTo(BigDecimal(100)) == 0),
    choiceList: LearnerChoice? = null,
    nbDraxoEvaluationsHidden: Int = 0,
) : ExplanationData(
    responseId,
    content,
    author,
    nbEvaluations,
    nbDraxoEvaluations,
    meanGrade,
    confidenceDegree,
    score,
    correct,
    choiceList,
    nbDraxoEvaluationsHidden = nbDraxoEvaluationsHidden,
) {
    constructor(explanationData: ExplanationData) : this(
        responseId = explanationData.responseId,
        content = explanationData.content,
        author = explanationData.author,
        nbEvaluations = explanationData.nbEvaluations,
        nbDraxoEvaluations = explanationData.nbDraxoEvaluations,
        meanGrade = explanationData.meanGrade,
        confidenceDegree = explanationData.confidenceDegree,
        correct = explanationData.correct,
        score = explanationData.score,
        choiceList = explanationData.choiceList,
        nbDraxoEvaluationsHidden = explanationData.nbDraxoEvaluationsHidden,
    )

    override val fromTeacher = true
}