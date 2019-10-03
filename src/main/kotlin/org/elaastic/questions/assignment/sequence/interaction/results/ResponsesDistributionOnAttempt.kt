package org.elaastic.questions.assignment.sequence.interaction.results

import com.fasterxml.jackson.annotation.JsonIgnore
import org.elaastic.questions.assignment.sequence.interaction.response.Response


typealias ItemIndex = Int
typealias NbVote = Int

class ResponsesDistributionOnAttempt(
        var nbResponse: Int,
        var nbVotesByItem: Array<NbVote>,
        var nbNoItem: Int = 0
) {
    // Construct empty result for a number of items
    constructor(nbItem: Int) : this(
            0,
            Array(nbItem) { 0 },
            0
    )

    constructor(nbItem: Int, responses: List<Response>) : this(nbItem) {
        responses.forEach { add(it) }
    }

    @JsonIgnore
    fun getNbItem(): Int = nbVotesByItem.size

    fun size() = getNbItem()

    fun getNbVotes(i: ItemIndex): Int {
        return nbVotesByItem[i - 1]
    }

    fun incNbVotes(i: ItemIndex) {
        nbVotesByItem[i - 1]++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResponsesDistributionOnAttempt

        if (nbNoItem != other.nbNoItem) return false
        if (!nbVotesByItem.contentEquals(other.nbVotesByItem)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbNoItem
        result = 31 * result + nbVotesByItem.hashCode()
        return result
    }

    fun add(response: Response) {
        nbResponse++
        response.choiceListSpecification.let { choices ->
            if (choices?.isEmpty() != false)
                nbNoItem++
            else choices.forEach { incNbVotes(it) }
        }

    }
}
