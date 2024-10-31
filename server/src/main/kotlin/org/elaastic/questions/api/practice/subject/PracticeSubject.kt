package org.elaastic.questions.api.practice.subject

import com.fasterxml.jackson.annotation.JsonIgnore
import org.elaastic.questions.api.practice.subject.question.PracticeQuestion
import org.elaastic.questions.assignment.Assignment
import java.util.*

/**
 * A practice subject contains questions on which learners can practice.
 * Practice subjects are constructed from sequences played by learners during assignments.
 * Each question of a practice subject is attached to the 3 best ranked learners explanations
 * provided during the elaastic sequence execution. Those explanations will be used as feedbacks
 * for learners practicing the subject afterward.
 *
 *
 * @author John Tranier
 */
class PracticeSubject(
    id: UUID,
    title: String,

    @JsonIgnore
    val questions: List<PracticeQuestion>,

    @JsonIgnore
    val learners: Set<PracticeLearner>,

    @JsonIgnore
    val topic: PracticeTopic? = null,
) : SummaryPracticeSubject(id, title) {

    constructor(
        assignment: Assignment,
        questions: List<PracticeQuestion>,
        learners: Set<PracticeLearner>,
        topic: PracticeTopic? = null,
    ) : this(
        id = assignment.globalId,
        title = assignment.title,
        questions = questions,
        learners = learners,
        topic = topic,
    )
}