package org.elaastic.questions.course

import org.elaastic.questions.directory.User
import org.elaastic.questions.test.TestingService
import org.elaastic.questions.test.directive.tExpect
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import java.util.*
import javax.validation.ConstraintViolationException
import kotlin.collections.ArrayList

class CourseServiceTest(
        @Autowired val courseService: CourseService,
        @Autowired val testingService: TestingService
){

    @Test
    fun `findAllByOwner - no subject`() {
        val teacher = testingService.getTestTeacher()

        courseService.findAllByOwner(teacher)
                .tExpect {
                    MatcherAssert.assertThat(it.totalElements, CoreMatchers.equalTo(0L))
                    MatcherAssert.assertThat(it.totalPages, CoreMatchers.equalTo(0))
                }
    }

    @Test
    fun `findAllByOwner - with subjects`() {
        val teacher = testingService.getTestTeacher()
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
    fun `save a valid subject`() {
        val teacher = testingService.getTestTeacher()
        val subject = courseService.save( Course("Subject", teacher))
        tWhen { courseService.save(subject) }
                .tThen {
                    MatcherAssert.assertThat(it.id, CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(it.version, CoreMatchers.equalTo(0L))
                    MatcherAssert.assertThat(UUID.fromString(it.globalId), CoreMatchers.notNullValue())
                    MatcherAssert.assertThat(it.title, CoreMatchers.equalTo("Subject"))
                    MatcherAssert.assertThat(it.owner, CoreMatchers.equalTo(testingService.getTestTeacher()))
                }
    }

    @Test
    fun `a subject must have a not blank title`() {
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
    fun `findByGlobalId - not existing value`() {
        MatcherAssert.assertThat(
                courseService.findByGlobalId("not existing"),
                CoreMatchers.nullValue()
        )
    }

    @Test
    fun `findByGlobalId - existing value`() {
        val teacher = testingService.getTestTeacher()
        courseService.save(
                Course(
                        title = "An assignment",
                        owner = teacher
                )
        ).tExpect {
            MatcherAssert.assertThat(
                    courseService.findByGlobalId(it.globalId),
                    CoreMatchers.equalTo(it)
            )
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