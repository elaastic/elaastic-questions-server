package org.elaastic.questions.assignment.sequence.interaction.feedback

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.User
import org.springframework.data.jpa.repository.JpaRepository

interface TeacherFeedbackRepository : JpaRepository<TeacherFeedback, Long> {
    fun findByTeacherAndSequence(teacher: User, sequence: Sequence): TeacherFeedback?
    fun findAllBySequence(sequence: Sequence): List<TeacherFeedback>?
}