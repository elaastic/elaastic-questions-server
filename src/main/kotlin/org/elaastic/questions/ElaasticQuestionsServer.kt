package org.elaastic.questions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 * @author John Tranier
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
class ElaasticQuestionsServer

fun main(args: Array<String>) {
	runApplication<ElaasticQuestionsServer>(*args)
}
