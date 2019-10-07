package org.elaastic.questions.assignment.ia

import org.elaastic.questions.assignment.sequence.interaction.response.Response

class ResponseInfo(
        val id: Long,
        val correct: Boolean,
        val evaluable: Boolean = true,
        var nbSelection: Int = 0
) : Comparable<ResponseInfo> {

    constructor(response: Response) : this(
            response.id!!,
            response.score == 100f,
            response.explanation?.length ?: 0 > 10 // TODO Constant
    )

    override fun compareTo(other: ResponseInfo): Int =
            this.nbSelection.compareTo(other.nbSelection)

    override fun toString(): String =
            "<$id, $correct, $nbSelection>"
}