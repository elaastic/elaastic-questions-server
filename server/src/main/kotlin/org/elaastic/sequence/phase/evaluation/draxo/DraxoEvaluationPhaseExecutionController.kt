package org.elaastic.sequence.phase.evaluation.draxo

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.Assignment
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.activity.results.ItemIndex
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.user.User
import org.elaastic.sequence.phase.evaluation.AbstractEvaluationPhaseExecutionController
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
        val user: User = authentication.principal as User
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