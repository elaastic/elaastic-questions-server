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

import org.apache.commons.lang3.time.DateUtils
import org.elaastic.bootstrap.BootstrapService
import org.elaastic.questions.directory.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
internal class PasswordResetMailServiceIntegrationTest(
        @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository,
        @Autowired val userService: UserService,
        @Autowired val passwordResetMailService: PasswordResetMailService,
        @Autowired val roleService: RoleService,
        @Autowired val entityManager: EntityManager,
        @Autowired val bootstrapService: BootstrapService
) {

    lateinit var alPacino: User
    lateinit var claraLuciani: User
    lateinit var bobDeniro: User

    val logger: Logger = Logger.getLogger(PasswordResetMailServiceIntegrationTest::class.java.name)
    val smtpServer = bootstrapService.mailServer!!

    @BeforeEach
    fun initUsersWithEmailCheckingNeeded() {
        // given exactly 3 users with generated password reset key
        //
        alPacino = User(
                firstName = "Al",
                lastName = "Pacino",
                username = "alpacino",
                plainTextPassword = "1234",
                email = "alpacino@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "fr", true)
            userService.generatePasswordResetKeyForUser(it)
            it
        }
        claraLuciani = User(
                firstName = "Clara",
                lastName = "Luciani",
                username = "claraluciani",
                plainTextPassword = "1234",
                email = "claraluciani@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "en", true)
            userService.generatePasswordResetKeyForUser(it)
            it
        }
        bobDeniro = User(
                firstName = "Bob",
                lastName = "Deniro",
                username = "bobdeniro",
                plainTextPassword = "1234",
                email = "bobdeniro@elaastic.org"
        ).addRole(roleService.roleStudent()).let {
            userService.addUser(it, "fr", true)
            userService.generatePasswordResetKeyForUser(it)
            it
        }

    }

    @Test
    fun `test email sending`() {
        // given: the list of password reset keys to update
        val expirationDate = DateUtils.addHours(Date(), -1)
        val keys = passwordResetKeyRepository.findAllPasswordResetKeys(expirationDate)
        smtpServer.purgeEmailFromAllMailboxes()
        // when:  triggering emails sending to send password keys
        passwordResetMailService.sendPasswordResetKeyEmails()
        // then: 3 messages have been received
        if(smtpServer.waitForIncomingEmail(2000, 3)) {
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

        // and: password reset keys are updated
        keys.forEach {
            entityManager.refresh(it)
            assertTrue(it.passwordResetEmailSent)
        }
    }

}
