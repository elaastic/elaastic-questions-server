package org.elaastic.questions.moderation

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.SequenceService
import org.elaastic.questions.assignment.sequence.peergrading.draxo.DraxoPeerGradingService
import org.elaastic.questions.directory.User
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/report-manager")
class ReportManagerController(
    @Autowired val sequenceService: SequenceService,
    @Autowired val draxoPeerGradingService: DraxoPeerGradingService,
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
) {

    private val reportedCandidateModelFactory: ReportedCandidateModelFactory = ReportedCandidateModelFactory

    private val LOGGER = LoggerFactory.getLogger(ReportManagerController::class.java)

    @GetMapping("/{idSequence}")
    fun getAllReport(
        authentication: Authentication, model: Model, @PathVariable idSequence: Long
    ): String {
        val user = authentication.principal as User

        val sequence = sequenceService.get(user, idSequence, true)

        val draxoReported = draxoPeerGradingService.findAllDraxoPeerGradingReportedNotHidden(sequence)
        val chatGPTReported = chatGptEvaluationService.findAllReportedNotHidden(sequence)

        val allReportedCandidateModel: List<ReportedCandidateModel> = (draxoReported + chatGPTReported)
            .mapNotNull { reportedCandidateModelFactory.build(it) }

        model["user"] = user
        model["allReportedCandidateModel"] = allReportedCandidateModel

        LOGGER.info("User ${user.id} is viewing the report manager for sequence $idSequence")
        LOGGER.info("Found ${allReportedCandidateModel.size} reported candidates")
        allReportedCandidateModel.forEach {
            LOGGER.info("Reported candidate ${it.id} with content \"${it.contentReported}\"")
            LOGGER.info("$it")
        }

        return "moderation/report-manager"
    }
}