package org.elaastic.questions.assignment.sequence

import javax.persistence.Transient

interface SequenceProgress {
    @Transient
    fun isNotStarted(): Boolean

    @Transient
    fun hasStarted(): Boolean

    @Transient
    fun isInProgress(): Boolean
}