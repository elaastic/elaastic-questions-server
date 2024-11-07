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

import org.elaastic.bootstrap.BootstrapService
import org.elaastic.user.ActivationKeyRepository
import org.elaastic.user.RoleService
import org.elaastic.user.User
import org.elaastic.user.UserService
import org.elaastic.user.email.MailCheckingMailService
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class MailCheckingMailServiceIntegrationTest(
    @Autowired val activationKeyRepository: ActivationKeyRepository,
    @Autowired val userService: UserService,
    @Autowired val mailCheckingMailService: MailCheckingMailService,
    @Autowired val roleService: RoleService,
    @Autowired val entityManager: EntityManager,
    @Autowired val bootstrapService: BootstrapService
) {

    lateinit var alPacino: User
    lateinit var claraLuciani: User
    lateinit var bobDeniro: User

    val logger = Logger.getLogger(MailCheckingMailServiceIntegrationTest::class.java.name)
    val smtpServer = bootstrapService.mailServer!!

    @BeforeEach
    fun initUsersWithEmailCheckingNeeded() {
        // given exactly 3 users needed email checking
        //
        alPacino = User(
                firstName = "Al",
                lastName = "Pacino",
                username = "alpacino",
                plainTextPassword = "1234",
                email = "alpacino@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "fr", true)
        }
        claraLuciani = User(
                firstName = "Clara",
                lastName = "Luciani",
                username = "claraluciani",
                plainTextPassword = "1234",
                email = "claraluciani@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "en", true)
        }
        bobDeniro = User(
                firstName = "Bob",
                lastName = "Deniro",
                username = "bobdeniro",
                plainTextPassword = "1234",
                email = "bobdeniro@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "fr", true)
        }

    }

    @Test
    fun `test email sending`() {
        // given: the list of activation keys to update
        val actKeys = listOf(alPacino, claraLuciani, bobDeniro).map {
            activationKeyRepository.findByUser(it)!!
        }
        smtpServer.purgeEmailFromAllMailboxes()
        // when:  triggering emails sending to check validity
        mailCheckingMailService.sendEmailsToAccountActivation()
        // then: 3 messages have been received
        if(smtpServer.waitForIncomingEmail(1000, 3)) {
            assertThat(smtpServer.receivedMessages.size, equalTo(3))
        }
        else {
            fail("The 3 messages have not been received")
        }

        smtpServer.receivedMessages.forEachIndexed { index, message ->
            logger.info("""
                Content of  message $index:
                ${message.inputStream.bufferedReader().use(BufferedReader::readText)}
            """.trimIndent())
        }

        // and: activation key are updated
        actKeys.forEach {
            entityManager.refresh(it)
            assertTrue(it.activationEmailSent)
        }
    }


    @Test
    fun `test find All Notification Recipients`() {
        // when: asking the mail checking service to find mail notification recipients
        //
        val notificationRecipients = mailCheckingMailService.findAllNotificationRecipients()
        // then: 3 notification recipients are found by the mail checking service
        //
        assertThat(
                notificationRecipients.size,
                equalTo(3)
        )
        // and: the map contains the 3 expected recipients
        //
        assertThat(
                notificationRecipients[activationKeyRepository.findByUser(alPacino)?.activationKey]?.hashCode(),
                equalTo(mapOf("user_id" to alPacino.id, "first_name" to alPacino.firstName, "email" to alPacino.email, "language" to "fr").hashCode())
        )
        assertThat(
                notificationRecipients[activationKeyRepository.findByUser(claraLuciani)?.activationKey],
                notNullValue()
        )
        assertThat(
                notificationRecipients[activationKeyRepository.findByUser(bobDeniro)?.activationKey],
                notNullValue()
        )
    }

    @Test
    fun `test update activation status`() {
        // given: the list of activation keys to update
        val actKeys = listOf(alPacino, claraLuciani, bobDeniro).map {
            activationKeyRepository.findByUser(it)!!
        }
        actKeys.forEach {
            assertFalse(it.activationEmailSent)
        }
        // when: asking mail checking service to update status
        mailCheckingMailService.updateEmailSentStatusForAllNotifications(actKeys.map { it.activationKey })
        // then: activation key are updated
        actKeys.forEach {
            entityManager.refresh(it)
            assertTrue(it.activationEmailSent)
        }
    }
}
