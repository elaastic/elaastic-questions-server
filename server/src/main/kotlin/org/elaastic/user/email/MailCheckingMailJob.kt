package org.elaastic.user.email

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class MailCheckingMailJob(
        @Autowired val mailCheckingMailService: MailCheckingMailService
) {

    val logger = Logger.getLogger(MailCheckingMailJob::class.java.name)

    @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes
    fun execute() {
        logger.info("Start email checking job...")
        mailCheckingMailService.sendEmailsToAccountActivation()
        logger.info("End email checking  job.")
    }

}