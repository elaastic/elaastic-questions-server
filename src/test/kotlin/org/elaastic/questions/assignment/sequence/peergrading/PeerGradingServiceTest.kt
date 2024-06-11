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

package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.ExecutionContext
import org.elaastic.questions.assignment.LearnerAssignment
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.sequence.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.ReportReason
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.Criteria
import org.elaastic.questions.assignment.sequence.peergrading.draxo.option.OptionId
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.UserService
import org.elaastic.questions.subject.SubjectService
import org.elaastic.questions.test.FunctionalTestingService
import org.elaastic.questions.test.IntegrationTestingService
import org.elaastic.questions.test.directive.tGiven
import org.elaastic.questions.test.directive.tThen
import org.elaastic.questions.test.directive.tWhen
import org.elaastic.questions.test.interpreter.command.Phase
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class PeerGradingServiceTest(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val userService: UserService,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val entityManager: EntityManager,
    @Autowired val subjectService: SubjectService,
    @Autowired val functionalTestingService: FunctionalTestingService,
    @Autowired val roleService: RoleService,
) {

    @Test
    fun `a student can report a peerGrading`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val reporter = integrationTestingService.getTestStudent()
        response.learner = reporter
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We report it") {
            val reportReason: List<String> = listOf(ReportReason.INCOHERENCE.name)
            assertDoesNotThrow {
                peerGradingService.updateReport(reporter, peerGrading, reportReason)
            }
        }.tThen("the peerGrading is reported") {
            assertNotNull(peerGrading.reportReasons)
            assertTrue(peerGrading.reportReasons!!.contains(ReportReason.INCOHERENCE.name))
        }
    }

    @Test
    fun `can't report an draxo peerGrading where there is no content to report`() {
        val response = integrationTestingService.getAnyResponse()
        val reporter = integrationTestingService.getTestStudent()
        response.learner = reporter
        lateinit var peerGrading: DraxoPeerGrading
        tGiven("A draxo peerGrading without a content to report") {
            val grader = integrationTestingService.getAnyUser()
            // A peerGrading where there is no content to report, is a peerGrading where there is no comment
            // We can achieve the zero-comment peerGrading by giving all the criteria a positive option
            peerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.YES)
                    .addEvaluation(Criteria.A, OptionId.YES)
                    .addEvaluation(Criteria.X, OptionId.YES)
                    .addEvaluation(Criteria.O, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
            // We check that the peerGrading has no comment
            assertNull(peerGrading.getDraxoEvaluation().getExplanation(), "The peerGrading should not have a comment")
        }.tWhen("We try report it") {
            val reportReason: List<String> = listOf(ReportReason.INCOHERENCE.name)
            reportReason
        }.tThen("We should get an exception") { reportReason ->
            assertThrows(IllegalStateException::class.java) {
                peerGradingService.updateReport(reporter, peerGrading, reportReason)
            }
        }.tThen("The peerGrading is not reported") {
            assertNull(peerGrading.reportReasons)
        }
    }

    @Test
    fun `report a Draxo peerGrading with an explanation`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val reporter = integrationTestingService.getTestStudent()
        response.learner = reporter
        lateinit var peerGrading: DraxoPeerGrading
        val comment = "This is a comment to explain the reason of the report"

        tGiven("A draxo peer grading") {
            peerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.NO, "Reportable content"),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We report it") {
            val reportReason: List<String> = listOf(ReportReason.INCOHERENCE.name)
            assertDoesNotThrow {
                peerGradingService.updateReport(reporter, peerGrading, reportReason, comment)
            }
            reportReason
        }.tThen("the peerGrading is reported") { reportReason ->
            assertNotNull(peerGrading.reportReasons)
            assertEquals(comment, peerGrading.reportComment)
            assertTrue(peerGrading.reportReasons!!.contains(reportReason.first()))
        }
    }

    @Test
    fun `a report with the reason 'OTHER' and without comment should not be possible`() {
        val response = integrationTestingService.getAnyResponse()
        val reporter = integrationTestingService.getTestStudent()
        response.learner = reporter
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            val grader = integrationTestingService.getAnyUser()
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We try report it with the reason 'OTHER' without a comment (e.i. empty comment or comment null)") {
            val reportReason: List<String> = listOf(ReportReason.OTHER.name)
            reportReason
        }.tThen("We should get an exception") { reportReason ->
            val emptyComment = ""
            assertThrows(IllegalArgumentException::class.java) {
                peerGradingService.updateReport(reporter, peerGrading, reportReason, emptyComment)
            }
            val nullComment = null
            assertThrows(IllegalArgumentException::class.java) {
                peerGradingService.updateReport(reporter, peerGrading, reportReason, nullComment)
            }
        }.tThen("The peerGrading is not reported") {
            assertNull(peerGrading.reportReasons)
        }
    }

    @Test
    fun `report a peerGrading with the reason 'OTHER' and a comment`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val reporter = integrationTestingService.getTestStudent()
        val comment = "This is a comment to explain the reason of the report"
        response.learner = reporter
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We report it with the reason 'OTHER' and a comment") {
            val reportReason: List<String> = listOf(ReportReason.OTHER.name)
            peerGradingService.updateReport(reporter, peerGrading, reportReason, comment)
        }.tThen("the peerGrading is reported") {
            assertNotNull(peerGrading.reportReasons)
            assertEquals(comment, peerGrading.reportComment)
            assertNotNull(peerGrading.reportReasons)
            assertTrue(peerGrading.reportReasons!!.contains(ReportReason.OTHER.name))
        }
    }

    @Test
    fun `a student can give to a peerGrading a UtilityGrade`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val learner = integrationTestingService.getTestStudent()
        response.learner = learner
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We give it a UtilityGrade") {
            val utilityGrade = UtilityGrade.AGREE
            peerGradingService.updateUtilityGrade(learner, peerGrading, utilityGrade)
        }.tThen("the peerGrading has a UtilityGrade") {
            assertNotNull(peerGrading.utilityGrade)
            assertEquals(UtilityGrade.AGREE, peerGrading.utilityGrade)
        }
    }

    @Test
    fun `a sudent who don't own the response can't report or give an UtilityGrade`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val learner = integrationTestingService.getTestStudent()
        val reporterWhoDontOwnTheResponse = integrationTestingService.getNLearners(1).first()
        response.learner = learner
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
            // We check that the reporterWhoDontOwnTheResponse is not the learner
            assertNotEquals(reporterWhoDontOwnTheResponse, learner)
        }.tWhen("We try to report it") {
            val reportReason: List<String> = listOf(ReportReason.INCOHERENCE.name)
            assertThrows(IllegalAccessException::class.java) {
                peerGradingService.updateReport(reporterWhoDontOwnTheResponse, peerGrading, reportReason)
            }
        }.tWhen("We try to give it a UtilityGrade") {
            val utilityGrade = UtilityGrade.AGREE
            assertThrows(IllegalAccessException::class.java) {
                peerGradingService.updateUtilityGrade(reporterWhoDontOwnTheResponse, peerGrading, utilityGrade)
            }
        }
    }

    @Test
    fun `a teacher can hide a peerGrading`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val teacher = integrationTestingService.getTestTeacher()
        response.interaction.sequence.assignment!!.owner = teacher

        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We hide it") {
            assertDoesNotThrow({
                peerGradingService.markAsHidden(teacher, peerGrading)
            }, "A teacher can hide a peerGrading")
        }.tThen("the peerGrading is hidden") {
            assertTrue(peerGrading.hiddenByTeacher)
        }.tWhen("a student (or a teacher that doesn't own the sequence ) try hidding a peerGrading") {}
            .tThen("an exception is thrown") {
                assertThrows<IllegalAccessException> {
                    peerGradingService.markAsHidden(grader, peerGrading)
                }
                val anotherTeacher = integrationTestingService.getAnyUser()
                anotherTeacher.addRole(roleService.roleTeacher())
                assertNotEquals(teacher, anotherTeacher)
                assertThrows<IllegalAccessException> {
                    peerGradingService.markAsHidden(anotherTeacher, peerGrading)
                }
            }
    }

    @Test
    fun `a teacher unhide a peerGrading`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val teacher = response.interaction.sequence.assignment!!.owner
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tWhen("A teacher try unhiding a peerGrading that is not hidden") {}.tThen("Nothing is thrown") {
            assertDoesNotThrow({
                peerGradingService.markAsShow(teacher, peerGrading)
            }, "A teacher can unhide a peerGrading that is not hidden")
        }.tWhen("We hide it") {
            peerGradingService.markAsHidden(teacher, peerGrading)
            assertTrue(peerGrading.hiddenByTeacher)
        }.tWhen("We unhide it") {
            assertDoesNotThrow({
                peerGradingService.markAsShow(teacher, peerGrading)
            }, "A teacher can unhide a peerGrading")
        }.tThen("the peerGrading is unhidden") {
            assertFalse(peerGrading.hiddenByTeacher)
        }
    }

    @Test
    fun `a student (or a student who doesn't own the sequence) can't unhide a peerGrading`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val teacher = integrationTestingService.getTestTeacher()
        response.interaction.sequence.assignment!!.owner = teacher
        lateinit var peerGrading: PeerGrading
        tGiven("A peer grading") {
            peerGrading = LikertPeerGrading(
                grade = BigDecimal(2),
                annotation = "Reportable content",
                grader = grader,
                response = response
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We hide it") {
            peerGradingService.markAsHidden(teacher, peerGrading)
            assertTrue(peerGrading.hiddenByTeacher)
        }.tWhen("A student try unhiding a peerGrading") {}.tThen("an exception is thrown") {
            assertNotEquals(teacher, grader)
            assertThrows<IllegalAccessException> {
                peerGradingService.markAsShow(grader, peerGrading)
            }
            val anotherStudent = integrationTestingService.getNLearners(1).first()
            anotherStudent.addRole(roleService.roleStudent())
            assertNotEquals(grader, anotherStudent)
            assertNotEquals(teacher, anotherStudent)
            assertThrows<IllegalAccessException> {
                peerGradingService.markAsShow(anotherStudent, peerGrading)
            }
        }.tWhen("a teacher that doesn't own the sequence try unhiding a peerGrading") {}
            .tThen("an exception is thrown") {
                val anotherTeacher = integrationTestingService.getAnyUser()
                anotherTeacher.addRole(roleService.roleTeacher())
                assertNotEquals(teacher, anotherTeacher)
                assertThrows<IllegalAccessException> {
                    peerGradingService.markAsShow(anotherTeacher, peerGrading)
                }
            }
    }

    @Test
    fun `test of getDraxoPeerGrading`() {
        val response = integrationTestingService.getAnyResponse()
        val grader = integrationTestingService.getAnyUser()
        val teacher = integrationTestingService.getTestTeacher()
        response.interaction.sequence.assignment!!.owner = teacher
        lateinit var peerGrading: DraxoPeerGrading
        tGiven("A draxo peer grading") {
            peerGrading = DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation()
                    .addEvaluation(Criteria.D, OptionId.YES)
                    .addEvaluation(Criteria.R, OptionId.YES)
                    .addEvaluation(Criteria.A, OptionId.YES)
                    .addEvaluation(Criteria.X, OptionId.YES)
                    .addEvaluation(Criteria.O, OptionId.NO),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.saveAndFlush(it)
                    entityManager.clear()
                    it
                }
        }.tWhen("We get the draxo peer grading") {
            val draxoPeerGrading = peerGrading.id?.let { it1 -> peerGradingService.getDraxoPeerGrading(it1) }
            draxoPeerGrading
        }.tThen("the draxo peer grading is returned") { draxoPeerGrading ->
            assertNotNull(draxoPeerGrading)
            assertEquals(peerGrading.id, draxoPeerGrading!!.id)
        }.tWhen("we get with an inexisting id") {}.tThen("we get an exception") {
            assertThrows<Exception> {
                peerGradingService.getDraxoPeerGrading(-1)
            }
        }
    }

    @Test
    fun `test of countEvaluationsMadeByUsers`() {
        // Given
        val learners = integrationTestingService.getNLearners(2)
        val grader = learners[0]
        val learner = learners[1]
        val subject = functionalTestingService.createSubject(integrationTestingService.getTestTeacher())
        functionalTestingService.addQuestion(subject, QuestionType.OpenEnded)
        val assignement = functionalTestingService.createAssignment(subject)
        val sequence = assignement.sequences.first()

        val learnersAssignementList: List<LearnerAssignment> = listOf(
            LearnerAssignment(grader, assignement),
            LearnerAssignment(learner, assignement),
        )
        val mapUserToEvaluatioCount: MutableMap<LearnerAssignment, Long> =
            learnersAssignementList.associateWith { 0.toLong() }.toMutableMap()
        assertEquals(
            mapUserToEvaluatioCount,
            peerGradingService.countEvaluationsMadeByUsers(learnersAssignementList, sequence),
            ""
        ) //No evaluations made by the users, because the submission interaction isn't initialized

        functionalTestingService.startSequence(sequence, ExecutionContext.FaceToFace) // Phase 1 (Start)

        val response = functionalTestingService.submitResponse(
            Phase.PHASE_1,
            learner,
            sequence,
            true,
            ConfidenceDegree.CONFIDENT,
            "response"
        )
        functionalTestingService.nextPhase(sequence) // Phase 2 (Evaluation)

        assertEquals(
            mapUserToEvaluatioCount,
            peerGradingService.countEvaluationsMadeByUsers(learnersAssignementList, sequence),
            "No evaluations made by the users"
        )
        assertEquals(
            emptyMap<LearnerAssignment, Long>(),
            peerGradingService.countEvaluationsMadeByUsers(emptyList<LearnerAssignment>(), sequence),
            "No user given"
        )

        tGiven("A peerGrading given by the grader") {
            DraxoPeerGrading(
                grader = grader,
                response = response,
                draxoEvaluation = DraxoEvaluation().addEvaluation(Criteria.D, OptionId.NO, "explanation"),
                lastSequencePeerGrading = false
            )
                .tWhen {
                    peerGradingRepository.save(it)
                    it
                }
        }.tWhen("We get all the evaluations for the response") {
            val evaluations = peerGradingService.findAllEvaluation(grader, response.interaction.sequence)
            evaluations
        }.tThen("the peerGrading is returned") { evaluations ->
            mapUserToEvaluatioCount[learnersAssignementList[0]] = 1
            assertEquals(
                mapUserToEvaluatioCount,
                peerGradingService.countEvaluationsMadeByUsers(learnersAssignementList, sequence)
            )
            assertEquals(1, evaluations.count())
            assertEquals(grader, evaluations.first().grader)
            assertEquals(response, evaluations.first().response)
        }
    }

}