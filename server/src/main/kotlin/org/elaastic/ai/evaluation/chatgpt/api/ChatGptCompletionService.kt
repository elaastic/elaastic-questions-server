/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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
package org.elaastic.ai.evaluation.chatgpt.api

import org.elaastic.activity.response.Response
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluation

interface ChatGptCompletionService {

    /**
     * Create a ChatGPT evaluation for a response.
     *
     * @param response the response to evaluate
     * @param language the language of the evaluation
     * @param chatGptExistingEvaluation the existing evaluation if it exists
     * @return the created evaluation
     */
    fun createEvaluation(
        response: Response,
        language: String,
        chatGptExistingEvaluation: ChatGptEvaluation? = null
    ): ChatGptEvaluation

    /**
     * Get the response from the ChatGPT API
     * @param messages List of messages to send to the API
     * @return ChatGptApiResponseData
     */
    fun getChatGptResponse(messages: List<ChatGptApiMessageData>, nParameter: Int = 1): ChatGptApiResponseData
}