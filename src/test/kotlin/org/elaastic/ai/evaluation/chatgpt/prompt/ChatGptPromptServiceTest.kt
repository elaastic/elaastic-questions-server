package org.elaastic.ai.evaluation.chatgpt.prompt

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
class ChatGptPromptServiceTest(
    @Autowired
    val chatGptPromptService: ChatGptPromptService
) {

    @Test
    fun testGetPromptOnExistingPromptForLanguage() {
        // get the prompt for language "en"
        val prompt = chatGptPromptService.getPrompt("en")
        // check that the prompt is active
        assert(prompt.active)
        // check that the prompt language is correct
        assert(prompt.language == "en")
    }

    @Test
    fun testGetPromptOnNonExistingPromptForLanguage() {
        // get the prompt for language "es"
        val prompt = chatGptPromptService.getPrompt("es")
        // get the prompt for language "en"
        val promptEn = chatGptPromptService.getPrompt("en")
        // check that both prompts are the same
        assert(prompt == promptEn)
    }

    @Test
    fun testUpdatePromptWhenNoPromptExists() {
        // update prompt for language "es"
        val prompt = chatGptPromptService.updatePrompt("prompt content", "es")
        // check that the prompt is active
        assert(prompt.active)
        // check that the prompt content is correct
        assert(prompt.content == "prompt content")
        // check that the prompt language is correct
        assert(prompt.language == "es")
    }

    @Test
    fun testUpdatePromptWhenPromptExists() {
        // get the prompt for language "en"
        val prompt = chatGptPromptService.getPrompt("en")
        // update prompt for language "en"
        val updatedPrompt = chatGptPromptService.updatePrompt("updated prompt content", "en")
        // check that the prompt is not active
        assert(!prompt.active)
        // check that the prompt has an end date
        assert(prompt.endDate != null)
        // check that the updated prompt is active
        assert(updatedPrompt.active)
        // check that the updated prompt content is correct
        assert(updatedPrompt.content == "updated prompt content")
        // check that the updated prompt language is correct
        assert(updatedPrompt.language == "en")
    }
}