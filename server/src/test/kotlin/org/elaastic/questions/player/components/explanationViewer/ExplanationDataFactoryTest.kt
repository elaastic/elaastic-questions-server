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

import org.elaastic.questions.assignment.sequence.interaction.response.Response
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
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ExplanationDataFactoryTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
) {

    @Test
    fun `test create`() {
        lateinit var response: Response
        tGiven("a response") {
            response = integrationTestingService.getAnyResponse()
        }.tWhen("we create an explanation data") {
            ExplanationDataFactory.create(response, false)
        }.tThen("we should regain the same data as the response") { explanationData ->
            assertEquals(response.id, explanationData.responseId)
            assertEquals(response.explanation, explanationData.content)
            assertEquals(response.learner.getDisplayName(), explanationData.author)
            assertEquals(response.evaluationCount, explanationData.nbEvaluations)
            assertEquals(response.draxoEvaluationCount, explanationData.nbDraxoEvaluations)
            assertEquals(response.meanGrade, explanationData.meanGrade)
            assertEquals(response.confidenceDegree, explanationData.confidenceDegree)
            assertEquals(response.score, explanationData.score)
            assertEquals(response.score?.compareTo(BigDecimal(100)) == 0, explanationData.correct)
            assertEquals(response.learnerChoice, explanationData.choiceList)
        }
    }
}