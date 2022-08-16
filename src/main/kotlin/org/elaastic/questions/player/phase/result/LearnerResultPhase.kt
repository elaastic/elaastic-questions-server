package org.elaastic.questions.player.phase.result

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.player.components.results.ResultsModelFactory
import org.elaastic.questions.player.phase.*

class LearnerResultPhase(
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
        "${PhaseTemplate.TEMPLATE_PACKAGE}/result/_learner-result-phase.html",
        "resultPhase"
    )
) {
    override val phaseType = LearnerPhaseType.RESULT
    override var learnerPhaseExecution: LearnerResultPhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if(learnerPhaseExecution is LearnerResultPhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override  fun getViewModel() = LearnerResultPhaseViewModel(
        learnerPhaseExecution!!.myResultsModel,
        ResultsModelFactory.build(
            teacher = false,
            sequence = this.learnerSequence.sequence,
            responseSet = learnerPhaseExecution!!.responseSet,
            userCanRefreshResults = learnerPhaseExecution!!.userCanRefreshResults,
            featureManager = learnerPhaseExecution!!.featureManager,
            messageBuilder = learnerPhaseExecution!!.messageBuilder,
        )
    )



}