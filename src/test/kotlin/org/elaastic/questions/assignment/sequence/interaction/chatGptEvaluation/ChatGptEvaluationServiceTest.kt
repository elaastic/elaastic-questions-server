package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatGptEvaluationServiceTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired var chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired var responseRepository: ResponseRepository,
) {



    @BeforeEach
    @Transactional
    fun cleanup() {
        chatGptEvaluationRepository.deleteAll()
    }

    @Test
    fun `canHideGrading should return true if the user is the teacher of the sequence`() {
        // Given
        val response = integrationTestingService.getAnyResponse()
        val teacher: User = response.interaction.owner

        tGiven("A chatGPT evaluation") {
            ChatGptEvaluation(
                response = response,
                annotation = "annotation",
                grade = null,
                status = ChatGptEvaluationStatus.DONE.name,
                reportReasons = null,
                reportComment = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                removedByTeacher = false,
            )
        }.tThen("canHideGrading should return true") {
            assertTrue(chatGptEvaluationService.canHideEvaluation(it, teacher))
            it
        }.tThen("canHideGrading should return false") {
            val anotherUser = integrationTestingService.getTestStudent()
            assertNotEquals(teacher, anotherUser)
            assertFalse(chatGptEvaluationService.canHideEvaluation(it, anotherUser))
        }
    }

    @Test
    fun `canHideGrading should return false when the evaluation isn't DONE`() {
        // Given
        val response = integrationTestingService.getAnyResponse()
        val teacher: User = response.interaction.owner

        tGiven("A chatGPT evaluation") {
            ChatGptEvaluation(
                response = response,
                annotation = "annotation",
                grade = null,
                reportReasons = null,
                reportComment = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                removedByTeacher = false,
            )
        }.tWhen("The status isn't DONE") {
            it.status = ChatGptEvaluationStatus.PENDING.name
            assertNotEquals(ChatGptEvaluationStatus.DONE.name, it.status)
            it
        }.tThen("canHideGrading should return false") {
            assertFalse(chatGptEvaluationService.canHideEvaluation(it, teacher))
            it
        }.tWhen("The status is DONE") {
            it.status = ChatGptEvaluationStatus.DONE.name
            assertEquals(ChatGptEvaluationStatus.DONE.name, it.status)
            it
        }.tThen("canHideGrading should return true") {
            assertTrue(chatGptEvaluationService.canHideEvaluation(it, teacher))
        }
    }

    @Test
    fun `canHideGrading should return false when the user isn't the teacher of the sequence`() {
        // Given
        val response = integrationTestingService.getAnyResponse()
        val teacher: User = response.interaction.owner
        response.learner = integrationTestingService.getTestStudent()
        responseRepository.save(response)

        tGiven("A chatGPT evaluation") {
            ChatGptEvaluation(
                response = response,
                annotation = "annotation",
                grade = null,
                status = ChatGptEvaluationStatus.DONE.name,
                reportReasons = null,
                reportComment = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                removedByTeacher = false,
            )
        }.tThen("The user isn't the teacher of the sequence") {
            val anotherUser = integrationTestingService.getTestStudent()
            assertNotEquals(teacher, anotherUser)
            assertFalse(chatGptEvaluationService.canHideEvaluation(it, anotherUser))
            it
        }.tThen("the learner of the response isn't the teacher of the sequence") {
            assertNotEquals(teacher, response.learner)
            assertFalse(chatGptEvaluationService.canHideEvaluation(it, response.learner))
            it
        }.tThen("canHideGrading should return true with the teacher") {
            assertTrue(chatGptEvaluationService.canHideEvaluation(it, teacher))
        }
    }

    @Test
    fun `a teacher can hide a chatGPT evaluation in a sequence he own`() {
        // Given
        val response = integrationTestingService.getAnyResponse()
        val teacher: User = response.interaction.owner

        tGiven("A chatGPT evaluation") {
            ChatGptEvaluation(
                response = response,
                annotation = "annotation",
                grade = null,
                status = ChatGptEvaluationStatus.DONE.name,
                reportReasons = null,
                reportComment = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                removedByTeacher = false,
            ).tWhen {
                chatGptEvaluationRepository.save(it)
                it
            }
        }.tWhen("The teacher hide the evaluation") {
            assertDoesNotThrow {
                chatGptEvaluationService.markAsHidden(it, teacher)
            }
            it
        }.tThen("The evaluation is hidden") {
            assertTrue(it.hiddenByTeacher)
            it
        }
    }

    @Test
    fun `a student despit owning the response can't hide a chatGPT evaluation`() {
        // Given
        val response = integrationTestingService.getAnyResponse()
        response.learner = integrationTestingService.getTestStudent()
        responseRepository.save(response)
        val student: User = response.learner

        tGiven("A chatGPT evaluation") {
            ChatGptEvaluation(
                response = response,
                annotation = "annotation",
                grade = null,
                status = ChatGptEvaluationStatus.DONE.name,
                reportReasons = null,
                reportComment = null,
                utilityGrade = null,
                hiddenByTeacher = false,
                removedByTeacher = false,
            ).tWhen {
                chatGptEvaluationRepository.save(it)
                it
            }
        }.tWhen("The student try to hide the evaluation") {
            assertFalse(chatGptEvaluationService.canHideEvaluation(it, student))
            assertThrows(IllegalAccessException::class.java) {
                chatGptEvaluationService.markAsHidden(it, student)
            }
            it
        }.tThen("The evaluation is not hidden") {
            assertFalse(it.hiddenByTeacher)
            it
        }
    }
}