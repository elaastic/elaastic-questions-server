package org.elaastic.questions.assignment.sequence

import org.elaastic.sequence.interaction.Interaction
import org.elaastic.user.User
import org.elaastic.sequence.phase.LearnerPhase
import java.lang.IllegalStateException

class TransientLearnerSequence(
    override val learner: User,
    override val sequence: Sequence
) : ILearnerSequence, SequenceProgress by sequence {
    @Suppress("UNUSED_PARAMETER")
    override var activeInteraction: Interaction?
        get() = sequence.activeInteraction
        set(ignore) = throw IllegalStateException("Setting activeInteraction is not allowed on TransientLearnerSequence")

    override var phaseList: Array<LearnerPhase?> = arrayOf<LearnerPhase?>(null, null, null)

    override fun loadPhase(learnerPhase: LearnerPhase) {
        phaseList[learnerPhase.index - 1] = learnerPhase
    }

    override fun getPhase(index: Int) = phaseList[index - 1]!!
}