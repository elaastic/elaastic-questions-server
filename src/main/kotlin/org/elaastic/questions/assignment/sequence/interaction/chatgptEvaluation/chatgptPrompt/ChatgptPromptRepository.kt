package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation.chatgptPrompt

import org.springframework.data.jpa.repository.JpaRepository

interface ChatgptPromptRepository : JpaRepository<ChatgptPrompt, Long> {

    fun findByLanguageAndActive(language: String, active: Boolean = true) : ChatgptPrompt?

}