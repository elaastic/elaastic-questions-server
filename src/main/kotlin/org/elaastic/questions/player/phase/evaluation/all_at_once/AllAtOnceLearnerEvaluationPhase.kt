package org.elaastic.questions.player.phase.evaluation.all_at_once

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.LearnerPhase
import org.elaastic.questions.player.phase.PhaseTemplate
import org.elaastic.questions.player.phase.evaluation.AbstractLearnerEvaluationPhaseViewModel

class AllAtOnceLearnerEvaluationPhase(
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
                "${PhaseTemplate.TEMPLATE_PACKAGE}/evaluation/all-at-once/_evaluation-phase.html",
                "evaluationPhase"
            )
    }

    override val phaseType = AllAtOnceEvaluationPhaseType
    override var learnerPhaseExecution: AllAtOnceLearnerEvaluationPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if(learnerPhaseExecution is AllAtOnceLearnerEvaluationPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

     fun getViewModel(): AbstractLearnerEvaluationPhaseViewModel =
         AllAtOnceLearnerEvaluationPhaseViewModelFactory.build(this)
}