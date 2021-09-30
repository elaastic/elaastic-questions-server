package org.elaastic.questions.course

import org.elaastic.questions.directory.User
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import java.util.*
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@SpringBootTest
@Transactional
@Profile("test")
class CourseServiceIntegrationTest(
        @Autowired val courseService: CourseService,
        @Autowired val subjectService: SubjectService,
        @Autowired val testingService: TestingService
){

    @Test
    fun `findAllByOwner - no course`() {
        val teacher = testingService.getAnotherTestTeacher()

        courseService.findAllByOwner(teacher)
                .tExpect {
                    MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(0L))
                    MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(0))
                }
    }

    @Test
    fun `findAllByOwner - with courses`() {
        val teacher = testingService.getAnotherTestTeacher()
        createTestingData(teacher)

        courseService.findAllByOwner(teacher)
                .tExpect {
                    MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(10L))
                    MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(1))
                }

        courseService.findAllByOwner(teacher, PageRequest.of(0, 5))
                .tExpect {
                    MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(2))
                }
    }

    @Test
    fun `save a valid course`() {
        val teacher = testingService.getTestTeacher()
        val course = Course("A course", teacher)
        tWhen { courseService.save(course) }
                .tThen {
                    MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                    MatcherAssert.assertThat(UUID.fromString(it.globalId), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(it.title, CoreMatchers.equalTo("A course"))
                    MatcherAssert.assertThat(it.owner, CoreMatchers.equalTo(testingService.getTestTeacher()))
                }
    }

    @Test
    fun `a course must have a not blank title`() {
        val exception = org.junit.jupiter.api.assertThrows<ConstraintViolationException> {
            courseService.save(Course("", testingService.getTestTeacher()))
        }

        MatcherAssert.assertThat(exception.constraintViolations.size, CoreMatchers.equalTo(1))
        MatcherAssert.assertThat(
                exception.constraintViolations.elementAt(0).propertyPath.toString(),
                CoreMatchers.equalTo("title")
        )
    }

    @Test
    fun `add a subject without a course to a course`() {
        tGiven("A subject without a course and a course") {
            val subject = subjectService.save(Subject("a subject", testingService.getTestTeacher()))
            val course = courseService.save(Course("a course", testingService.getTestTeacher()))
            Pair<Subject,Course>(subject, course)
        }.tThen {
            MatcherAssert.assertThat(it.first.course,CoreMatchers.nullValue())
            MatcherAssert.assertThat(it.second.subjects.size, CoreMatchers.equalTo(0))
            it
        }.tWhen("Adding the subject to the course") {
            courseService.addSubjectToCourse(testingService.getTestTeacher(),it.first, it.second)
            it
        }.tThen {
            MatcherAssert.assertThat(it.first.course,CoreMatchers.equalTo(it.second))
            MatcherAssert.assertThat(it.second.subjects.size, CoreMatchers.equalTo(1))
        }
    }

    @Test
    fun `add a subject with a course to a new course`() {
        val course1 = courseService.save(Course("Course 1", testingService.getTestTeacher()))
        val subject = subjectService.save(Subject("a subject", testingService.getTestTeacher(), course = course1))
        val course2 = courseService.save(Course("a course", testingService.getTestTeacher()))
        tGiven("A subject with a course and another course") {
            subject ; course1 ; course2
        }.tThen {
            MatcherAssert.assertThat(subject.course,CoreMatchers.equalTo(course1))
            MatcherAssert.assertThat(course1.subjects.size, CoreMatchers.equalTo(1))
            MatcherAssert.assertThat(course2.subjects.size, CoreMatchers.equalTo(0))
        }.tWhen("Adding the subject to the course 2") {
            courseService.addSubjectToCourse(testingService.getTestTeacher(),subject, course2)
        }.tThen {
            MatcherAssert.assertThat(subject.course,CoreMatchers.equalTo(course2))
            MatcherAssert.assertThat(course2.subjects.size, CoreMatchers.equalTo(1))
            MatcherAssert.assertThat(course1.subjects.size, CoreMatchers.equalTo(0))
        }
    }

    private fun createTestingData(owner: User, n: Int = 10) {
        (1..n).forEach {
            courseService.save(
                    Course(
                            title = "Course n°$it",
                            owner = owner
                    )
            )
        }
    }

}