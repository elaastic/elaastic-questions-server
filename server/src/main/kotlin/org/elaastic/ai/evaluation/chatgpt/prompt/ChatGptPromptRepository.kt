package org.elaastic.ai.evaluation.chatgpt.prompt

import org.springframework.data.jpa.repository.JpaRepository

interface ChatGptPromptRepository : JpaRepository<ChatGptPrompt, Long> {

    fun findByLanguageAndActive(language: String, active: Boolean = true) : ChatGptPrompt?

}