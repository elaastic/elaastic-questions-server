package org.elaastic.analytics.lrs

import org.elaastic.questions.assignment.sequence.Sequence
import org.springframework.data.jpa.repository.JpaRepository

interface EventLogRepository : JpaRepository<EventLog, Long> {

    fun deleteBySequenceIn(sequence: List<Sequence>): List<EventLog>
    fun findBySequenceIn(sequence: List<Sequence>): List<EventLog>
    fun countBySequenceIn(sequences: List<Sequence>): Int
}