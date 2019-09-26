package org.elaastic.questions.attachment.jobs

import org.elaastic.questions.attachment.AttachmentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class AttachmentGarbageCollectorJob(
        @Autowired val attachmentService: AttachmentService
) {

    val logger: Logger = Logger.getLogger(AttachmentGarbageCollectorJob::class.java.name)

    //@Scheduled(cron = "0 0 0/1 * * ?") // every hours
    @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes
    fun execute() {
        logger.info("Start attachment garbage collector job...")
        attachmentService.deleteAttachmentAndFileInSystem()
        logger.info("End attachment garbage collector  job.")
    }

}
