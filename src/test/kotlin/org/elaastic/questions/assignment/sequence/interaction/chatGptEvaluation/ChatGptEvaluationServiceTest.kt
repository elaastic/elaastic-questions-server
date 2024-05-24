package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatGptEvaluationServiceTest (
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
) {

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
}