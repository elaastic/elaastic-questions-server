package org.elaastic.consolidation.subject.question

import org.elaastic.consolidation.subject.question.attachment.PracticeAttachment
import org.elaastic.consolidation.subject.question.specification.ExclusiveChoiceQuestionSpecification
import org.elaastic.consolidation.subject.question.specification.MultipleChoiceQuestionSpecification
import org.elaastic.consolidation.subject.question.specification.OpenQuestionSpecification
import org.elaastic.material.instructional.question.ChoiceSpecification
import org.elaastic.material.instructional.question.ExclusiveChoiceSpecification
import org.elaastic.material.instructional.question.MultipleChoiceSpecification
import org.elaastic.material.instructional.question.QuestionType
import org.elaastic.sequence.Sequence

object PracticeQuestionFactory {

    fun buildQuestion(sequence: Sequence, explanations: List<PracticeLearnerExplanation>?) =
        PracticeQuestion(
            id = sequence.uuid,
            rank = sequence.rank,
            title = sequence.statement.title,
            content = sequence.statement.content,
            expectedExplanation = sequence.statement.expectedExplanation ?: "",
            specification = when (sequence.statement.questionType) {
                QuestionType.OpenEnded -> OpenQuestionSpecification()
                QuestionType.ExclusiveChoice -> parseExclusiveChoiceSpecification(sequence.statement.choiceSpecification)
                QuestionType.MultipleChoice -> parseMultipleChoiceSpecification(sequence.statement.choiceSpecification)
            },
            attachment = sequence.statement.attachment?.let(::PracticeAttachment),
            explanations = if (explanations != null) explanations else emptyList()
        )

    private fun parseMultipleChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is MultipleChoiceSpecification ->
                MultipleChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndexList = choiceSpecification.expectedChoiceList.map { it.index }
                )

            else -> throw IllegalArgumentException("provided choice specification is not a multiple choice specification !")
        }

    private fun parseExclusiveChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is ExclusiveChoiceSpecification ->
                ExclusiveChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndex = choiceSpecification.expectedChoice.index
                )

            else -> throw IllegalArgumentException("provided choiceSpecification is not an exclusive choice")
        }
}