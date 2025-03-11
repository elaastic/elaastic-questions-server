package org.elaastic.player.sequence

import org.elaastic.player.command.CommandModelFactory
import org.elaastic.sequence.SequenceService
import org.elaastic.user.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class SequenceController(
    private val sequenceService: SequenceService
) {

    @GetMapping("/config-sequence/{sequenceId}/modal")
    fun configSequence(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
    ): String {
        val user = authentication.principal as User
        val sequence = sequenceService.get(sequenceId)

        val commandModel = CommandModelFactory.build(user, sequence)

        model["sequenceId"] = commandModel.sequenceId
        model["statementId"] = commandModel.statementId
        model["questionType"] = commandModel.questionType
        model["hasExpectedExplanation"] = commandModel.hasExpectedExplanation
        return "player/assignment/sequence/components/command/_config-sequence.html :: configSequence"
    }
}