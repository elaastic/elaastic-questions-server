package org.elaastic.questions.moderation

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.questions.assignment.sequence.report.ReportCandidateService
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
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
    @Autowired val reportCandidateService: ReportCandidateService,
    @Autowired val peerGradingService: PeerGradingService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val peerGradingRepository: PeerGradingRepository,
) {

    private val reportedCandidateModelFactory: ReportedCandidateModelFactory = ReportedCandidateModelFactory

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
            .mapNotNull { reportedCandidateModelFactory.build(it) }

        model["user"] = user
        model["allReportedCandidateModel"] = allReportedCandidateModel

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
        val reportedCandidateDetail: ReportCandidateDetail = when (type) {
            ReportedCandidateType.PEER_GRADING -> {
                val peerGrading = peerGradingRepository.findById(id).orElseThrow()
                ReportCandidateDetail(
                    id = peerGrading.id!!,
                    contentReported = peerGrading.annotation!!,
                    reportReasons = peerGrading.reportReasons,
                    reportComment = peerGrading.reportComment,
                    type = ReportedCandidateType.PEER_GRADING,
                    reporter = peerGrading.response.learner.getDisplayName(),
                    graderThatHaveBeenReported = peerGrading.grader.getDisplayName(),
                )
            }

            ReportedCandidateType.CHAT_GPT_EVALUATION -> {
                val chatGptEvaluation = chatGptEvaluationRepository.findById(id).orElseThrow()
                ReportCandidateDetail(
                    id = chatGptEvaluation.id!!,
                    contentReported = chatGptEvaluation.annotation!!,
                    reportReasons = chatGptEvaluation.reportReasons,
                    reportComment = chatGptEvaluation.reportComment,
                    type = ReportedCandidateType.CHAT_GPT_EVALUATION,
                    reporter = chatGptEvaluation.response.learner.getDisplayName(),
                    graderThatHaveBeenReported = "ChatGPT", //TODO Introduce constant
                )
            }
        }
        return reportedCandidateDetail
    }

    class ReportCandidateDetail(
        id: Long,
        contentReported: String,
        reportReasons: String?, //TODO Get the list of reasons from i18n
        reportComment: String?,
        type: ReportedCandidateType,
        val reporter: String,
        val graderThatHaveBeenReported: String,
        val numberOfReport: Int = 0,
    ) : ReportedCandidateModel(
        id,
        contentReported,
        reportReasons,
        reportComment,
        type
    )
}