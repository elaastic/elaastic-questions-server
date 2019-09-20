package org.elaastic.questions.assignment.sequence

import org.springframework.data.jpa.repository.JpaRepository


interface LearnerSequenceRepository : JpaRepository<LearnerSequence, Long> {
    fun findAllBySequence(sequence: Sequence): List<LearnerSequence>
}
