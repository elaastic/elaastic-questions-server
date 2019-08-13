package org.elaastic.questions.email

import org.elaastic.questions.bootstrap.BootstrapService
import org.elaastic.questions.directory.ActivationKeyRepository
import org.elaastic.questions.directory.RoleService
import org.elaastic.questions.directory.User
import org.elaastic.questions.directory.UserService
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.logging.Logger
import javax.persistence.EntityManager
import javax.transaction.Transactional

@SpringBootTest
@Transactional
internal class MailCheckingServiceIntegrationTest(
        @Autowired val activationKeyRepository: ActivationKeyRepository,
        @Autowired val userService: UserService,
        @Autowired val mailCheckingService: MailCheckingService,
        @Autowired val roleService: RoleService,
        @Autowired val entityManager: EntityManager,
        @Autowired val bootstrapService: BootstrapService
) {

    lateinit var alPacino: User
    lateinit var claraLuciani: User
    lateinit var bobDeniro: User

    val logger = Logger.getLogger(MailCheckingServiceIntegrationTest::class.java.name)
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
        // when:  triggering emails sending to check validity
        mailCheckingService.sendEmailsToAccountActivation()
        // then: 3 messages have been received
        assertThat(smtpServer.receivedMessages.size, equalTo(3))
        smtpServer.receivedMessages.forEachIndexed { index, message ->
            logger.info("""
                Content of  message $index:
                ${message.content}
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
        val notificationRecipients = mailCheckingService.findAllNotificationRecipients()
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
        mailCheckingService.updateEmailSentStatusForAllNotifications(actKeys.map { it.activationKey })
        // then: activation key are updated
        actKeys.forEach {
            entityManager.refresh(it)
            assertTrue(it.activationEmailSent)
        }
    }
}
