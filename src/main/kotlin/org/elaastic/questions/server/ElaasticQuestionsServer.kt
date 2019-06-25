package org.elaastic.questions.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author John Tranier
 */
@SpringBootApplication
class ElaasticQuestionsServer

fun main(args: Array<String>) {
	runApplication<ElaasticQuestionsServer>(*args)
}
