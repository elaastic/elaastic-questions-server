package org.elaastic.questions.bootstrap

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

/**
 * @author John Tranier
 */
@Component
@Profile("!prod")
class DevBootstrap(
        @Autowired val bootstrapService: BootstrapService
) {

    val LOG : Logger = Logger.getLogger(DevBootstrap::class.toString())

    @PostConstruct
    fun init() {
        LOG.info("Bootstrapping elaastic-questions in development and test modes...")
        bootstrapService.initializeDevUsers()
        bootstrapService.startDevLocalSmtpServer()
        LOG.info("End of the bootstrap")
    }

}
