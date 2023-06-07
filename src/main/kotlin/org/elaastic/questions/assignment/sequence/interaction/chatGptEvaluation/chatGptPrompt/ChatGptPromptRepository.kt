package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation.chatGptPrompt

import org.springframework.data.jpa.repository.JpaRepository

interface ChatGptPromptRepository : JpaRepository<ChatGptPrompt, Long> {

    fun findByLanguageAndActive(language: String, active: Boolean = true) : ChatGptPrompt?

}