package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
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
        @Autowired val fakeExplanationRepository: FakeExplanationRepository
) {
    fun get(user: User, id: Long): Sequence {
        return sequenceRepository.findOneById(id)?.let {
            if (it.owner != user) throw AccessDeniedException("You are not autorized to access to this sequence")
            it
        } ?: throw EntityNotFoundException("There is no sequence for id \"$id\"")
    }

    fun findAllFakeExplanation(user: User, sequenceId: Long) : List<FakeExplanation> {
        return fakeExplanationRepository.findAllByStatement(
                get(user, sequenceId).statement
        )
    }
}