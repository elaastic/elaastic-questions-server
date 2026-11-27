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

import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

@Service
@Transactional
class ChatGptPromptService(
    val chatGptPromptRepository: ChatGptPromptRepository
) {

    /**
     * Get the active prompt in the given language
     * @param language the language of the prompt
     * @return the active prompt in the given language
     */
    fun getPrompt(language: String): ChatGptPrompt {
        return chatGptPromptRepository.findByLanguageAndActive(language)
            ?: chatGptPromptRepository.findByLanguageAndActive("en")!!
    }

    /**
     * Update the active prompt in the given language
     * @param content the content of the prompt
     * @param language the language of the prompt
     * @return the updated prompt
     */
    fun updatePrompt(content: String, language: String): ChatGptPrompt {
        // Make no more active the current active prompt
        chatGptPromptRepository.findByLanguageAndActive(language)?. let {
            it.active = false
            it.endDate = Date()
            chatGptPromptRepository.saveAndFlush(it)
        }
        // create the new active prompt
        val newPrompt = ChatGptPrompt(
            content = content,
            language = language,
        )
        return chatGptPromptRepository.save(newPrompt)
    }
}