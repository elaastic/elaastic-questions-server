package org.elaastic.sequence.phase.evaluation

import org.elaastic.material.instructional.question.legacy.LearnerChoice
import org.elaastic.activity.response.ConfidenceDegree
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.SequenceService
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.activity.response.Response
import org.elaastic.activity.response.ResponseService
import org.elaastic.activity.results.ItemIndex
import org.elaastic.user.User
import java.util.Locale

abstract class AbstractEvaluationPhaseExecutionController(
    open val sequenceService: SequenceService,
    open val responseService: ResponseService,
    open val chatGptEvaluationService: ChatGptEvaluationService
) {

    fun changeAnswer(user: User, sequence: Sequence, answer: Answer): Response {
        val choiceListSpecification = answer.choiceList?.let {
            LearnerChoice(it)
        }

        check(sequence.isSecondAttemptAllowed())
        val previousResponse = responseService.find(user, sequence, 2)

        val response = Response(
            learner = user,
            interaction = sequence.getResponseSubmissionInteraction(),
            attempt = 2,
            confidenceDegree = answer.confidenceDegree,
            explanation = answer.explanation,
            learnerChoice = choiceListSpecification,
            score = choiceListSpecification?.let {
                Response.computeScore(
                    it,
                    sequence.statement.choiceSpecification
                        ?: error("The choice specification is undefined")
                )
            },
            statement = sequence.statement

        )

        if(previousResponse != null)  {
            response.makeAsUpdateOf(previousResponse)
        }


        val userActiveInteraction = sequenceService.getActiveInteractionForLearner(sequence, user)
            ?: error("No active interaction, cannot submit a response")

        return responseService.save(
            userActiveInteraction,
            response
        )

    }

    fun finalizePhaseExecution(user: User,
                               sequence: Sequence,
                               assignmentId: Long,
                               locale: Locale,
                               lastResponse: Response? = null): String {
        if (sequence.executionIsDistance() || sequence.executionIsBlended()) {
            sequenceService.nextInteractionForLearner(sequence, user)
        }

        if (sequence.chatGptEvaluationEnabled && lastResponse != null) {
            chatGptEvaluationService.createEvaluation(lastResponse, locale.language)
        }

        return "redirect:/player/assignment/${assignmentId}/play/sequence/${sequence.id}"
    }

    class Answer(
        val choiceList: List<ItemIndex>?,
        val confidenceDegree: ConfidenceDegree?,
        val explanation: String?
    )
}