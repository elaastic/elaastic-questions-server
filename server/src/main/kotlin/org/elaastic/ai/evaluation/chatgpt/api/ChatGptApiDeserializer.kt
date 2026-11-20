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

package org.elaastic.ai.evaluation.chatgpt.api

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class ChatGptApiDeserializer : JsonDeserializer<ChatGptApiResponseData>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ChatGptApiResponseData {
        val node: JsonNode = p.codec.readTree(p)
        val usage: JsonNode = node["usage"]
        val messageList = node["choices"].map {
            ChatGptApiMessageData(
                role = it["message"]["role"].asText(),
                content = it["message"]["content"].asText(),
                finishReason = it["finish_reason"].asText()
            )
        }


        return ChatGptApiResponseData(
            id = node["id"].asText(),
            objectValue = node["object"].asText(),
            created = node["created"].asLong(),
            model = node["model"].asText(),
            promptTokens = usage["prompt_tokens"].asInt(),
            completionTokens = usage["completion_tokens"].asInt(),
            totalTokens = usage["total_tokens"].asInt(),
            messageList = messageList,
        )
    }
}