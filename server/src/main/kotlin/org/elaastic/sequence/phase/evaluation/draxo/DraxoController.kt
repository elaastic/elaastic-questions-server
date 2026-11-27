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
import org.elaastic.activity.evaluation.peergrading.draxo.DraxoEvaluation
import org.elaastic.activity.evaluation.peergrading.draxo.criteria.CriteriaEvaluation
import org.elaastic.activity.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/draxo/response/{responseId}")
class DraxoController(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
) {

    @PostMapping("submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        @PathVariable sequenceId: Long,
        @PathVariable responseId: Long,
        @RequestParam lastResponseToGrade: Boolean,
        @RequestBody criteriaEvaluationList: List<CriteriaEvaluation>,
    ): ResponseEntity<DraxoEvaluation> {
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser

        val evaluation = DraxoEvaluation().addEvaluationList(criteriaEvaluationList)
        val response = responseService.getReferenceById(responseId)

        require(response.interaction.sequence.id == sequenceId) {
            "The response $responseId is not bound to the sequence $sequenceId"
        }

        val peerGrading = draxoPeerGradingService.createOrUpdateDraxo(user, response, evaluation, lastResponseToGrade)

        return ResponseEntity.ok(peerGrading.getDraxoEvaluation())
    }

}