package org.elaastic.sequence.phase.response

import org.elaastic.questions.assignment.sequence.ILearnerSequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.sequence.config.ResponseSubmissionSpecification
import org.elaastic.sequence.phase.*

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

    override val phaseType = LearnerPhaseType.RESPONSE
    override var learnerPhaseExecution: LearnerResponsePhaseExecution? = null

    override fun loadPhaseExecution(learnerPhaseExecution: LearnerPhaseExecution) {
        if (learnerPhaseExecution is LearnerResponsePhaseExecution)
            this.learnerPhaseExecution = learnerPhaseExecution
        else throw IllegalArgumentException()
    }

    override fun getViewModel(): PhaseViewModel = run {
        // TODO we should get rid of interaction here
        val interaction = learnerSequence.sequence.getResponseSubmissionInteraction()

        LearnerResponsePhaseViewModel(
            sequenceId = learnerSequence.sequence.id ?: error("Sequence must have an ID to get a response"),
            interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
            learnerPhaseState = state,
            responseSubmitted = learnerPhaseExecution?.responseSubmitted ?: false,
            responseFormModel = LearnerResponseFormViewModel(
                interactionId = interaction.id ?: error("Interaction must have an ID to get response"),
                attempt = 1,
                responseSubmissionSpecification = interaction.specification as ResponseSubmissionSpecification,
                hasChoices = learnerSequence.sequence.statement.hasChoices(),
                multipleChoice = learnerSequence.sequence.statement.isMultipleChoice(),
                nbItem = learnerSequence.sequence.statement.choiceSpecification?.nbCandidateItem
            )
        )
    }
}