package org.elaastic.questions.api.practice.subject

import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.Statement
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import java.time.LocalDateTime
import javax.transaction.Transactional
import org.hamcrest.MatcherAssert.*
import org.hamcrest.collection.IsEmptyCollection
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.persistence.EntityManager

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Profile("test")
internal class PracticeSubjectServiceIntegrationTest(
    @Autowired val practiceSubjectService: PracticeSubjectService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val subjectService: SubjectService,
) {

    @Autowired
    lateinit var entityManager: EntityManager

    @Test
    fun `findAllPracticeSubject - no subject`() {
        tWhen {
            // There is no subject updated since "tomorrow" :P
            practiceSubjectService.findAllPracticeSubject(LocalDateTime.now().plusDays(1))
        }
            .tExpect { result ->
                assertThat(result, IsEmptyCollection.empty())
            }
    }

    @Test
    fun `An assignment without sequence is not ready to practice`() {
        val teacher = integrationTestingService.getTestTeacher()

        with(functionalTestingService) {
            createSubject(teacher)
                .let { createAssignment(it) }
        }

        tWhen {
            practiceSubjectService.findAllPracticeSubject(LocalDateTime.now().minusDays(1))
        }
            .tExpect { result ->
                assertThat(result.size, equalTo(0))
            }
    }

    @Test
    fun `An assignment with sequences but none of them ready to practice is not ready to practice`() {
        val teacher = integrationTestingService.getTestTeacher()

        with(functionalTestingService) {
            createSubject(teacher)
                .also {
                    addQuestion(it, QuestionType.OpenEnded)
                    addQuestion(it, QuestionType.MultipleChoice)

                }
                .let { createAssignment(it) }
        }

        tWhen {
            practiceSubjectService.findAllPracticeSubject(LocalDateTime.now().minusDays(1))
        }
            .tExpect { result ->
                assertThat(result.size, equalTo(0))
            }
    }

    @Test
    fun `An assignment with some sequences ready to practice is itself ready to practice`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)

        with(functionalTestingService) {
            createSubject(teacher)
                .also {
                    addQuestion(it, QuestionType.OpenEnded)
                    addQuestion(it, QuestionType.MultipleChoice)

                }
                .let(this::createAssignment)
                .sequences.first().let(curriedRandomlyPlaySequence(learners))

        }

        tWhen {
            practiceSubjectService.findAllPracticeSubject(LocalDateTime.now().minusDays(1))
        }
            .tExpect { result ->
                assertThat(result.size, equalTo(1))
            }
    }

    @Test
    fun `A subject with 2 assignments ready to practice leads to 2 practice subjects`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)

        with(functionalTestingService) {
            createSubject(teacher)
                .also {
                    addQuestion(it, QuestionType.OpenEnded)
                    addQuestion(it, QuestionType.MultipleChoice)

                }
                // 1st assignment
                .also { createAssignment(it).let(curriedRandomlyPlayAllSequences(learners)) }
                // 2nd assignment
                .also { createAssignment(it).let(curriedRandomlyPlayAllSequences(learners)) }
        }

        tWhen {
            practiceSubjectService.findAllPracticeSubject(LocalDateTime.now().minusDays(1))
        }
            .tExpect { result ->
                assertThat(result.size, equalTo(2))
            }
    }

    @Test
    fun `findAllPracticeSubject since a date`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)
        val t0 = LocalDateTime.now()

        assertThat(
            "Initially there is no practice subject",
            practiceSubjectService.findAllPracticeSubject(t0).size,
            equalTo(0)
        )

        // Create a 1st subject
        val subject = with(functionalTestingService) {
            createSubject(teacher)
                .also {
                    addQuestion(it, QuestionType.OpenEnded)
                    addQuestion(it, QuestionType.MultipleChoice)

                }
                .also { subject ->
                    createAssignment(subject)
                        .sequences.forEach(curriedRandomlyPlaySequence(learners))
                }
        }

        entityManager.flush()

        Thread.sleep(1000)
        val t1 = LocalDateTime.now()

        assertThat(practiceSubjectService.findAllPracticeSubject(t0).size, equalTo(1))
        assertThat(practiceSubjectService.findAllPracticeSubject(t1).size, equalTo(0))

        // Adding a new question
        Thread.sleep(1000)
        subjectService.addStatement(subject, Statement.createExampleStatement(subject.owner))
        entityManager.flush()

        assertThat(practiceSubjectService.findAllPracticeSubject(t1).size, equalTo(1))

        // Updating a sequence
        Thread.sleep(1000)
        val t2 = LocalDateTime.now()
        Thread.sleep(1000)

        assertThat(practiceSubjectService.findAllPracticeSubject(t2).size, equalTo(0))

        subject.assignments.first().sequences.first()
            .also(functionalTestingService::reopenSequence)
            .also { entityManager.flush() }
            .also(functionalTestingService::stopSequence)
            .also {
                entityManager.flush()
            }

        assertThat(practiceSubjectService.findAllPracticeSubject(t2).size, equalTo(1))
    }

    @Test
    fun `When an assignment is not ready to practice, trying to get it throws an error`() {
        val teacher = integrationTestingService.getTestTeacher()

        val assignment = with(functionalTestingService) {
            createSubject(teacher)
                .let(this::createAssignment)
        }

        assertThat(assignment.id, notNullValue())

        val exception = assertThrows<IllegalStateException> {
            practiceSubjectService.getPracticeSubject(assignment.id!!)
        }
        assertThat(exception.message, equalTo("The subject ${assignment.id} is not ready to practice"))
    }

    @Test
    fun `The topic of a practice subject comes from the course of its assignment`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)

        with(functionalTestingService) {
            val course = createCourse(user = teacher, title = "My course")

            val assignment = createSubject(teacher, course)
                .also { subject -> addQuestion(subject, QuestionType.MultipleChoice) }
                .let { subject -> createAssignment(subject)  }
                .also(curriedRandomlyPlayAllSequences(learners))

            val practiceSubject = practiceSubjectService.getPracticeSubject(assignment.id!!)
            assertThat(practiceSubject.topic, notNullValue())
            assertThat(practiceSubject.topic?.id, equalTo(course.id))
            assertThat(practiceSubject.topic?.title, equalTo(course.title))
        }
    }

    @Test
    fun `The registered learners on an assignment are the learners of a practice subject`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)

        with(functionalTestingService) {
            val assignment = createSubject(teacher)
                .also { subject -> addQuestion(subject, QuestionType.MultipleChoice) }
                .let { subject -> createAssignment(subject)  }
                .also(curriedRandomlyPlayAllSequences(learners))

            val practiceSubject = practiceSubjectService.getPracticeSubject(assignment.id!!)

            assertThat(practiceSubject.learners.map(PracticeLearner::id), equalTo(learners.map(User::id)))
        }
    }

    @Test
    fun `A practice subjects must contains all the ready to practice questions and only them`() {
        val teacher = integrationTestingService.getTestTeacher()
        val learners = integrationTestingService.getNLearners(5)

        with(functionalTestingService) {
            val course = createCourse(user = teacher, title = "My course")

            val assignment = createSubject(teacher, course)
                .also { subject ->
                    addQuestion(subject, QuestionType.MultipleChoice)
                    addQuestion(subject, QuestionType.OpenEnded)
                    addQuestion(subject, QuestionType.ExclusiveChoice)
                    addQuestion(subject, QuestionType.OpenEnded)
                }
                .let { subject -> createAssignment(subject)  }
                .also {assignment ->
                    randomlyPlaySequence(learners, assignment.sequences[0])
                    randomlyPlaySequence(learners, assignment.sequences[2])
                }

            val practiceSubject = practiceSubjectService.getPracticeSubject(assignment.id!!)
            assertThat(practiceSubject.questions.size, equalTo(2))
            assertThat(practiceSubject.questions[0].title, equalTo(assignment.sequences[0].statement.title))
            assertThat(practiceSubject.questions[1].title, equalTo(assignment.sequences[2].statement.title))
        }
    }
}