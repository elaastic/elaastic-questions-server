package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.assignment.StatementRepository
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanation
import org.elaastic.questions.assignment.sequence.explanation.FakeExplanationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class StatementService(
        @Autowired val statementRepository: StatementRepository,
        @Autowired val fakeExplanationRepository: FakeExplanationRepository
) {
    fun save(statement: Statement): Statement {
        return statementRepository.save(statement)
    }

    fun delete(statementId: Long) {
        statementRepository.deleteById(statementId)
    }

    fun addFakeExplanation(statement: Statement,
                           fakeExplanationData: FakeExplanationData): FakeExplanation {
        require(fakeExplanationData.content != null) {
            "The content of the fake explanation must not be null"
        }

        return fakeExplanationRepository.save(
                FakeExplanation(
                        author = statement.owner,
                        statement = statement,
                        content = fakeExplanationData.content!!,
                        correspondingItem = fakeExplanationData.correspondingItem
                )
        )

    }

    fun updateFakeExplanationList(statement: Statement,
                                  fakeExplanationDataList: List<FakeExplanationData>) {
        if(fakeExplanationDataList.isNotEmpty()) {
            removeAllFakeExplanation(statement)
            fakeExplanationDataList.forEach {
                addFakeExplanation(statement, it)
            }
        }
    }

    fun removeAllFakeExplanation(statement: Statement) {
        fakeExplanationRepository.deleteAllByStatement(statement)
    }
}