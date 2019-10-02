package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.directory.User
import org.springframework.data.jpa.repository.JpaRepository


interface LearnerSequenceRepository : JpaRepository<LearnerSequence, Long> {
    fun findAllBySequence(sequence: Sequence): List<LearnerSequence>

    fun findByLearnerAndSequence(learner: User, sequence: Sequence): LearnerSequence?
}
