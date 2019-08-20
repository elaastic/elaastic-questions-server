package org.elaastic.questions.directory.jobs

import org.elaastic.questions.directory.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class ActivationKeyGarbageCollectorJob(
        @Autowired val userService: UserService
) {

    val logger = Logger.getLogger(ActivationKeyGarbageCollectorJob::class.java.name)

    @Scheduled(cron = "0 0 0/1 * * ?") // every hours
    fun execute() {
        logger.info("Start activation key garbage collector job...")
        userService.removeOldActivationKeys()
        logger.info("End activation key garbage collector  job.")
    }

}
