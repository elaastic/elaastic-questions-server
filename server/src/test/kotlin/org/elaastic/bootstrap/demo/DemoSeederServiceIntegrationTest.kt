/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.bootstrap.demo

import org.elaastic.assignment.AssignmentRepository
import org.elaastic.material.instructional.course.CourseRepository
import org.elaastic.material.instructional.subject.SubjectRepository
import org.elaastic.user.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class DemoSeederServiceIntegrationTest @Autowired constructor(
    private val demoSeederService: DemoSeederService,
    private val userService: UserService,
    private val courseRepository: CourseRepository,
    private val subjectRepository: SubjectRepository,
    private val assignmentRepository: AssignmentRepository,
    private val entityManager: EntityManager
) {

    @Test
    fun `seeds demo content on an empty database`() {
        val demoOwnerId = requireNotNull(userService.findByUsername(DEMO_OWNER_LOGIN)).id
        assertTrue(courseRepository.findAll().filterNotNull().none { it.owner.id == demoOwnerId })

        demoSeederService.seedDemoContent()
        entityManager.flush()
        entityManager.clear()

        val courses = courseRepository.findAll().filterNotNull()
        val mathsCourse = courses.single { it.title == "Maths 6e" }
        assertEquals(demoOwnerId, mathsCourse.owner.id)
        val subject = mathsCourse.subjects.single { it.title == "Fractions" }
        val question = subject.statements.single { it.title == "Tracer une équation linéaire" }
        assertEquals("CreateQuestion.png", question.attachment?.originalFileName)
        assertTrue(subject.assignments.isNotEmpty())
    }

    @Test
    fun `keeps existing demo content when seeded again`() {
        demoSeederService.seedDemoContent()
        entityManager.flush()
        entityManager.clear()

        val courseIds = courseRepository.findAll().filterNotNull().map { it.id }.toSet()
        assertTrue(courseIds.isNotEmpty())
        val subjectCount = subjectRepository.count()
        val assignmentCount = assignmentRepository.count()

        demoSeederService.seedDemoContent()
        entityManager.flush()
        entityManager.clear()

        assertEquals(courseIds, courseRepository.findAll().filterNotNull().map { it.id }.toSet())
        assertEquals(subjectCount, subjectRepository.count())
        assertEquals(assignmentCount, assignmentRepository.count())
    }
}
