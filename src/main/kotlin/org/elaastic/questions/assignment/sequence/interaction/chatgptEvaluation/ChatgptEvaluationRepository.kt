package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.springframework.data.jpa.repository.JpaRepository


interface ChatgptEvaluationRepository : JpaRepository<ChatgptEvaluation, Long> {

    fun findByResponse(response: Response): ChatgptEvaluation?

}