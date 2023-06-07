package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class ChatGptEvaluationSaver (
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository
) {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun saveEvaluation(chatGptEvaluation: ChatGptEvaluation) = chatGptEvaluationRepository.saveAndFlush(chatGptEvaluation)

}