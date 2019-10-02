package org.elaastic.questions.player.components.assignmentOverview

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.State
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType

object AssignmentOverviewModelFactory {

    fun build(teacher: Boolean,
              nbRegisteredUser: Int,
              assignmentTitle: String,
              sequences: List<Sequence>,
              sequenceToUserActiveInteraction: Map<Sequence, Interaction?>,
              selectedSequenceId: Long? = null,
              hideStatementContent: Boolean = false
    ): AssignmentOverviewModel = AssignmentOverviewModel(
            nbRegisteredUser = nbRegisteredUser,
            assignmentTitle = assignmentTitle,
            sequences = sequences.map {
                AssignmentOverviewModel.SequenceInfo(
                        id = it.id!!,
                        title = it.statement.title,
                        content = it.statement.content,
                        icons = resolveIcons(
                                teacher,
                                it,
                                sequenceToUserActiveInteraction[it])
                )
            },
            selectedSequenceId = selectedSequenceId,
            hideStatementContent = hideStatementContent
    )

    private fun resolveIcons(teacher: Boolean,
                             sequence: Sequence,
                             userActiveInteraction: Interaction?): List<PhaseIcon> =
            if (sequence.executionIsFaceToFace() || !teacher) {
                when {
                    sequence.isStopped() ->
                        if (sequence.resultsArePublished)
                            listOf("grey bar chart outline")
                        else listOf("big grey lock")

                    else -> when (userActiveInteraction?.interactionType) {
                        null -> listOf("big grey minus")
                        InteractionType.ResponseSubmission -> listOf("big grey comment outline")
                        InteractionType.Evaluation -> listOf("big grey comments outline")
                        InteractionType.Read -> listOf("big grey bar chart outline")
                    }

                }

            } else { // Distance & blended for teacher
                when (sequence.state) {
                    State.beforeStart -> listOf("big grey minus")
                    State.afterStop ->
                        if (sequence.resultsArePublished)
                            listOf("big grey bar chart outline")
                        else listOf("big grey lock")
                    else ->
                        if (sequence.resultsArePublished)
                            listOf(
                                    "large grey comment outline",
                                    "large grey comments outline",
                                    "large  grey bar chart outline"
                            )
                        else listOf(
                                "large grey comment outline",
                                "large grey comments outline"
                        )
                }
            }


}