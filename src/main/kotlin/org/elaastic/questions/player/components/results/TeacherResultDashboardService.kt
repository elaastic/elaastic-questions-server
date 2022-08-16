package org.elaastic.questions.player.components.results

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.controller.MessageBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.togglz.core.manager.FeatureManager

@Service
class TeacherResultDashboardService(
    @Autowired val responseService: ResponseService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val featureManager: FeatureManager,
) {

    fun buildModel(sequence: Sequence): ResultsModel =
        ResultsModelFactory.build(
            true,
            sequence,
            featureManager,
            responseSet = responseService.findAll(sequence, excludeFakes = false),
            true,
            messageBuilder,
            peerGradings = peerGradingService.findAll(sequence)
        )
}