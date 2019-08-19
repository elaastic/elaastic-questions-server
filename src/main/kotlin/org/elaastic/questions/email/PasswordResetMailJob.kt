package org.elaastic.questions.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class PasswordResetMailJob(
        @Autowired val passwordResetMailService: PasswordResetMailService
) {

    val logger = Logger.getLogger(PasswordResetMailJob::class.java.name)

    @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes
    fun execute() {
        logger.info("Start password reset key email sending job...")
        passwordResetMailService.sendPasswordResetKeyEmails()
        logger.info("End password reset key email sending.")
    }

}
