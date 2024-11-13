package org.elaastic.user.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.*
import java.util.logging.Logger
import javax.mail.internet.MimeMessage
import javax.persistence.EntityManager
import javax.transaction.Transactional

@Service
class MailCheckingMailService(
    @Autowired val entityManager: EntityManager,
    @Autowired val messageSource: MessageSource,
    @Autowired val mailSender: JavaMailSender,
    @Autowired val templateEngine: TemplateEngine,

    @Value("\${elaastic.questions.url}")
        val elaasticQuestionUrl:String,

    @Value("\${elaastic.questions.mail.activation.from}")
        val elaasticQuestionActivationFrom:String

) {

    val logger = Logger.getLogger(MailCheckingMailService::class.java.name)
    val relativeActivationURL = "userAccount/activate"

    /**
     * Send email to check user emails and then activate the corresponding user
     * accounts
     */
    @Transactional
    fun sendEmailsToAccountActivation() {
        var processedActivationKeys = mutableListOf<String>()
        findAllNotificationRecipients().forEach { (key, userInfo) ->
            try {
                buidMessage(key, userInfo).let {
                    mailSender.send(it)
                }
                processedActivationKeys.add(key)
            } catch (e: Exception) {
                logger.severe("Error with ${userInfo["email"]} : ${e.message}")
            }
        }
        logger.info("Nb email sending try : ${processedActivationKeys.size}")
        if (processedActivationKeys.isNotEmpty()) {
            updateEmailSentStatusForAllNotifications(processedActivationKeys)
        }
    }



    /**
     * Build the mime message
     * @param userInfo user informations
     * @return the built mime message
     */
    private fun buidMessage(key: String, userInfo: Map<String, Any>): MimeMessage {
        val locale = Locale(userInfo["language"] as String)
        val subject = messageSource.getMessage(
                "email.checking.title",
                null,
                locale)
        val templateContext: Context = Context(locale)
        templateContext.setVariable("firstName", userInfo["first_name"])
        templateContext.setVariable("activationUrl", "$elaasticQuestionUrl$relativeActivationURL?actKey=$key")
        val htmlText = templateEngine.process("email/emailCheckingMail", templateContext)
        logger.fine("""Content of email sent: 
            |$htmlText
        """.trimMargin())
        val mailMessage = mailSender.createMimeMessage()
        MimeMessageHelper(mailMessage, true).let {
            it.setTo(userInfo["email"] as String)
            it.setFrom(elaasticQuestionActivationFrom)
            it.setSubject(subject)
            it.setText(htmlText, true)
        }
        return mailMessage
    }

    /**
     * Notification recipients are stored in a map :<br>
     * <ul>
     *   <li> key : the activation key
     *   <li> value : a map representing the corresponding user [user_id:..., first_name:...,email:...]
     * </ul>
     * @return the notification recipients as a map
     */
    fun findAllNotificationRecipients(): Map<String, Map<String, Any>> {

        val queryString = """ 
            SELECT tuser.id as user_id, tuser.first_name, tuser.email, tsettings.language, tact_key.activation_key
              FROM user as tuser
              INNER JOIN  activation_key as tact_key ON tact_key.user_id = tuser.id
              INNER JOIN settings as tsettings ON tsettings.user_id = tuser.id
              where tact_key.activation_email_sent = false
        """.trimIndent()

        val query = entityManager.createNativeQuery(queryString)
        val results = query.resultList.toList()

        logger.info("Nb rows selected: ${results.size}")

        val notifications = results.associate {
            it as Array<out Any>
            it[4] as String to mapOf(
                    "user_id" to it[0],
                    "first_name" to it[1],
                    "email" to it[2],
                    "language" to it[3]
            )
        }

        logger.fine("Notifications to process: $notifications")

        return notifications
    }

    /**
     * Update status on activation key after emails were sent
     * @param actKeys list of activation keys which email was sent for
     */
    fun updateEmailSentStatusForAllNotifications(actKeys: List<String>) {
        val queryStr = actKeys.map { '?' }.joinToString(", ").let {
            """ 
            update activation_key as tact_key set tact_key.activation_email_sent = true 
            where tact_key.activation_key in ($it)
            """.trimIndent()
        }
        logger.fine("The update request: $queryStr")

        val query = entityManager.createNativeQuery(queryStr)
        actKeys.forEachIndexed { i, s ->
            query.setParameter(i + 1, s)
        }
        val nbOfUpdates = query.executeUpdate()

        logger.info("Nb rows updated: $nbOfUpdates")
    }

}