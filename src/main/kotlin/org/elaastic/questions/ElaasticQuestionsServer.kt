package org.elaastic.questions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ElaasticQuestionsServer

fun main(args: Array<String>) {
	runApplication<ElaasticQuestionsServer>(*args)
}
