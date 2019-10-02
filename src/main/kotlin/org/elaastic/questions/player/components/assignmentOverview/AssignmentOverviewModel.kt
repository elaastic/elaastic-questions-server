package org.elaastic.questions.player.components.assignmentOverview

data class AssignmentOverviewModel(
        val nbRegisteredUser: Int,
        val assignmentTitle: String,
        val sequences: List<SequenceInfo>,
        val selectedSequenceId: Long? = null,
        val hideStatementContent: Boolean = false
) {

    data class SequenceInfo(
            val id: Long,
            val title: String,
            val content: String,
            val icons: List<PhaseIcon>
    )
}

typealias PhaseIcon = String