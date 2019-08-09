package org.elaastic.questions.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class MailCheckingJob(
        @Autowired val mailCheckingService: MailCheckingService
) {

    val logger = Logger.getLogger(MailCheckingJob::class.java.name)

    @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes
    fun execute() {
        logger.info("Start email checking job...")
        mailCheckingService.sendEmailsToAccountActivation()
        logger.info("End email checking  job.")
    }

}
