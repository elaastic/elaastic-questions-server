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

package org.elaastic.sequence.phase.evaluation.all_at_once

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
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/all-at-once")
class AllAtOnceEvaluationPhaseExecutionController(
    @Autowired override val sequenceService: SequenceService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired override val responseService: ResponseService,
    @Autowired override val chatGptEvaluationService: ChatGptEvaluationService
) : AbstractEvaluationPhaseExecutionController(
    sequenceService,
    responseService,
    chatGptEvaluationService
) {

    @PostMapping("/submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable sequenceId: Long,
        @ModelAttribute evaluationData: EvaluationData,
        locale: Locale
    ): String {
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser
        var assignment: Assignment

        sequenceService.get(sequenceId, true).let { sequence ->
            assignment = sequence.assignment!!
            evaluationData.getGrades().forEach {
                peerGradingService.createOrUpdateLikert(
                    user,
                    responseService.getReferenceById(it.key),
                    it.value.toBigDecimal()
                )
            }

            var lastResponse: Response? = null
            if (sequence.isSecondAttemptAllowed()
                && !responseService.hasResponseForUser(user, sequence, 2)
            ) {
                lastResponse = changeAnswer(
                    user,
                    sequence,
                    Answer(
                        evaluationData.choiceList,
                        evaluationData.confidenceDegree,
                        evaluationData.explanation
                    )
                )
            }

            return finalizePhaseExecution(user, sequence, assignment.id!!, locale, lastResponse)
        }
    }

    class EvaluationData(
        val id: Long,
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    ) {
        private var grades = HashMap<Long, Int>()

        fun getGrades() = grades

        fun setGrades(value: HashMap<Long, Int>) {
            grades = value
        }
    }
}