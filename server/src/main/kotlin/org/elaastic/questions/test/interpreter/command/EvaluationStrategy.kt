package org.elaastic.questions.test.interpreter.command

import org.elaastic.sequence.interaction.response.Response
import java.math.BigDecimal
import kotlin.random.Random

enum class EvaluationStrategy(val evaluate: (Response) -> BigDecimal) {
    RANDOM(
        fun(_: Response): BigDecimal = (1 + Random.nextInt(4)).toBigDecimal()
    ),
    ALWAYS_MAX(
        fun(_: Response): BigDecimal = 5.toBigDecimal()
    ),
    ALWAYS_MIN(
        fun(_: Response): BigDecimal = 1.toBigDecimal()
    ),
    RELEVANT(
        fun(response: Response): BigDecimal {
            val score = response.score?.toInt() ?: 0
            return (1 + score * 4 / 100).toBigDecimal()
        }
    ),
    IRRELEVANT(
        fun(response: Response): BigDecimal {
            val score = response.score?.toInt() ?: 0
            return (5 - score * 4 / 100).toBigDecimal()
        }
    ),
}