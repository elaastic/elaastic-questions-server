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

package org.elaastic.questions.email

import org.elaastic.questions.assignment.*
import org.elaastic.questions.assignment.choice.ChoiceItem
import org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification
import org.elaastic.questions.assignment.sequence.Sequence as ElaasticSequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.StatementService
import org.elaastic.questions.assignment.sequence.interaction.feedback.FeedbackService
import org.elaastic.questions.assignment.sequence.interaction.feedback.TeacherFeedback
import org.elaastic.questions.bootstrap.BootstrapService
import org.elaastic.questions.directory.*
import org.elaastic.questions.test.directive.tWhen
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

import org.elaastic.questions.test.directive.*
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/// TODO webEnvironment must be added to all integration tests
@Transactional
internal class TeacherFeedbackMailServiceIntegrationTest(
        @Autowired val sequenceService: SequenceService,
        @Autowired val assignmentService: AssignmentService,
        @Autowired val statementService: StatementService,
        @Autowired val userService: UserService,
        @Autowired val teacherFeedbackMailService: TeacherFeedbackMailService,
        @Autowired val feedbackService: FeedbackService,
        @Autowired val roleService: RoleService,
        @Autowired val entityManager: EntityManager,
        @Autowired val bootstrapService: BootstrapService
) {

    lateinit var alPacino: User
    lateinit var claraLuciani: User
    lateinit var sequence1: ElaasticSequence
    lateinit var sequence2: ElaasticSequence

    val logger = Logger.getLogger(TeacherFeedbackMailServiceIntegrationTest::class.java.name)
    val smtpServer = bootstrapService.mailServer!!

    @BeforeEach
    fun `init users and sequences`() {
        // given exactly 2 users and 2 sequences
        //
        alPacino = User(
                firstName = "Al",
                lastName = "Pacino",
                username = "alpacino",
                plainTextPassword = "1234",
                email = "alpacino@elaastic.org"
        ).addRole(roleService.roleTeacher()).let {
            userService.addUser(it, "fr", true)
        }
        claraLuciani = User(
                firstName = "Clara",
                lastName = "Luciani",
                username = "claraluciani",
                plainTextPassword = "1234",
                email = "claraluciani@elaastic.org"
        ).addRole(roleService.roleTeacher()).let {
            userService.addUser(it, "en", true)
        }

        sequence1 = ElaasticSequence(
                alPacino,
                statementService.save(Statement(alPacino, "A",
                        "A?", QuestionType.ExclusiveChoice,
                        ExclusiveChoiceSpecification(2, ChoiceItem(1, 100.0f)))),
                assignmentService.save(Assignment("A", alPacino))
        ).let(sequenceService::save)

        sequence2 = ElaasticSequence(
                claraLuciani,
                statementService.save(Statement(claraLuciani, "B",
                        "B?", QuestionType.ExclusiveChoice,
                        ExclusiveChoiceSpecification(2, ChoiceItem(1, 100.0f)))),
                assignmentService.save(Assignment("B", claraLuciani))
        ).let(sequenceService::save)
    }

    @Test
    fun `test email sending 1`() {
        smtpServer.purgeEmailFromAllMailboxes()

        tGiven("""
                2 teachers and 2 sequences with one stopped 6 minutes ago and
                the other still playing. Both of the teachers have not submitted
                feedback.
            """.trimIndent()
        ) {
            sequenceService.start(alPacino, sequence1, ExecutionContext.FaceToFace, false, 2)
            sequenceService.start(claraLuciani, sequence2, ExecutionContext.FaceToFace, false, 2)
            sequenceService.stop(alPacino, sequence1)
            sequenceService.publishResults(alPacino, sequence1)
            sequence1.dateResultsPublished = Date(Date().time - 6 * 60 * 1000)
            sequenceService.save(sequence1)
        }
        tWhen("triggering email sending to give a feedback on a sequence") {
            teacherFeedbackMailService.sendFeedbackReminderEmails()
        }
        tThen("1 email has been sent") {
            assertThat(smtpServer.receivedMessages.size, equalTo(1))
            smtpServer.receivedMessages.forEachIndexed { index, message ->
                logger.info("""
                    Content of  message $index:
                    ${message.inputStream.bufferedReader().use(BufferedReader::readText)}
                """.trimIndent())
            }
        }
    }

    @Test
    fun `test email sending 2`() {
        smtpServer.purgeEmailFromAllMailboxes()

        tGiven("""
                2 teachers and 2 sequences with one stopped 6 minutes ago and 
                the other stopped just now. Both of the teachers have not submitted
                feedback.
            """.trimIndent()
        ) {
            sequenceService.start(alPacino, sequence1, ExecutionContext.FaceToFace, false, 2)
            sequenceService.start(claraLuciani, sequence2, ExecutionContext.FaceToFace, false, 2)
            sequenceService.stop(alPacino, sequence1)
            sequenceService.stop(claraLuciani, sequence2)
            sequenceService.publishResults(alPacino, sequence1)
            sequenceService.publishResults(claraLuciani, sequence2)
            sequence1.dateResultsPublished = Date(Date().time - 6 * 60 * 1000)
            sequenceService.save(sequence1)
        }
        tWhen("triggering email sending to give a feedback on a sequence") {
            teacherFeedbackMailService.sendFeedbackReminderEmails()
        }
        tThen("1 email has been sent") {
            assertThat(smtpServer.receivedMessages.size, equalTo(1))
            smtpServer.receivedMessages.forEachIndexed { index, message ->
                logger.info("""
                    Content of  message $index:
                    ${message.inputStream.bufferedReader().use(BufferedReader::readText)}
                """.trimIndent())
            }
        }
    }

    @Test
    fun `test email sending 3`() {
        smtpServer.purgeEmailFromAllMailboxes()

        tGiven("""
                2 teachers and 2 sequences both stopped 6 minutes ago.
                One of the two teachers have not submitted feedback.
            """.trimIndent()
        ) {
            sequenceService.start(alPacino, sequence1, ExecutionContext.FaceToFace, false, 2)
            sequenceService.start(claraLuciani, sequence2, ExecutionContext.FaceToFace, false, 2)
            sequenceService.stop(alPacino, sequence1)
            sequenceService.stop(claraLuciani, sequence2)
            sequenceService.publishResults(alPacino, sequence1)
            sequenceService.publishResults(claraLuciani, sequence2)
            sequence1.dateResultsPublished = Date(Date().time - 6 * 60 * 1000)
            sequence2.dateResultsPublished = Date(Date().time - 6 * 60 * 1000)
            sequenceService.save(sequence1)
            sequence2 = sequenceService.save(sequence2)
            TeacherFeedback(
                    claraLuciani,
                    sequence2,
                    3,
                    3,
                    "Test"
            ).let(feedbackService::saveTeacherFeedback)
        }
        tWhen("triggering email sending to give a feedback on a sequence") {
            teacherFeedbackMailService.sendFeedbackReminderEmails()
        }
        tThen("1 email has been sent") {
            assertThat(smtpServer.receivedMessages.size, equalTo(1))
            smtpServer.receivedMessages.forEachIndexed { index, message ->
                logger.info("""
                    Content of  message $index:
                    ${message.inputStream.bufferedReader().use(BufferedReader::readText)}
                """.trimIndent())
            }
        }
    }
}
