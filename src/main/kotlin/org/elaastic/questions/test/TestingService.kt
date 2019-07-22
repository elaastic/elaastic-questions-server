package org.elaastic.questions.test

import org.elaastic.questions.assignment.*
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class TestingService(
        @Autowired val userRepository: UserRepository,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val interactionResponseRepository: InteractionResponseRepository
) {

    fun getAnyUser(): User {
        return userRepository.findAll().iterator().next()
    }

    fun getAnyStatement(): Statement {
        return statementRepository.findAll().iterator().next()
    }

    fun getAnyInteraction(): Interaction {
        return interactionRepository.findAll().iterator().next()
    }

    fun getAnySequence() : Sequence {
        return sequenceRepository.findAll().iterator().next()
    }

    fun getAnyAssignment() : Assignment {
        return assignmentRepository.findAll().iterator().next()
    }

    fun getAnyInteractionResponse() : InteractionResponse {
        return interactionResponseRepository.findAll().iterator().next()
    }
}