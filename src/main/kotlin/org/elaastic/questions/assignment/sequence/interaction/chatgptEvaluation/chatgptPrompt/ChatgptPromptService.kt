package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptPrompt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChatgptPromptService (
    @Autowired val chatgptPromptRepository: ChatgptPromptRepository
) {

    fun getPrompt(language: String) : ChatgptPrompt {
        return chatgptPromptRepository.findByLanguageAndActive(language) ?: chatgptPromptRepository.findByLanguageAndActive("en")!!
    }
}