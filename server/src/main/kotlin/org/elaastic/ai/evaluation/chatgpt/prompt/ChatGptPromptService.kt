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