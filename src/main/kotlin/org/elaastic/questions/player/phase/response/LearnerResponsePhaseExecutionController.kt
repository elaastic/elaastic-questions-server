package org.elaastic.questions.player.phase.response

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/learner/interaction/response")
class LearnerResponsePhaseExecutionController {

    @GetMapping(value = ["", "/", "/index"])
    fun index(authentication: Authentication,
              model: Model
    ): String {
        val user: User = authentication.principal as User

        // TODO : get the state of the phase & chain with the proper action

        return "TODO"
    }

    fun showNotStated() {
        TODO()
    }

    fun showForm() {
        TODO()
    }

    fun showDone() {
        TODO()
    }

    fun submitResponse() {
        TODO()
    }

}