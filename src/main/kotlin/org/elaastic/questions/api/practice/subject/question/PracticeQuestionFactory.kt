package org.elaastic.questions.api.practice.subject.question

import org.elaastic.questions.api.practice.subject.question.attachment.PracticeAttachment
import org.elaastic.questions.api.practice.subject.question.specification.ExclusiveChoiceQuestionSpecification
import org.elaastic.questions.api.practice.subject.question.specification.MultipleChoiceQuestionSpecification
import org.elaastic.questions.api.practice.subject.question.specification.OpenQuestionSpecification
import org.elaastic.questions.assignment.QuestionType
import org.elaastic.questions.assignment.choice.ChoiceSpecification
import org.elaastic.questions.assignment.sequence.Sequence

object PracticeQuestionFactory {

    fun buildQuestion(sequence: Sequence, explanations: List<PracticeLearnerExplanation>) =
        PracticeQuestion(
            id = sequence.id!!,
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
            explanations = explanations
        )

    private fun parseMultipleChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is org.elaastic.questions.assignment.choice.MultipleChoiceSpecification ->
                MultipleChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndexList = choiceSpecification.expectedChoiceList.map { it.index }
                )

            else -> throw IllegalArgumentException("provided choice specification is not a multiple choice specification !")
        }

    private fun parseExclusiveChoiceSpecification(choiceSpecification: ChoiceSpecification?) =
        when (choiceSpecification) {
            is org.elaastic.questions.assignment.choice.ExclusiveChoiceSpecification ->
                ExclusiveChoiceQuestionSpecification(
                    nbCandidateItem = choiceSpecification.nbCandidateItem,
                    expectedChoiceIndex = choiceSpecification.expectedChoice.index
                )

            else -> throw IllegalArgumentException("provided choiceSpecification is not an exclusive choice")
        }
}