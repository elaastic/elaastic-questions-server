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

import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.sequence.interaction.InteractionService
import org.elaastic.activity.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.questions.test.interpreter.command.Phase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class LearnerSequenceServiceTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val responseRepository: ResponseRepository,
    @Autowired val sequenceService: SequenceService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val learnerSequenceService: LearnerSequenceService,
    @Autowired val interactionService: InteractionService,
    @Autowired val sequenceRepository: SequenceRepository,
    @Autowired val functionalTestingService: FunctionalTestingService,
) {


    @Test
    fun countReportMade() {
        // Given
        val learners = integrationTestingService.getNLearners(2)
        val grader = learners[0]
        val reporter = learners[1]
        val teacher = integrationTestingService.getTestTeacher()
        val subject = functionalTestingService.createSubject(teacher)
        functionalTestingService.addQuestion(subject, QuestionType.OpenEnded)
        val assignement = functionalTestingService.createAssignment(subject)
        val sequence = assignement.sequences.first()
        assertEquals(0, learnerSequenceService.countReportMade(reporter, sequence), "The report count should be 0 as the sequence has not started yet")
        functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace) // Phase 1 (Start)
        val response = functionalTestingService.submitResponse(
            Phase.PHASE_1,
            reporter,
            sequence,
            true,
            ConfidenceDegree.CONFIDENT,
            "response"
        )
        functionalTestingService.nextPhase(sequence) // Phase 2 (Evaluation)

        tGiven("A peerGrading") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "Reportable content"),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tThen("The report count should be 0") { peerGrading ->
            functionalTestingService.nextPhase(sequence) // Phase 3 (Read)
            assertEquals(0, learnerSequenceService.countReportMade(reporter, sequence), "The report count should be 0")
            peerGrading
        }.tWhen("The reporter report it") { peerGrading ->
            peerGradingService.updateReport(reporter, peerGrading, listOf("reason"))
        }.tThen("The report count should be 1") {
            assertEquals(1, learnerSequenceService.countReportMade(reporter, sequence), "The report count should be 1")
        }

    }
}