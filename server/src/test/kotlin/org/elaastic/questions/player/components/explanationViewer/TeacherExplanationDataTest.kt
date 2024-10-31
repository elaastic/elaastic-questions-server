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

package org.elaastic.questions.player.components.explanationViewer

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
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
            assertEquals(1, teacherExplanationData.responseId)
            assertEquals("content", teacherExplanationData.content)
            assertEquals("author", teacherExplanationData.author)
            assertEquals(2, teacherExplanationData.nbEvaluations)
            assertEquals(3, teacherExplanationData.nbDraxoEvaluations)
            assertNull(teacherExplanationData.meanGrade)
            assertNull(teacherExplanationData.confidenceDegree)
            assertNull(teacherExplanationData.score)
            assertNull(teacherExplanationData.correct)
            assertNull(teacherExplanationData.choiceList)
            assertEquals(4, teacherExplanationData.nbDraxoEvaluationsHidden)
            assertTrue(teacherExplanationData.fromTeacher)
        }


    }
}