package org.elaastic.questions.assignment.sequence.explanation

import org.elaastic.questions.assignment.Statement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class FakeExplanationService(
        @Autowired val fakeExplanationRepository: FakeExplanationRepository
) {

    fun findAllByStatement(statement: Statement): List<FakeExplanation> {
        return fakeExplanationRepository.findAllByStatement(statement)
    }

}