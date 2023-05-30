package org.elaastic.questions.api.practice.subject

import org.elaastic.questions.api.practice.subject.question.PracticeLearnerExplanation
import org.elaastic.questions.api.practice.subject.question.PracticeQuestionFactory
import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Service for manipulating practice subjects
 *
 * A practice subject can be built from an assignment that is ready to practice (meaning it has terminated sequences
 * for which results have been published)
 *
 * @author John Tranier
 */
@Service
class PracticeSubjectService(
    @Autowired val assignmentService: AssignmentService,
    @Autowired val sequenceService: SequenceService,
    @Autowired val responseService: ResponseService,
) {

    /**
     * Find all the PracticeSubject that have been published or updated after the "since" parameter
     */
    fun findAllPracticeSubject(since: LocalDateTime) =
        assignmentService.findAllAssignmentUpdatedSince(since)
            .filter(::isSubjectReadyToPractice)
            .map(::SummaryPracticeSubject)

    /**
     * Get a practice subject from its id
     * @param id identifier of the practice subject that directly matched the corresponding assignment id
     */
    fun getPracticeSubject(id: Long) =
        assignmentService.get(id, true)
            .let { assignment ->
                assignment.sequences.map(sequenceService::loadInteractions)

                val sequences = assignment.sequences.filter(::isSequenceReadyToPractice)

                check(sequences.isNotEmpty()) { "The subject $id is not ready to practice" }

                val learners = assignmentService.findAllLearnersRegisteredOn(assignment)

                PracticeSubject(
                    assignment =  assignment,
                    questions = sequences
                        .map { sequence ->
                            PracticeQuestionFactory.buildQuestion(
                                sequence,
                                findBestExplanations(sequence)
                            )
                        },
                    topic = assignment.subject?.course?.let(::PracticeTopic),
                    learners = learners.map(::PracticeLearner).toSet()
                )
            }

    fun isSequenceReadyToPractice(sequence: Sequence) =
        sequence.isStopped() && sequence.resultsArePublished

    fun isSubjectReadyToPractice(assignment: Assignment) =
        assignment.sequences.any(::isSequenceReadyToPractice)

    fun isAttachmentReadyToPractice(subjectId: Long, questionId: Long, attachmentId: Long): Boolean {
        // Get the sequence bound to the question
        val sequence = sequenceService.get(questionId, true)
        sequenceService.loadInteractions(sequence)

        return isSequenceReadyToPractice(sequence)
                && sequence.statement.attachment?.id == attachmentId
                && sequence.assignment?.id == subjectId
    }


    private fun findBestExplanations(sequence: Sequence) =
        responseService.find3BestRankedResponses(sequence)
            .toList()
            .filterNotNull()
            .map { response -> PracticeLearnerExplanation(response) }


}