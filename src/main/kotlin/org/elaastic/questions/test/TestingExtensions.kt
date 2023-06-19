package org.elaastic.questions.test

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.subject.Subject

fun Subject.getAnyAssignment() =
    if (this.assignments.isEmpty()) {
        throw IllegalStateException("This subject has no assignments")
    } else this.assignments.shuffled().find { true }!!

fun Assignment.getAnySequence() =
    if (this.sequences.isEmpty()) {
        throw IllegalStateException("This assignment has no sequences")
    } else this.sequences.shuffled().find { true }!!

fun Assignment.getAnyNSequences(n: Int) =
    if (this.sequences.size < n) {
        throw IllegalStateException("This assignment has less than $n sequences...")
    } else this.sequences.shuffled().take(n)

