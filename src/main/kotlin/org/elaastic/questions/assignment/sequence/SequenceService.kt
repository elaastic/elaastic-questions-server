package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
import org.elaastic.questions.assignment.sequence.interaction.InteractionRepository
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
@Transactional
class SequenceService(
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val fakeExplanationRepository: FakeExplanationRepository,
        @Autowired val interactionRepository: InteractionRepository
) {
    fun get(user: User, id: Long, fetchInteractions: Boolean = false): Sequence { // TODO Test
        return sequenceRepository.findOneById(id)?.let {sequence ->
            if (sequence.owner != user) throw AccessDeniedException("You are not autorized to access to this sequence")

            if(fetchInteractions) {
                interactionRepository.findAllBySequence(sequence).map {
                    sequence.interactions[it.interactionType] = it
                }
            }

            sequence
        } ?: throw EntityNotFoundException("There is no sequence for id \"$id\"")
    }

    fun findAllFakeExplanation(user: User, sequenceId: Long) : List<FakeExplanation> {
        return fakeExplanationRepository.findAllByStatement(
                get(user, sequenceId).statement
        )
    }
}