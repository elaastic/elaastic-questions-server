package org.elaastic.questions.assignment.sequence.interaction

import com.fasterxml.jackson.annotation.JsonIgnore


typealias ItemIndex = Int
typealias NbVote = Int
class ResultOfGroupOnAttempt(
        var nbResponse: Int,
        var nbVotesByItem: List<NbVote>,
        var nbNoItem: Int = 0
) {

    @JsonIgnore
    fun getNbItem(): Int = nbVotesByItem.size

    fun size() = getNbItem()

    fun getNbVotes(i: ItemIndex): Int {
        return nbVotesByItem[i-1]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ResultOfGroupOnAttempt

        if (nbNoItem != other.nbNoItem) return false
        if (nbVotesByItem != other.nbVotesByItem) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nbNoItem
        result = 31 * result + nbVotesByItem.hashCode()
        return result
    }

}
