package org.elaastic.consolidation.subject

import org.elaastic.consolidation.subject.question.PracticeLearnerExplanation
import org.elaastic.consolidation.subject.question.PracticeQuestionFactory
import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.RevisionMode
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

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
     * Get a practice subject from its uuid
     * @param uuid of the practice subject that directly matched the corresponding assignment id
     */
    fun getPracticeSubject(uuid: UUID) =
        assignmentService.findByUuid(uuid, true)
            .let { assignment ->
                assignment.sequences.map(sequenceService::loadInteractions)

                val sequences = assignment.sequences.filter(::isSequenceReadyToPractice)

                check(sequences.isNotEmpty()) { "The subject $uuid is not ready to practice" }

                System.out.println("sequences: $sequences")

                val learners = assignmentService.findAllLearnersRegisteredOnWithCasUser(assignment)

                PracticeSubject(
                    assignment =  assignment,
                    questions = sequences
                        .map { sequence ->
                            PracticeQuestionFactory.buildQuestion(
                                sequence,
                                if (assignment.revisionMode.equals(RevisionMode.Immediately))
                                   emptyList()
                                else findBestExplanations(sequence)
                            )
                        },
                    topic = assignment.subject?.course?.let(::PracticeTopic),
                    learners = learners.map(::PracticeLearner).toSet()
                )
            }

    fun isSequenceReadyToPractice(sequence: Sequence) =
        sequence.assignment?.revisionMode == RevisionMode.Immediately ||
            sequence.assignment?.revisionMode == RevisionMode.AfterTeachings
            && sequence.resultsArePublished
            && (sequence.executionIsFaceToFace() || sequence.isStopped())

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
            .toList()
            .filterNotNull()
            .map { response -> PracticeLearnerExplanation(response) }


}