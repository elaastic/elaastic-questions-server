package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionType
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class LearnerSequenceService(
        @Autowired val learnerSequenceRepository: LearnerSequenceRepository
) {

    fun getActiveInteractionForLearner(learner: User,
                                       sequence: Sequence): Interaction? =
            when {
                sequence.executionIsFaceToFace() -> sequence.activeInteraction
                else -> findOrCreateLearnerSequence(learner, sequence).activeInteraction
            }

    fun findOrCreateLearnerSequence(learner: User,
                                    sequence: Sequence) : LearnerSequence =
            learnerSequenceRepository.findByLearnerAndSequence(
                    learner,
                    sequence
            ).let {
                it ?: LearnerSequence(learner, sequence)
                        .let { learnerSequenceRepository.save(it) }
            }.let {
                if (it.activeInteraction == null && sequence.activeInteraction != null) {
                    it.activeInteraction = sequence.interactions[InteractionType.ResponseSubmission]
                    learnerSequenceRepository.save(it)
                }
                else it
            }
}