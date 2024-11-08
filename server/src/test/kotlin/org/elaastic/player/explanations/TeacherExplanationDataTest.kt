package org.elaastic.player.explanations

import org.elaastic.material.instructional.subject.SubjectService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.user.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class TeacherExplanationDataTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
) {

    @Test
    fun `test constructor`() {
        lateinit var explanationData: ExplanationData
        tGiven {
            explanationData = ExplanationData(
                responseId = 1,
                content = "content",
                author = "author",
                nbEvaluations = 2,
                nbDraxoEvaluations = 3,
                meanGrade = null,
                confidenceDegree = null,
                score = null,
                correct = null,
                choiceList = null,
                nbDraxoEvaluationsHidden = 4,
            )
        }.tWhen {
            TeacherExplanationData(explanationData)
        }.tThen { teacherExplanationData ->
            Assertions.assertEquals(1, teacherExplanationData.responseId)
            Assertions.assertEquals("content", teacherExplanationData.content)
            Assertions.assertEquals("author", teacherExplanationData.author)
            Assertions.assertEquals(2, teacherExplanationData.nbEvaluations)
            Assertions.assertEquals(3, teacherExplanationData.nbDraxoEvaluations)
            Assertions.assertNull(teacherExplanationData.meanGrade)
            Assertions.assertNull(teacherExplanationData.confidenceDegree)
            Assertions.assertNull(teacherExplanationData.score)
            Assertions.assertNull(teacherExplanationData.correct)
            Assertions.assertNull(teacherExplanationData.choiceList)
            Assertions.assertEquals(4, teacherExplanationData.nbDraxoEvaluationsHidden)
            Assertions.assertTrue(teacherExplanationData.fromTeacher)
        }


    }
}