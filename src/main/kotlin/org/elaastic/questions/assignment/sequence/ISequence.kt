package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.subject.statement.Statement
import javax.persistence.Transient

interface ISequence {

    @Transient
    fun isNotStarted(): Boolean

    @Transient
    fun hasStarted(): Boolean

    @Transient
    fun isInProgress(): Boolean

    @Transient
    fun getResponseSubmissionInteraction(): Interaction

    @Transient
    fun getEvaluationInteraction(): Interaction


    fun getInteractionAt(rank: Int): Interaction

    var statement: Statement
}