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

package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.subject.statement.StatementService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LearnerSequenceServiceTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseService: ResponseService,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val sequenceRepository: SequenceRepository,
    @Autowired val sequenceService: SequenceService,
    @Autowired val assignmentService: AssignmentService,
    @Autowired val statementService: StatementService,
    @Autowired val userService: UserService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val learnerSequenceRepository: LearnerSequenceRepository
) {

    @Autowired
    private lateinit var learnerSequenceService: LearnerSequenceService

    @Test
    fun countReportMade() {
        // Given
        val response = Response(
            integrationTestingService.getTestStudent(),
            integrationTestingService.getAnyInteraction(),
            1,
            null,
            null,
            null,
            null,
            null,
            false,
            0,
            0,
            integrationTestingService.getAnyStatement(),
            hiddenByTeacher = false,
            recommendedByTeacher = false
        )
        responseRepository.save(response)
        val sequence = response.interaction.sequence
        val reporter = response.learner
        val grader = integrationTestingService.getNLearners(1).first()

        assertEquals(0, response.evaluationCount)

        tGiven("A peerGrading") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tThen("The report count should be 0") { peerGrading ->
            assertEquals(0, learnerSequenceService.countReportMade(reporter, sequence))
            peerGrading
        }.tWhen ("The reporter report it") { peerGrading ->
            peerGradingService.updateReport(reporter, peerGrading, listOf("reason"))
        }.tThen("The report count should be 1") {
            assertEquals(1, learnerSequenceService.countReportMade(reporter, sequence))
        }

    }
}