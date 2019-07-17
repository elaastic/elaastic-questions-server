package org.elaastic.questions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * @author John Tranier
 */
@SpringBootApplication
@EnableCaching
class ElaasticQuestionsServer

fun main(args: Array<String>) {
	runApplication<ElaasticQuestionsServer>(*args)
}
