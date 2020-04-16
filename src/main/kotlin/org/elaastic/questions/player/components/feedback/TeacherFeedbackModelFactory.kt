package org.elaastic.questions.player.components.feedback

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.feedback.TeacherFeedback
import org.elaastic.questions.directory.User
import java.lang.IllegalStateException

object TeacherFeedbackModelFactory {

    fun build(user: User, sequence: Sequence, teacherFeedback: TeacherFeedback?): TeacherFeedbackModel =
            TeacherFeedbackModel(
                    userId = user.id?: throw IllegalStateException("This sequence has no ID"),
                    sequenceId = sequence.id ?: throw IllegalStateException("This sequence has no ID"),
                    hasSubmitedFeedback = teacherFeedback != null,
                    recommendDegree = teacherFeedback?.recommendRating,
                    reuseDegree = teacherFeedback?.reuseRating,
                    explanation = teacherFeedback?.explanation
            )
}