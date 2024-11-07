package org.elaastic.questions.assignment.ia

import org.elaastic.sequence.interaction.ResponseId
import java.util.*

class RecommendationResponsePool(responseList: List<ResponseInfo>,
                                 comparator: Comparator<ResponseInfo>) {

    var comparator: Comparator<ResponseInfo> = comparator
        set(value) {
            responseSet.let {
                responseSet = TreeSet(value)
                responseSet.addAll(it)
            }

            field = value
        }

    fun comparator(value: Comparator<ResponseInfo>): RecommendationResponsePool {
        comparator = value
        return this
    }

    private var responseSet: TreeSet<ResponseInfo> = TreeSet(comparator)

    init {
        responseSet.addAll(responseList)
    }


    fun next(except: Collection<ResponseId> = listOf()): ResponseInfo? {
        val refusedCandidates = mutableListOf<ResponseInfo>()

        var candidate: ResponseInfo? = null
        while (candidate == null && !responseSet.isEmpty()) {
            candidate = responseSet.pollLast()
            if (candidate.id in except) {
                refusedCandidates.add(candidate)
                candidate = null
            }
        }

        responseSet.addAll(refusedCandidates) //re-queue all the refused candidates
        if (candidate != null) {
            candidate.nbSelection++
            responseSet.add(candidate) // re-queue the selected candidate
        }

        return candidate
    }
}