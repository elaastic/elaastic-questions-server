package org.elaastic.test.sequence

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("ui/sequence")
class SequenceUiTestController {

    @GetMapping("/sequence-configuration/test")
    fun sequenceConfigurationTest() = "player/assignment/sequence/components/command/test/test-sequence-config"
}