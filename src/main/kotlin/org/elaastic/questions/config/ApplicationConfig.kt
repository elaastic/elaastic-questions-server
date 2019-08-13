package org.elaastic.questions.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling


@Configuration
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
class ApplicationConfig {
}