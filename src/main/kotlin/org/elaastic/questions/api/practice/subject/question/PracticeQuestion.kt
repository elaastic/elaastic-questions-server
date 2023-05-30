package org.elaastic.questions.api.practice.subject.question

import com.fasterxml.jackson.annotation.JsonIgnore
import com.toedter.spring.hateoas.jsonapi.JsonApiId
import com.toedter.spring.hateoas.jsonapi.JsonApiType
import org.elaastic.questions.api.practice.subject.question.attachment.PracticeAttachment
import org.elaastic.questions.api.practice.subject.question.specification.QuestionSpecification

/**
 * Represents a question of a practice subject
 * It may have an attachement.
 * It has a list learner explanations (the 3 best ranked) that will be used as feedbacks provided
 * to learner practicing on this question.
 * 
 * @author John Tranier
 */
class PracticeQuestion(
    @JsonApiId
    val id: Long,
    val rank: Int,
    val title: String,
    val content: String,
    val expectedExplanation: String,
    val specification: QuestionSpecification,

    @JsonIgnore
    val explanations: List<PracticeLearnerExplanation>,

    @JsonIgnore
    val attachment: PracticeAttachment?,
) {
    @JsonApiType
    val type = "practice-question"
}