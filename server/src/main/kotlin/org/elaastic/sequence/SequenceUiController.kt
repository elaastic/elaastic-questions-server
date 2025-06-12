package org.elaastic.sequence

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("ui/sequence")
class SequenceUiController {

    @GetMapping("/sequence-configuration")
    fun sequenceConfiguration() = "player/assignment/sequence/components/command/ui-sequence-config"
}