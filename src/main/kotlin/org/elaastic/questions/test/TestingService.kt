package org.elaastic.questions.test

import org.elaastic.questions.assignment.*
import org.elaastic.questions.assignment.sequence.*
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.assignment.sequence.interaction.InteractionRepository
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserRepository
import org.elaastic.questions.lti.*
import org.elaastic.questions.lti.controller.LtiLaunchData
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
        @Autowired val responseRepository: ResponseRepository,
        @Autowired val assignmentService: AssignmentService,
        @Autowired val ltiConsumerRepository: LtiConsumerRepository,
        @Autowired val roleService: RoleService
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

    fun getLastStatement(): Statement {
        return statementRepository.findAll().last()
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

    fun getAnyInteractionResponse() : Response {
        return responseRepository.findAll().iterator().next()
    }

    fun getTestAssignment() : Assignment {
        return assignmentService.get(382)
    }

    fun getAnyLtiConsumer(): LtiConsumer {
        return ltiConsumerRepository.findAll().iterator().next()
    }

    fun getLtiLaunchDataComingFromBoBDeniroTeacher(): LtiLaunchData {
        return LtiLaunchData(
                oauth_consumer_key = getAnyLtiConsumer().key,
                user_id = "lti_user_id",
                roles = "Instructor",
                lis_person_name_given = "Bob",
                lis_person_name_family = "Deniro",
                lis_person_contact_email_primary = "bob@elaastic.org",
                context_id = "course_id",
                context_title = "A spendid course",
                resource_link_id = "activity_if",
                resource_link_title = "A great activity"
        ).let {
            it.roleService = roleService
            it
        }
    }

    fun getLtiLaunchDataWithBadGlobalId(): LtiLaunchData {
        return LtiLaunchData(
                oauth_consumer_key = getAnyLtiConsumer().key,
                user_id = "lti_user_id",
                roles = "Instructor",
                lis_person_name_given = "Bob",
                lis_person_name_family = "Deniro",
                lis_person_contact_email_primary = "bob@elaastic.org",
                context_id = "course_id",
                context_title = "A spendid course",
                resource_link_id = "activity_id_2",
                resource_link_title = "A great activity",
                custom_assignmentid = "Bad_one"
        ).let {
            it.roleService = roleService
            it
        }
    }

}

