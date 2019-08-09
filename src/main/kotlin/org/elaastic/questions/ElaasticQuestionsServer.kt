package org.elaastic.questions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * @author John Tranier
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
class ElaasticQuestionsServer

fun main(args: Array<String>) {
	runApplication<ElaasticQuestionsServer>(*args)
}
