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

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService

@Service
class TeacherFeedbackMailService(
        @Autowired val entityManager: EntityManager,
        @Autowired val messageSource: MessageSource,
        @Autowired val mailSender: JavaMailSender,
        @Autowired val templateEngine: TemplateEngine,
        @Autowired val sequenceService: SequenceService,

        @Value("\${elaastic.questions.url}")
        val elaasticQuestionUrl: String,

        @Value("\${elaastic.questions.mail.teacherFeedbackReminder.from}")
        val elaasticFeedbackReminderMailFrom: String

) {

    val logger: Logger = Logger.getLogger(TeacherFeedbackMailService::class.java.name)
    val relativeFeedbackURLFormat = "player/sequence/%d/play"

    @Transactional
    fun sendFeedbackReminderEmails() {
        findAllNotificationRecipients().let { list ->
            val done: MutableList<Sequence> = mutableListOf()

            list.forEach { sequence ->
                try {
                    buildMessage(sequence).let {
                        mailSender.send(it)
                    }
                    done.add(sequence)
                } catch (e: Exception) {
                    logger.severe("Error with ${sequence.owner.email} : ${e.message}")
                }
            }
            logger.info("Nb emails sent: ${done.size}")
            if (done.isNotEmpty()) {
                updateEmailSentStatusForAllNotifications(done)
            }
        }
    }

    private fun buildMessage(sequence: Sequence): MimeMessage {
        val locale = Locale(sequence.owner.settings!!.language)
        val relativeFeedbackURL = relativeFeedbackURLFormat.format(sequence.id)
        val subject = messageSource.getMessage(
                "email.feedbackReminderMail.title",
                null,
                locale)
        val templateContext:Context = Context(locale)
        templateContext.setVariable("firstName", sequence.owner.firstName)
        templateContext.setVariable("feedbackUrl", "$elaasticQuestionUrl$relativeFeedbackURL")
        val htmlText = templateEngine.process("email/feedbackReminderMail", templateContext)
        logger.fine("""Content of email sent: 
            |$htmlText
        """.trimMargin())
        val mailMessage = mailSender.createMimeMessage()
        MimeMessageHelper(mailMessage, true).let {
            it.setTo(sequence.owner.email!!)
            it.setFrom(elaasticFeedbackReminderMailFrom)
            it.setSubject(subject)
            it.setText(htmlText, true)
        }
        return mailMessage
    }

    fun findAllNotificationRecipients(): List<Sequence> =
            sequenceService.findAll().filter {
                it.owner.hasEmail()
                        && it.isStopped()
                        && !it.feedbackReminderMailSent
                        // TODO only take the ones with no feedback
                        && it.dateStopped?.before(Date(Date().time - 5 * 60 * 1000)) ?: false
            }

    fun updateEmailSentStatusForAllNotifications(sequences: List<Sequence>) =
            sequences.forEach {
                it.feedbackReminderMailSent = true
                sequenceService.save(it)
            }

}

