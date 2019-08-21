package org.elaastic.questions.test

import org.elaastic.questions.assignment.*
import org.elaastic.questions.assignment.sequence.*
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionRepository
import org.elaastic.questions.assignment.sequence.interaction.InteractionResponse
import org.elaastic.questions.assignment.sequence.interaction.InteractionResponseRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.IllegalStateException

@Service
class TestingService(
        @Autowired val userRepository: UserRepository,
        @Autowired val statementRepository: StatementRepository,
        @Autowired val interactionRepository: InteractionRepository,
        @Autowired val sequenceRepository: SequenceRepository,
        @Autowired val assignmentRepository: AssignmentRepository,
        @Autowired val interactionResponseRepository: InteractionResponseRepository,
        @Autowired val assignmentService: AssignmentService
) {

    fun getAnyUser(): User {
        return userRepository.findAll().iterator().next()
    }

    fun getTestTeacher(): User {
        return userRepository.getByUsername("fsil")
    }

    fun getAnotherTestTeacher(): User  {
        return userRepository.getByUsername("aein")
    }

    fun getTestStudent(): User {
        return userRepository.getByUsername("msil")
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
                ?: throw IllegalStateException("There is no assignment is testing data")
    }

    fun getAnyInteractionResponse() : InteractionResponse {
        return interactionResponseRepository.findAll().iterator().next()
    }

    fun getTestAssignment() : Assignment {
        return assignmentService.get(382)
    }
}