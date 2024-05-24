package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingController.ResponseSubmissionAsynchronous
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/chatGptEvaluation")
class ChatGptEvaluationController(
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val messageSource: MessageSource,
) {

    @GetMapping("/hide/{gradingId}")
    fun hideChatGptEvaluation(
        authentication: Authentication,
        model: Model,
        @PathVariable gradingId: Long,
    ): ResponseSubmissionAsynchronous {
        val user = authentication.principal as User
        val chatGptEvaluation = chatGptEvaluationService.findEvaluationById(gradingId)

        val locale: Locale = LocaleContextHolder.getLocale()

        return if (chatGptEvaluation != null) {
            try {
                chatGptEvaluationService.markAsHidden(chatGptEvaluation, user)
                ResponseSubmissionAsynchronous(
                    success = true,
                    header = messageSource.getMessage("evaluation.hideEvaluation.success.header", null, locale),
                    content = messageSource.getMessage("evaluation.hideEvaluation.success.content", null, locale)
                )
            } catch (e: IllegalAccessException) {
                ResponseSubmissionAsynchronous(
                    success = false,
                    header = messageSource.getMessage("evaluation.accesDenied.header", null, locale),
                    content = messageSource.getMessage("evaluation.hideEvaluation.accesDenied.content", null, locale)
                )
            }
        } else {
            ResponseSubmissionAsynchronous(
                success = false,
                header = messageSource.getMessage("evaluation.hideEvaluation.error.header", null, locale),
                content = messageSource.getMessage("evaluation.hideEvaluation.error.content", null, locale)
            )
        }

    }
}