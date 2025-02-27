package org.elaastic.sequence

import org.elaastic.sequence.interaction.Interaction
import org.elaastic.sequence.phase.LearnerPhase
import org.elaastic.user.User

interface ILearnerSequence : SequenceProgress {

    val learner: User
    val sequence: Sequence

    var activeInteraction: Interaction?

    fun loadPhase(learnerPhase: LearnerPhase)

    fun getPhase(index: Int): LearnerPhase

    val phaseList: Array<LearnerPhase?>
}