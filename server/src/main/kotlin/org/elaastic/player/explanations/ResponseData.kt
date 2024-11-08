package org.elaastic.player.explanations

import org.elaastic.activity.response.Response
import java.math.BigDecimal

class ResponseData(
        val choices: List<Int> = listOf(),
        val score: Int, // percents
        val correct: Boolean
) {
    constructor(response: Response) : this(
            choices = response.learnerChoice ?: error("The learner choice is undefined"),
            score = (response.score ?: error("The score is undefined")).toInt(),
            correct = response.score?.compareTo(BigDecimal(100)) == 0
    )

    override fun equals(other: Any?): Boolean {
        return (other is ResponseData) && choices == other.choices
    }

    override fun hashCode(): Int {
        return choices.hashCode()
    }
}