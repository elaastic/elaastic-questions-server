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

package org.elaastic.sequence.phase.evaluation.draxo

import org.elaastic.user.PrincipalUserResolver

import org.elaastic.activity.evaluation.peergrading.PeerGradingService
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.activity.results.ItemIndex
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.assignment.Assignment
import org.elaastic.sequence.SequenceService
import org.elaastic.sequence.phase.evaluation.AbstractEvaluationPhaseExecutionController
import org.elaastic.user.User
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
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/draxo")
class DraxoEvaluationPhaseExecutionController(
    @Autowired override val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired override val responseService: ResponseService,
    @Autowired override val chatGptEvaluationService: ChatGptEvaluationService,
) : AbstractEvaluationPhaseExecutionController(
    sequenceService,
    responseService,
    chatGptEvaluationService
) {

    @PostMapping("/close-evaluation")
    fun closeEvaluationPhase(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
        @ModelAttribute changeAnswerData: ChangeAnswerData,
        locale: Locale
    ): String {
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser
        var assignment: Assignment

        sequenceService.get(sequenceId, true).let { sequence ->
            assignment = sequence.assignment!!

            var lastResponse: Response? = null
            // Note : we systematically add a 2nd attempt at the end of the review ; if the learner didn't change its response, we store a copy of its 1st attempts as the 2nd attempt
            if (changeAnswerData.changeAnswer || changeAnswerData.lastResponseToGrade) {
                lastResponse = changeAnswer(
                    user,
                    sequence,
                    Answer(
                        changeAnswerData.choiceList,
                        changeAnswerData.confidenceDegree,
                        changeAnswerData.explanation
                    )
                )
            }


            if (changeAnswerData.lastResponseToGrade) {
                finalizePhaseExecution(user, sequence, assignment.id!!, locale, lastResponse)
            }

            return "redirect:/player/assignment/${assignment.id}/play/sequence/${sequence.id}"
        }
    }

    class ChangeAnswerData(
        //val responseId: Long, // TODO : Should store the response that leads to change answer

        val changeAnswer: Boolean = false,
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?,
        val lastResponseToGrade: Boolean,
    )
}