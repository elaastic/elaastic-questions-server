package org.elaastic.questions.assignment.sequence.peergrading.draxo

import org.elaastic.questions.assignment.LearnerAssignmentService
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseRepository
import org.elaastic.questions.assignment.sequence.interaction.response.ResponseService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DraxoPeerGradingService(
    @Autowired val draxoPeerGradingRepository: DraxoPeerGradingRepository,
    @Autowired val learnerAssignmentService: LearnerAssignmentService,
    @Autowired val responseService: ResponseService,
    @Autowired val responseRepository: ResponseRepository,
) {

    fun createOrUpdateDraxo(
        grader: User,
        response: Response,
        evaluation: DraxoEvaluation,
        lastSequencePeerGrading: Boolean
    ): DraxoPeerGrading {
        require(learnerAssignmentService.isGraderRegisteredOnAssignment(grader, response)) {
            "You must be registered on the assignment to provide evaluations"
        }

        val peerGrade = draxoPeerGradingRepository.findByGraderAndResponse(grader, response)
            ?: DraxoPeerGrading(grader, response, evaluation, lastSequencePeerGrading)

        require(peerGrade is DraxoPeerGrading) {
            "It already exist a peer grading for this response & this grader but it is not a DRAXO evaluation"
        }

        peerGrade.updateFrom(evaluation)

        val savedPeerGrade = draxoPeerGradingRepository.save(peerGrade)
        responseService.updateMeanGradeAndEvaluationCount(response)

        return savedPeerGrade
    }

    fun findAllDraxo(responses: List<Response>): List<DraxoPeerGrading> =
        draxoPeerGradingRepository.findAllByResponseIn(responses)

    fun findAllDraxo(sequence: Sequence): List<DraxoPeerGrading> =
        findAllDraxo(
            responseRepository.findAllByInteraction(sequence.getResponseSubmissionInteraction())
        )

    fun findAllDraxo(response: Response): List<DraxoPeerGrading> =
        draxoPeerGradingRepository.findAllByResponseAndType(response, PeerGradingType.DRAXO)

    /**
     * Find a Draxo peer grading by its id.
     *
     * @param id the id of the peer grading.
     * @return the Draxo peer grading.
     * @throws IllegalArgumentException if no Draxo peer grading is found with
     *    the given id.
     */
    fun getDraxoPeerGrading(id: Long): DraxoPeerGrading =
        draxoPeerGradingRepository.findByIdAndType(id, PeerGradingType.DRAXO)
            ?: error("No Draxo peer grading found with id $id")

    /**
     * Return the list of all the DRAXO peer grading that have been reported
     * and are not hidden by the teacher.
     *
     * @param sequence the sequence
     * @return the list of DRAXO peer grading
     */
    fun findAllDraxoPeerGradingReportedNotHidden(sequence: Sequence): List<DraxoPeerGrading> =
        findAllDraxo(sequence)
            .filter { it.reportReasons?.isNotEmpty() == true }
            .filter { !it.hiddenByTeacher }

    /**
     * @param sequence the sequence
     * @return the number of DRAXO peer grading that have been reported and are
     *    not hidden by the teacher
     */
    fun countAllDraxoPeerGradingReportedNotHidden(sequence: Sequence): Int =
        draxoPeerGradingRepository.countAllByHiddenByTeacherIsFalseAndReportReasonsIsNotEmptyAndResponseIn(
            responseRepository.findAllByInteraction(sequence.getResponseSubmissionInteraction())
        )
}