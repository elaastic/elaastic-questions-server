package org.elaastic.consolidation.subject

import org.elaastic.activity.response.ResponseService
import org.elaastic.assignment.Assignment
import org.elaastic.assignment.AssignmentService
import org.elaastic.assignment.ReadyForConsolidation
import org.elaastic.consolidation.subject.question.PracticeLearnerExplanation
import org.elaastic.consolidation.subject.question.PracticeQuestionFactory
import org.elaastic.sequence.Sequence
import org.elaastic.sequence.SequenceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

/**
 * Service for manipulating practice subjects
 *
 * A practice subject can be built from an assignment that is ready to practice (meaning it has terminated sequences for
 * which results have been published)
 *
 * @author John Tranier
 */
@Service
class PracticeSubjectService(
    @Autowired val assignmentService: AssignmentService,
    @Autowired val sequenceService: SequenceService,
    @Autowired val responseService: ResponseService,
) {

    /** Find all the PracticeSubject that have been published or updated after the "since" parameter */
    fun findAllPracticeSubject(since: LocalDateTime) =
        assignmentService.findAllAssignmentUpdatedSince(since)
            .filter(::isSubjectReadyToPractice)
            .map(::SummaryPracticeSubject)

    /**
     * Get a practice subject from its uuid
     *
     * @param uuid of the practice subject that directly matched the corresponding assignment id
     */
    fun getPracticeSubject(uuid: UUID) =
        assignmentService.findByUuid(uuid, true)
            .let { assignment ->
                val sequences = assignment.sequences
                    .map(sequenceService::loadInteractions)
                    .filter(::isSequenceReadyToPractice)

                check(sequences.isNotEmpty()) { "The subject $uuid is not ready to practice" }

                val learners = assignmentService.findAllLearnersRegisteredOnWithCasUser(assignment)

                PracticeSubject(
                    assignment = assignment,
                    questions = sequences
                        .map { sequence ->
                            PracticeQuestionFactory.buildQuestion(
                                sequence,
                                if (assignment.readyForConsolidation != ReadyForConsolidation.Immediately) findBestExplanations(
                                    sequence
                                )
                                else emptyList()
                            )
                        },
                    topic = assignment.subject?.course?.let(::PracticeTopic),
                    learners = learners.map(::PracticeLearner).toSet()
                )
            }

    /**
     * Check if a sequence is ready to practice
     *
     * A sequence is ready to practice if:
     * - it is ready for consolidation immediately
     * - it is ready for consolidation after teachings, and the results are published, and the sequence is stopped
     *
     * @param sequence the sequence to check
     */
    fun isSequenceReadyToPractice(sequence: Sequence) =
        sequence.assignment?.readyForConsolidation == ReadyForConsolidation.Immediately
                ||
        (sequence.assignment?.readyForConsolidation == ReadyForConsolidation.AfterTeachings
        && sequence.resultsArePublished
        && (sequence.executionIsFaceToFace() || sequence.isStopped()))

    /**
     * Check if an assignment is ready to practice
     *
     * An assignment is ready to practice if at least one of its sequences is ready to practice
     *
     * @param assignment the assignment to check
     * @see isSequenceReadyToPractice
     */
    fun isSubjectReadyToPractice(assignment: Assignment) =
        assignment.sequences.any(::isSequenceReadyToPractice)

    fun isAttachmentReadyToPractice(subjectUuid: UUID, questionUuid: UUID, attachmentUuid: UUID): Boolean {
        // dirty fix to allow access to attachment
        return true

        // Get the sequence bound to the question
        //val sequence = sequenceService.findByUuid(questionUuid, true)
        //sequenceService.loadInteractions(sequence)

        //return isSequenceReadyToPractice(sequence)
        //        && sequence.statement.attachment?.uuid == attachmentUuid
        //        && sequence.assignment?.globalId == subjectUuid
    }


    private fun findBestExplanations(sequence: Sequence) =
        responseService.findRecommendedByTeacherResponses(sequence)
            .map { response -> PracticeLearnerExplanation(response) }


}