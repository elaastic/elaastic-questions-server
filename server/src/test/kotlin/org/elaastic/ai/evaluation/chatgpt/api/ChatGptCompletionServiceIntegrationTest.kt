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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["no-async"])
@EnabledIf(value = "#{@featureManager.isActive(@featureResolver.getFeature('CHATGPT_EVALUATION'))}", loadContext = true)
@Transactional
class ChatGptCompletionServiceIntegrationTest(
    @Autowired val chatGptCompletionService: ChatGptCompletionService
) {

    @Test
    fun testGetChatGptResponseWithOneMessage2Responses() {
        // given two messages to send to the API
        val messages = listOf(
            ChatGptApiMessageData("user", "Please send me a short abstract of the movie 'The Mask'." +
                    "You will send me two choices. The first one in plain text and the second one in " +
                    "JSON format with the following structure: { \"director\": \"\", \"main_actor\": \"\" }\"}."),
        )
        // when sending the messages to the API
        val response = chatGptCompletionService.getChatGptResponse(messages,2)
        // then the response should contain the messages and the completion tokens
        assertEquals(2, response.messageList.size)
        assertNotNull(response.messageList[0].content)
        assertNotNull(response.messageList[1].content)
        // display the response
        //println(response.toSimpleString())
        println("---- first message ----")
        println(response.messageList[0].content)
        println("---- second message ----")
        println(response.messageList[1].content)
    }

    @Test
    fun testGetChatGptResponseWithOneMessageOneJsonResponse() {
        // given two messages to send to the API
        val messages = listOf(
            ChatGptApiMessageData("user", "Please give me movie whom Jim Carrey play" +
                    "You will send the response as a " +
                    "JSON object with the following structure:" +
                    " { \"director\": \"\", \"title\": \"\" }\"}." +
                    "Please don't encapsulate the json object in a markdown envelop."),
        )
        // when sending the messages to the API
        val response = chatGptCompletionService.getChatGptResponse(messages)
        // then the response should contain the messages and the completion tokens
        assertEquals(1, response.messageList.size)
        assertNotNull(response.messageList[0].content)
        // display the response
        println(response.toSimpleString())
    }
}