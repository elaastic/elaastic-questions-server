/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.course



import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectRepository
import org.elaastic.questions.test.TestingService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

import java.util.*
import javax.transaction.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class CourseRepositoryIntegrationTest (
        @Autowired val courseRepository: CourseRepository,
        @Autowired val testingService: TestingService,
        @Autowired val subjectReposiitory: SubjectRepository
) {

    @Test
    fun `save a valid course`() {
        // Given : a course
        Course(
                "My test course",
                testingService.getAnyUser(),
                UUID.randomUUID().toString()
        ).let {
            // When having it
            courseRepository.saveAndFlush(it).let {
                // Then
                assertThat(it.id, not(nullValue()))
                assertThat(it.dateCreated, not(nullValue()))
                assertThat(it.lastUpdated, not(nullValue()))
                assertThat(it.version, equalTo(0L))
            }
        }
    }

    /*
    @Test
    fun `finding a course with subjects`(){
        // Given a course with subjects
        var course: Course = Course(
                "My test course",
                testingService.getAnyUser(),
                UUID.randomUUID().toString())

        courseRepository.saveAndFlush(course)

        Subject(
                "Title",
                course.title,
                testingService.getAnyUser(),
                UUID.randomUUID().toString()
        ).let {
            subjectReposiitory.saveAndFlush(it)}

        courseRepository.findOneWithSubjects(course.id!!).let{
            assertThat(it!!, equalToObject(course))
        }

    }*/
}
