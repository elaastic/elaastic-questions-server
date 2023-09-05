package org.elaastic.questions.assignment.sequence.interaction.chatgptEvaluation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
class ChatgptEvaluationSaver (
    @Autowired val chatgptEvaluationRepository: ChatgptEvaluationRepository
) {

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    fun saveEvaluation(chatgptEvaluation: ChatgptEvaluation) = chatgptEvaluationRepository.saveAndFlush(chatgptEvaluation)

}