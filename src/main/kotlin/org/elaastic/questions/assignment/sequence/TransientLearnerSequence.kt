package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.phase.LearnerPhase
import java.lang.IllegalStateException

class TransientLearnerSequence(
    override val learner: User,
    override val sequence: Sequence
) : ILearnerSequence, ISequence by sequence {
    override var activeInteraction: Interaction?
        get() = sequence.activeInteraction
        set(v) = throw IllegalStateException("Setting activeInteraction is not allowed on TransientLearnerSequence")

    // TODO Should be factorized with LearnerSequence class
    override var phaseList: Array<LearnerPhase?> = arrayOf<LearnerPhase?>(null, null, null)
        get() { // Needed because of JPA using a default empty constructor that bypass the var initialization...
            if (field == null) {
                field = arrayOf<LearnerPhase?>(null, null, null)
            }
            return field
        }

    override fun loadPhase(learnerPhase: LearnerPhase) {
        phaseList[learnerPhase.index - 1] = learnerPhase
    }

    override fun getPhase(index: Int) = phaseList[index - 1]!!
}