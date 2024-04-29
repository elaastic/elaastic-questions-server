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

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceRepository
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.LikertPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.Ignore
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ExplanationDataTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val peerGradingService: PeerGradingService,
) {

    @Autowired
    private lateinit var responseService: ResponseService

    @Test
    fun `test de la fonction getNbEvaluation`() {
        val grader: User = integrationTestingService.getNLearners(1).first()
        var response = integrationTestingService.getAnyResponse()
        val teacher: User = response.statement.owner

        assertEquals(0, response.evaluationCount)
        assertEquals(0, response.draxoEvaluationCount)
        assertEquals(0, response.draxoEvaluationHiddenCount)

        tGiven("A peerGrading of the response given by another student") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            ).tWhen("the peer grading is saved and varaibles are updated") { peerGrading ->
                peerGradingRepository.save(peerGrading)
                response = responseService.updateMeanGradeAndEvaluationCount(response)
                peerGrading
            }.tThen("variables are set as expected") { peerGrading ->
                assertEquals(1, peerGradingService.findAllDraxo(response).size)
                assertEquals(2, response.evaluationCount)
                assertEquals(1, response.draxoEvaluationCount)
                assertEquals(0, response.draxoEvaluationHiddenCount)
                peerGrading
            }.tThen("The number of evaluations should be 2 for the student and the teacher") { peerGrading ->
                response = responseService.responseRepository.findById(response.id!!).get()
                val explanationData = ExplanationData(response)
                assertEquals(2, explanationData.getNbEvaluation(false))
                assertEquals(2, explanationData.getNbEvaluation(true))
                peerGrading
            }.tWhen("The peerGrading is hidden by the teacher") { peerGrading ->
                peerGradingService.markAsHidden(teacher, peerGrading)
            }.tThen("The number of evaluations should be 1 for the student and 2 for the teacher") {
                response = responseService.responseRepository.findById(response.id!!).get()
                val explanationData = ExplanationData(response)
                assertEquals(1, explanationData.getNbEvaluation(false))
                assertEquals(2, explanationData.getNbEvaluation(true))
            }
        }
    }

    @Ignore
    @Test
    fun getNbDraxoEvaluation() {

    }
}