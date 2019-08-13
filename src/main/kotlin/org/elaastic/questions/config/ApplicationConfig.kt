package org.elaastic.questions.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author John Tranier
 */
@Configuration
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
class ApplicationConfig {
}