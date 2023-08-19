package org.elaastic.questions.player.phase.evaluation.draxo

import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoEvaluation
import org.elaastic.questions.directory.User
import org.elaastic.questions.assignment.sequence.peergrading.draxo.criteria.CriteriaEvaluation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/player/sequence/{sequenceId}/phase/evaluation/draxo/response/{responseId}")
class DraxoController(
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val responseService: ResponseService,
) {

    @PostMapping("submit-evaluation")
    fun submitEvaluation(
        authentication: Authentication,
        @PathVariable sequenceId: Long,
        @PathVariable responseId: Long,
        @RequestBody criteriaEvaluationList: List<CriteriaEvaluation>
    ): ResponseEntity<DraxoEvaluation> {
        val user: User = authentication.principal as User

        val evaluation = DraxoEvaluation().addEvaluationList(criteriaEvaluationList)
        val response = responseService.getReferenceById(responseId)

        require(response.interaction.sequence.id == sequenceId) {
            "The response $responseId is not bound to the sequence $sequenceId"
        }

        val peerGrading = peerGradingService.createOrUpdateDraxo(user, response, evaluation)

        return ResponseEntity.ok(peerGrading.getDraxoEvaluation())
    }

}