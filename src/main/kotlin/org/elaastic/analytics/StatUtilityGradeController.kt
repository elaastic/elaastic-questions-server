package org.elaastic.analytics

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluation
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

/** Controller for stat of utility grade */
@Controller
@RequestMapping("stat/utility-grade")
class StatUtilityGradeController(
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val peerGradingRepository: PeerGradingRepository,
) {

    @GetMapping("all", "")
    @ResponseBody
    fun getAll(
        authentication: Authentication,
    ): List<UtilityGradeStat> {
        val user = authentication.principal as User

        val allChatGptEvaluation = chatGptEvaluationRepository.findAll()
        val allDraxoEvaluation = peerGradingRepository.findAll().filter { it.type == PeerGradingType.DRAXO }

        return getUtilityStat(allDraxoEvaluation, allChatGptEvaluation)
    }

    @GetMapping("all/non-null", "/non-null")
    @ResponseBody
    fun getAllNonNull(
        authentication: Authentication,
    ): List<UtilityGradeStat> {
        val user = authentication.principal as User

        val allChatGptEvaluation =
            chatGptEvaluationRepository.findAll().filter { it.utilityGrade != null || it.teacherUtilityGrade != null }
        val allDraxoEvaluation =
            peerGradingRepository.findAll().filter { it.type == PeerGradingType.DRAXO && it.utilityGrade != null }

        return getUtilityStat(allDraxoEvaluation, allChatGptEvaluation)
    }

    private fun getUtilityStat(
        allDraxoEvaluation: List<PeerGrading>,
        allChatGptEvaluation: List<ChatGptEvaluation>
    ): List<UtilityGradeStat> {
        val draxoUtilityGrade: List<UtilityGradeStat> = allDraxoEvaluation.map {
            UtilityGradeStat(
                learner = it.utilityGrade,
                teacher = null,
                evaluationType = EvaluationType.DRAXO,
                evaluationId = it.id!!
            )
        }

        val chatGptUtilityGrade: List<UtilityGradeStat> = allChatGptEvaluation.map {
            UtilityGradeStat(
                learner = it.utilityGrade,
                teacher = it.teacherUtilityGrade,
                evaluationType = EvaluationType.CHAT_GPT,
                evaluationId = it.id!!
            )
        }

        return draxoUtilityGrade + chatGptUtilityGrade
    }

    data class UtilityGradeStat(
        val learner: UtilityGrade? = null,
        val teacher: UtilityGrade? = null,
        val evaluationType: EvaluationType,
        val evaluationId: Long,
    )

    enum class EvaluationType {
        CHAT_GPT,
        DRAXO
    }
}