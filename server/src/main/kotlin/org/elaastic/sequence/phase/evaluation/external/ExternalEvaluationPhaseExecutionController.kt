package org.elaastic.sequence.phase.evaluation.external

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.activity.results.ItemIndex
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.assignment.Assignment
import org.elaastic.sequence.SequenceService
import org.elaastic.sequence.phase.evaluation.AbstractEvaluationPhaseExecutionController
import org.elaastic.user.PrincipalUserResolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/external")
class ExternalEvaluationPhaseExecutionController(
    @Autowired override val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired override val responseService: ResponseService,
    @Autowired override val chatGptEvaluationService: ChatGptEvaluationService
) : AbstractEvaluationPhaseExecutionController(
    sequenceService,
    responseService,
    chatGptEvaluationService
) {
    @PostMapping("/finalize")
    fun finalizeEvaluationPhase(
        authentication: Authentication,
        @PathVariable sequenceId: Long,
        @ModelAttribute evaluationData: EvaluationData,
        locale: Locale
    ): String {
        val user = (authentication.principal as PrincipalUserResolver).elaasticUser

        sequenceService.get(sequenceId, true).let { sequence ->
            val lastResponse: Response? =
                if (
                    sequence.isSecondAttemptAllowed()
                    && !responseService.hasResponseForUser(user, sequence, 2)
                )
                    changeAnswer(
                        user,
                        sequence,
                        Answer(
                            evaluationData.choiceList,
                            evaluationData.confidenceDegree,
                            evaluationData.explanation
                        )
                    )
                else null

            return finalizePhaseExecution(user, sequence, sequence.assignment!!.id!!, locale, lastResponse)
        }
    }

    data class EvaluationData(
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    )
}