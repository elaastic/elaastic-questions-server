package org.elaastic.questions.player.phase.evaluation.one_by_one

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.PhaseTemplate

class OneByOneLearnerEvaluationPhase(
    learnerSequence: ILearnerSequence,
    index: Int,
    active: Boolean,
    state: State,
) : LearnerPhase(
    learnerSequence,
    index, active,
    state,
    TEMPLATE
) {

    companion object {
        val TEMPLATE =
            PhaseTemplate(
                "${PhaseTemplate.TEMPLATE_PACKAGE}/evaluation/one-by-one/_evaluation-phase.html",
                "evaluationPhase"
            )
    }

    override val phaseType = OneByOneEvaluationPhaseType
    override var learnerPhaseExecution: OneByOneLearnerEvaluationPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if(learnerPhaseExecution is OneByOneLearnerEvaluationPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

     fun getViewModel(): OneByOneLearnerEvaluationPhaseViewModel =
         OneByOneLearnerEvaluationPhaseViewModelFactory.build(this)
}