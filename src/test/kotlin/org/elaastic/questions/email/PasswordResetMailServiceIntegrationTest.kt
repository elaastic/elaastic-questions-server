package org.elaastic.questions.email

import org.apache.commons.lang3.time.DateUtils
import org.elaastic.questions.bootstrap.BootstrapService
import org.elaastic.questions.directory.*
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.BufferedReader
import java.util.*
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
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

    val logger = Logger.getLogger(PasswordResetMailServiceIntegrationTest::class.java.name)
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
        assertThat(smtpServer.receivedMessages.size, equalTo(3))
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
