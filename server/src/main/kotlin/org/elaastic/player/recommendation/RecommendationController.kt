package org.elaastic.player.recommendation

import org.elaastic.common.util.requireAccessThrowDenied
import org.elaastic.player.sequence.SequenceModelFactory
import org.elaastic.sequence.SequenceService
import org.elaastic.user.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class RecommendationController(
    private val sequenceService: SequenceService,
    private val sequenceModelFactory: SequenceModelFactory
) {

    @GetMapping("/recommendation/{sequenceId}/modal")
    fun recommendation(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long
    ): String {
        val user = authentication.principal as User
        val sequence = sequenceService.get(sequenceId)

        requireAccessThrowDenied(sequence.owner == user) {
            "You are not allowed to access this modal"
        }

        val resultModel = sequenceModelFactory.buildForTeacher(user, sequence).resultsModel

        model["sequenceId"] = sequenceId
        model["resultsModel"] = resultModel!!
        return "/player/assignment/sequence/components/_recommendationExplanationPopup.html :: recommendationExplanationPopup"
    }
}