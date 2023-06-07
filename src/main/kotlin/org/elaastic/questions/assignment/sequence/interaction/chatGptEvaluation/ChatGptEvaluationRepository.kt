package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.springframework.data.jpa.repository.JpaRepository


interface ChatGptEvaluationRepository : JpaRepository<ChatGptEvaluation, Long> {

    fun findByResponse(response: Response): ChatGptEvaluation?

}