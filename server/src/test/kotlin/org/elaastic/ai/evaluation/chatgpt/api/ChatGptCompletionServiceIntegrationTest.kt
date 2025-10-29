package org.elaastic.ai.evaluation.chatgpt.api

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.prompt.ChatGptPromptService
import org.elaastic.test.IntegrationTestingService
import org.elaastic.test.directive.tThen
import org.elaastic.test.directive.tWhen
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.notNullValue
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["no-async"])
@EnabledIf(value = "#{environment.acceptsProfiles('chatgpt')}", loadContext = true)
@Transactional
open class ChatGptCompletionServiceIntegrationTest(
    @Autowired val integrationTestingService: IntegrationTestingService,
    @Autowired val chatGptCompletionService: ChatGptCompletionService,
    @Autowired val chatGptPromptService: ChatGptPromptService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
) {

    @BeforeEach
    @Transactional
    open fun setup() {
        chatGptEvaluationRepository.deleteAll()
        // Precondition
        assertTrue(chatGptEvaluationRepository.findAll().isEmpty())
        // We want a reasonable good prompt for the test
        chatGptPromptService.updatePrompt(
            "Tu es un enseignant bienveillant qui doit évaluer la réponse donnée par un élève"
                    + " à une question. "
                    + "Tu dois donner une note comprise entre 0 et 5 à la réponse de l'élève et expliquer"
                    + " pourquoi tu as donné cette note. Tu dois fournir la réponse sous la forme d'un objet Json ayant " +
                    "la structure suivante : { \"grade\": \"\", \"annotation\": \"\" } . " +
                    "Merci de ne pas encapsuler l'objet json dans une enveloppe markdown." +
                    "La question est fournie dans le JSON suivant contenant la question et la réponse de l'élève et son score sur la base de ce qu'il a choisi comme item.",
            "fr"
        )
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    open fun `get a chatgpt evaluation - invalid (no student answer)`() {

        val response = integrationTestingService.getAnyResponse()
        response.explanation = null

        tWhen {
            val block: () -> Unit = {
                chatGptCompletionService.createEvaluation(response, "fr")
            }
            block
        }.tThen {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "Error: No explanation to evaluate"
            )
        }
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    open fun `get a chatgpt evaluation - invalid (no teacher explanation)`() {

        val response = integrationTestingService.getAnyResponse()
        response.explanation =
            "Git est le meilleur système de gestion de version, il coche donc toutes les bonnes options."

        response.statement.expectedExplanation = null

        tWhen {
            val block: () -> Unit = {
                chatGptCompletionService.createEvaluation(response, "fr")
            }
            block
        }.tThen {
            assertThrows(
                IllegalArgumentException::class.java,
                it,
                "Error: You must define an expected explanation to create a ChatGPT evaluation"
            )
        }
    }

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
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    open fun `get a chatgpt evaluation - valid`() {

        val response = integrationTestingService.getAnyResponse()
        response.explanation =
            "Git est le meilleur système de gestion de version, il coche donc toutes les bonnes options."
        val promptFr = chatGptPromptService.getPrompt("fr")

        tWhen {
            chatGptCompletionService.createEvaluation(response, "fr")
        }.tThen {
            assertThat(it.id, notNullValue())
            assertThat(it.dateCreated, notNullValue())
            assertThat(it.lastUpdated, notNullValue())

            assertThat(it.status, equalTo("DONE"))
            assertThat(it.annotation, notNullValue())
            assertEquals(promptFr, it.prompt)

            assertThat(it.reportReasons, nullValue())
            assertThat(it.reportComment, nullValue())
            assertThat(it.utilityGrade, nullValue())

            assertThat(it.hiddenByTeacher, equalTo(false))
            assertThat(it.removedByTeacher, equalTo(false))
        }
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