package org.elaastic.questions.player.phase.response

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.phase.LearnerPhaseExecution
import org.elaastic.questions.player.phase.*

class LearnerResponsePhase(
    learnerSequence: ILearnerSequence,
    index: Int,
    active: Boolean,
    state: State,
) : LearnerPhase(
    learnerSequence,
    index,
    active,
    state,
    PhaseTemplate(
        "${PhaseTemplate.TEMPLATE_PACKAGE}/response/_response-phase.html",
        "responsePhase"
    )
) {

    override val phaseType = ResponsePhaseType
    override var learnerPhaseExecution: LearnerResponsePhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if (learnerPhaseExecution is LearnerResponsePhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }


    fun getViewModel() = LearnerResponsePhaseViewModelFactory.build(this)

}