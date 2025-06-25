package org.elaastic.sequence

import org.elaastic.common.abtesting.ElaasticFeatures
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("ui/sequence")
class SequenceUiController {

    @GetMapping("/configuration")
    fun sequenceConfiguration(
        model: Model
    ): String {
        model["IAExplanationIsEnabled"] = ElaasticFeatures.CHATGPT_EVALUATION.isActive()
        return "player/assignment/sequence/components/command/ui-sequence-config"
    }
}