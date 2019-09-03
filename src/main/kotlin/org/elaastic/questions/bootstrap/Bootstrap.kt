package org.elaastic.questions.bootstrap

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


@Component
class Bootstrap(
        @Autowired val bootstrapService: BootstrapService
) {

    val LOG : Logger = Logger.getLogger(Bootstrap::class.toString())

    @PostConstruct
    fun init() {
        LOG.info("Bootstrapping elaastic-questions for any profile...")
        bootstrapService.addTermsIfNotActiveOneAvailable()
        LOG.info("End of the bootstrap for any profile")
    }

}
