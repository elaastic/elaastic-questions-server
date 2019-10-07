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
import org.elaastic.questions.directory.PasswordResetKey
import org.elaastic.questions.directory.PasswordResetKeyRepository
import org.elaastic.questions.directory.User
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
import javax.transaction.Transactional

@Service
class PasswordResetMailService(
        @Autowired val messageSource: MessageSource,
        @Autowired val mailSender: JavaMailSender,
        @Autowired val templateEngine: TemplateEngine,
        @Autowired val passwordResetKeyRepository: PasswordResetKeyRepository,

        @Value("\${elaastic.questions.url}")
        val elaasticQuestionUrl:String
) {

    val logger = Logger.getLogger(PasswordResetMailService::class.java.name)
    val resetPasswordRelativeUrl: String = "userAccount/resetPassword"

    /**
     * Send email with password reset key
     * @param lifetime the lifetime of a reset password key in hours
     */
    @Transactional
    fun sendPasswordResetKeyEmails(lifetime: Int = 1) {
        val expirationDate = DateUtils.addHours(Date(), -lifetime)
        var processedKeys = mutableListOf<PasswordResetKey>()
        var nbSentEmail = 0
        val keysByUserEmail = mutableMapOf<String, MutableList<PasswordResetKey>>()
        passwordResetKeyRepository.findAllPasswordResetKeys(expirationDate).forEach {
            val email = it.user.email!!
            var listForEmail = keysByUserEmail[email]
            if (listForEmail == null) {
                listForEmail = mutableListOf<PasswordResetKey>()
                keysByUserEmail[email] = listForEmail
            }
            listForEmail.add(it)
        }
        keysByUserEmail.keys.forEach {email ->
            try {
                val keys = keysByUserEmail[email]!!
                buidMessage(email, keys).let { mimeMessage ->
                    mailSender.send(mimeMessage)
                    nbSentEmail++
                }
                keys.forEach { key ->
                    key.passwordResetEmailSent = true
                    passwordResetKeyRepository.saveAndFlush(key)
                    processedKeys.add(key)
                }

            } catch (e: Exception) {
                logger.severe("Error with ${email}: ${e.message}")
            }
        }

        logger.info("Nb processed keys: ${processedKeys.size}")
        logger.info("Nb processed sent emails: $nbSentEmail")
    }

    /**
     * Build the mime message
     * @param userInfo user informations
     * @return the built mime message
     */
    private fun buidMessage(email: String, keys: List<PasswordResetKey>): MimeMessage {
        val firstUser = keys[0].user
        val locale = Locale(firstUser.settings?.language)
        val subject = messageSource.getMessage(
                "email.passwordReset.notification.title",
                null,
                locale)
        val templateContext:Context = Context(locale)
        templateContext.setVariable("firstName", firstUser.firstName)
        val mapOfResetPasswordUrl = mutableMapOf<String, String>()
        keys.forEach {
            mapOfResetPasswordUrl[it.user.username] = "$elaasticQuestionUrl$resetPasswordRelativeUrl?passwordResetKey=${it.passwordResetKey}"
        }
        templateContext.setVariable("resetPasswordUrls", mapOfResetPasswordUrl)
        val htmlText = templateEngine.process("email/passwordResetMail", templateContext)
        logger.fine("""Content of email sent: 
            |$htmlText
        """.trimMargin())
        val mailMessage = mailSender.createMimeMessage()
        MimeMessageHelper(mailMessage, true).let {
            it.setTo(email)
            it.setSubject(subject)
            it.setText(htmlText, true)
        }
        return mailMessage
    }

}

