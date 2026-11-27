/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.player.results

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ResponseService
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.common.web.MessageBuilder
import org.elaastic.sequence.Sequence
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.togglz.core.manager.FeatureManager

@Service
class TeacherResultDashboardService(
    @Autowired val responseService: ResponseService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val messageBuilder: MessageBuilder,
    @Autowired val featureManager: FeatureManager,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
) {

    fun buildModel(sequence: Sequence): ResultsModel {

        val responseSet = responseService.findAll(sequence, excludeFakes = false)

        val listIdResponse = responseSet[1].map { it.id } + responseSet[2].map { it.id }

        val explanationHasChatGPTEvaluationMap: Map<Long, Boolean> =
            chatGptEvaluationService.associateResponseToChatGPTEvaluationExistence(listIdResponse)

        return ResultsModelFactory.build(
            true,
            sequence,
            featureManager,
            responseSet = responseSet,
            true,
            messageBuilder,
            peerGradings = peerGradingService.findAllByAttempt(sequence, 1),
            explanationHasChatGPTEvaluationMap = explanationHasChatGPTEvaluationMap
        )
    }

}
