package org.elaastic.questions.player.components.feedback

import org.elaastic.questions.assignment.sequence.interaction.feedback.TeacherConfidenceDegree

data class TeacherFeebackModel(
        val userId: Long,
        val sequenceId: Long,
        val hasSubmitedFeedback: Boolean,
        val reuseDegree: Int?,
        val recommendDegree: Int?,
        val explanation: String?,
        val teacherConfidenceDegreeValues: Array<TeacherConfidenceDegree> = TeacherConfidenceDegree.values()
) { }
