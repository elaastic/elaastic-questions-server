package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChatGptPromptService(
    @Autowired val chatGptPromptRepository: ChatGptPromptRepository
) {

    fun getPrompt(language: String): ChatGptPrompt {
        return chatGptPromptRepository.findByLanguageAndActive(language)
            ?: chatGptPromptRepository.findByLanguageAndActive("en")!!
    }
}