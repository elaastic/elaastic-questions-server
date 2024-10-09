package org.elaastic.ai.evaluation.chatgpt.prompt

import org.elaastic.questions.directory.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.transaction.Transactional

@Controller
@RequestMapping("/chatgpt/prompt")
@Transactional
class ChatGptPromptController(
    val chatGptPromptService: ChatGptPromptService
) {
    companion object {
        const val DEFAULT_LANGUAGE = "en"
        const val FR_LANGUAGE = "fr"
        const val ES_LANGUAGE = "es"
    }

    @GetMapping(value = ["/show"])
    fun show(authentication: Authentication, model: Model): String {
        val user: User = authentication.principal as User
        // get the active prompt in DEFAULT_LANGUAGE
        val promptDefaultLanguage = chatGptPromptService.getPrompt(DEFAULT_LANGUAGE)
        // get the active prompt in FR_LANGUAGE
        val promptFrLanguage = chatGptPromptService.getPrompt(FR_LANGUAGE)
        // get the active prompt in ES_LANGUAGE
        val promptEsLanguage = chatGptPromptService.getPrompt(ES_LANGUAGE)
        model.addAttribute("user", user)
        model.addAttribute("promptDefaultLanguage", promptDefaultLanguage)
        model.addAttribute("promptFrLanguage", promptFrLanguage)
        model.addAttribute("promptEsLanguage", promptEsLanguage)

        return "ai/evaluation/chatgpt/prompt/show"
    }
}