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

package org.elaastic.ai.evaluation.chatgpt.prompt

import org.elaastic.user.PrincipalUserResolver

import org.elaastic.common.web.MessageBuilder
import org.elaastic.user.User
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.transaction.Transactional

@Controller
@RequestMapping("/chatgpt/prompt")
@Transactional
class ChatGptPromptController(
    val chatGptPromptService: ChatGptPromptService,
    val messageBuilder: MessageBuilder
) {
    companion object {
        const val DEFAULT_LANGUAGE = "en"
        const val FR_LANGUAGE = "fr"
        const val ES_LANGUAGE = "es"
    }

    @GetMapping(value = ["/show"])
    fun show(authentication: Authentication, model: Model): String {
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser
        model.addAttribute("user", user)
        updateModelWithActivePrompts(model)
        return "ai/evaluation/chatgpt/prompt/show"
    }

    @GetMapping("/edit")
    fun edit(
        authentication: Authentication,
        model: Model
    ): String {
        val user: User = (authentication.principal as PrincipalUserResolver).elaasticUser
        model.addAttribute("user", user)
        updateModelWithActivePrompts(model)
        return "ai/evaluation/chatgpt/prompt/edit"
    }

    @PostMapping("/update")
    fun updatePrompt(
        @RequestParam language: String,
        @RequestParam content: String,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        chatGptPromptService.updatePrompt(
            content = content,
            language = language,
        ).let {
            with(messageBuilder) {
                success(
                    redirectAttributes,
                    message(
                        "default.updated.message",
                        "ChatGpt Prompt",
                        it.language
                    )
                )
            }
        }
        return "redirect:/chatgpt/prompt/edit"
    }

    private fun updateModelWithActivePrompts(model: Model) {
        // get the active prompt in DEFAULT_LANGUAGE
        val promptDefaultLanguage = chatGptPromptService.getPrompt(DEFAULT_LANGUAGE)
        // get the active prompt in FR_LANGUAGE
        val promptFrLanguage = chatGptPromptService.getPrompt(FR_LANGUAGE)
        // get the active prompt in ES_LANGUAGE
        val promptEsLanguage = chatGptPromptService.getPrompt(ES_LANGUAGE)
        model.addAttribute("promptDefaultLanguage", promptDefaultLanguage)
        model.addAttribute("promptFrLanguage", promptFrLanguage)
        model.addAttribute("promptEsLanguage", promptEsLanguage)
    }
}