package org.elaastic.player.results

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.common.web.MessageBuilder
import org.elaastic.sequence.Sequence
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Service to build the model for the teacher result dashboard.
 *
 * Do not mix with the [DashboardModelFactory][org.elaastic.player.dashboard.DashboardModelFactory]
 * which display information about the progression of learner in a sequence.
 */
@Service
class TeacherResultDashboardService(
    @Autowired val responseService: ResponseService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
) {

    fun buildModel(sequence: Sequence): ResultsModel {

        val responseSet = responseService.findAll(sequence, excludeFakes = false)

        val listIdResponse = responseSet[1].map { it.id } + responseSet[2].map { it.id }

        val chatGptEvaluationResponseStore =
            chatGptEvaluationService.associateResponseToChatGPTEvaluationExistence(listIdResponse)

        return ResultsModelFactory.build(
            true,
            sequence,
            responseSet = responseSet,
            true,
            messageBuilder,
            peerGradings = peerGradingService.findAllByAttempt(sequence, 1),
            chatGptEvaluationResponseStore = chatGptEvaluationResponseStore
        )
    }

}
