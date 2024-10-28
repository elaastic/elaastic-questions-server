package org.elaastic.moderation

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.AssignmentService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.questions.directory.User
import org.elaastic.questions.player.components.assignmentOverview.AssignmentOverviewModelFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
@RequestMapping("/report-manager")
class ReportManagerController(
    @Autowired val sequenceService: SequenceService,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val peerGradingRepository: PeerGradingRepository,
    @Autowired val messageSource: MessageSource,
    @Autowired val assignmentService: AssignmentService,
) {

    @GetMapping("/{idSequence}")
    fun getAllReport(
        authentication: Authentication,
        model: Model,
        @PathVariable idSequence: Long
    ): String {
        val user = authentication.principal as User

        val sequence = sequenceService.get(user, idSequence, true)

        val draxoReported = draxoPeerGradingService.findAllDraxoPeerGradingReportedNotHidden(sequence)
        val chatGPTReported = chatGptEvaluationService.findAllReportedNotHidden(sequence)

        val allReportedCandidateModel: List<ReportedCandidateModel> = (draxoReported + chatGPTReported)
            .mapNotNull { ReportedCandidateModelFactory.build(it) }

        val registeredUsers: Int = assignmentService.countAllRegisteredUsers(sequence.assignment!!)

        val assignmentOverviewModel = AssignmentOverviewModelFactory.buildOnSequence(
            true,
            sequence.assignment!!,
            nbRegisteredUser = registeredUsers,
            userActiveInteraction = sequence.activeInteraction,
            selectedSequence = sequence,
        )


        model["user"] = user
        model["allReportedCandidateModel"] = allReportedCandidateModel
        model["assignmentOverviewModel"] = assignmentOverviewModel

        return "moderation/report-manager"
    }

    @GetMapping("/detail/CHAT_GPT_EVALUATION/{idReportedCandidate}")
    @ResponseBody
    fun getChatGptEvaluationReportedCandidateDetail(
        authentication: Authentication,
        @PathVariable idReportedCandidate: Long
    ): ReportCandidateDetail {
        val user = authentication.principal as User
        return getReportedCandidateDetail(idReportedCandidate, ReportedCandidateType.CHAT_GPT_EVALUATION)
    }

    @GetMapping("/detail/PEER_GRADING/{idReportedCandidate}")
    @ResponseBody
    fun getPeerGradingReportedCandidateDetail(
        authentication: Authentication,
        @PathVariable idReportedCandidate: Long
    ): ReportCandidateDetail {
        val user = authentication.principal as User
        return getReportedCandidateDetail(idReportedCandidate, ReportedCandidateType.PEER_GRADING)
    }

    fun getReportedCandidateDetail(
        id: Long,
        type: ReportedCandidateType
    ): ReportCandidateDetail {
        val reportResaonToStringI18N = ReportReason.values().associateWith {
            messageSource.getMessage(
                "player.sequence.chatGptEvaluation.reportReason.$it",
                null,
                LocaleContextHolder.getLocale()
            )
        }
        val reportedCandidateDetail: ReportCandidateDetail = when (type) {
            ReportedCandidateType.PEER_GRADING -> {
                val peerGrading = peerGradingRepository.findById(id).orElseThrow()
                val nbReport = draxoPeerGradingService.countAllReportedNotHiddenForGrader(
                    peerGrading.response.interaction,
                    peerGrading.grader
                )

                ReportCandidateDetail(
                    id = peerGrading.id!!,
                    contentReported = peerGrading.annotation!!,
                    reportReasons = peerGrading.reportReasons,
                    reportComment = peerGrading.reportComment,
                    type = ReportedCandidateType.PEER_GRADING,
                    reporter = peerGrading.response.learner.getDisplayName(),
                    reporterId = peerGrading.response.learner.id!!,
                    graderThatHaveBeenReported = peerGrading.grader.getDisplayName(),
                    reportReasonToStringI18N = reportResaonToStringI18N,
                    numberOfReport = nbReport,
                )
            }

            ReportedCandidateType.CHAT_GPT_EVALUATION -> {
                val chatGptEvaluation = chatGptEvaluationRepository.findById(id).orElseThrow()
                val nbReport = chatGptEvaluationService.countAllReportedNotHidden(
                    chatGptEvaluation.response.interaction,
                )

                ReportCandidateDetail(
                    id = chatGptEvaluation.id!!,
                    contentReported = chatGptEvaluation.annotation!!,
                    reportReasons = chatGptEvaluation.reportReasons,
                    reportComment = chatGptEvaluation.reportComment,
                    type = ReportedCandidateType.CHAT_GPT_EVALUATION,
                    reporter = chatGptEvaluation.response.learner.getDisplayName(),
                    reporterId = chatGptEvaluation.response.learner.id!!,
                    graderThatHaveBeenReported = "ChatGPT", //TODO Introduce constant
                    reportReasonToStringI18N = reportResaonToStringI18N,
                    numberOfReport = nbReport,
                )
            }
        }
        return reportedCandidateDetail
    }

    class ReportCandidateDetail(
        id: Long,
        contentReported: String,
        reportReasons: String?,
        reportComment: String?,
        type: ReportedCandidateType,
        val reporter: String,
        val reporterId: Long,
        val graderThatHaveBeenReported: String,
        val numberOfReport: Int = 0,
        private val reportReasonToStringI18N: Map<ReportReason, String>,
    ) : ReportedCandidateModel(
        id,
        contentReported,
        reportReasons,
        reportComment,
        type
    ) {
        val reportReasonsI18N: List<String> = reasons.map { reportReasonToStringI18N[it]!! }
    }

    @GetMapping("/remove-report/{type}/{id}")
    @ResponseBody
    fun removeReport(
        authentication: Authentication,
        @PathVariable type: ReportedCandidateType,
        @PathVariable id: Long
    ): ResponseEntity<String> {
        val user = authentication.principal as User

        when (type) {
            ReportedCandidateType.PEER_GRADING -> {
                peerGradingService.removeReport(user, id)
            }
            ReportedCandidateType.CHAT_GPT_EVALUATION -> {
                chatGptEvaluationService.removeReport(user, id)
            }
        }
        return ResponseEntity.ok("Report removed")
    }
}