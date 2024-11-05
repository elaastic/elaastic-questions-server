package org.elaastic.analytics

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluation
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationRepository
import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationService
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.peergrading.PeerGrading
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingRepository
import org.elaastic.questions.assignment.sequence.peergrading.PeerGradingType
import org.elaastic.questions.assignment.sequence.report.ReportCandidate
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** Controller for stat of utility grade */
@RequestMapping("stat/utility-grade")
@RestController
class StatUtilityGradeController(
    @Autowired val chatGptEvaluationService: ChatGptEvaluationService,
    @Autowired val chatGptEvaluationRepository: ChatGptEvaluationRepository,
    @Autowired val peerGradingRepository: PeerGradingRepository,
) {

    data class UtilityGradeStat(
        val learner: UtilityGrade? = null,
        val teacher: UtilityGrade? = null,
        val evaluationType: EvaluationType,
        val evaluationId: Long,
    )

    fun getUtilityStat(
        evaluations: List<ReportCandidate>
    ): List<UtilityGradeStat> {
        return evaluations.map {
            UtilityGradeStat(
                learner = it.utilityGrade,
                teacher = if (it is ChatGptEvaluation) it.teacherUtilityGrade else null,
                evaluationType = when (it) {
                    is ChatGptEvaluation -> EvaluationType.CHAT_GPT
                    is PeerGrading -> EvaluationType.DRAXO
                    else -> throw IllegalArgumentException("Unknown evaluation type")
                },
                evaluationId = when (it) {
                    is ChatGptEvaluation -> it.id!!
                    is PeerGrading -> it.id!!
                    else -> throw IllegalArgumentException("Unknown evaluation type")
                }
            )
        }
    }

    enum class EvaluationType {
        CHAT_GPT,
        DRAXO
    }

    @GetMapping("all/", "")
    fun getAll(
        authentication: Authentication,
        @RequestParam("type") type: EvaluationType? = null,
        @RequestParam("noNull") noNull: Boolean = false,
    ): List<UtilityGradeStat> {
        val user = authentication.principal as User

        val allChatGptEvaluation = chatGptEvaluationRepository.findAll()
        val allDraxoEvaluation = peerGradingRepository.findAll().filter { it.type == PeerGradingType.DRAXO }

        var requestedEvaluation = when (type) {
            EvaluationType.CHAT_GPT -> allChatGptEvaluation
            EvaluationType.DRAXO -> allDraxoEvaluation
            null -> allChatGptEvaluation + allDraxoEvaluation
        }

        requestedEvaluation = if (noNull) {
            requestedEvaluation.filter { it.utilityGrade != null || (it is ChatGptEvaluation && it.teacherUtilityGrade != null) }
        } else {
            requestedEvaluation
        }

        return getUtilityStat(requestedEvaluation)
    }

    private fun getNoNullEvaluations(): Pair<List<ChatGptEvaluation>, List<PeerGrading>> {
        val allChatGptEvaluation =
            chatGptEvaluationRepository.findAll().filter { it.utilityGrade != null || it.teacherUtilityGrade != null }
        val allDraxoEvaluation =
            peerGradingRepository.findAll().filter { it.type == PeerGradingType.DRAXO && it.utilityGrade != null }
        return Pair(allChatGptEvaluation, allDraxoEvaluation)
    }

    /** Stat for mean utility grade */
    data class MeanUtilityGradeStat(
        val meanGradeOfLearner: Double,
        val meanGradeOfTeacher: Double,
        val evaluationType: EvaluationType,
    ) {
        val explain = "Each UtilityGrade is between 1 and 4. " +
                UtilityGrade.values().joinToString(", ") {
                    "${it.name} = ${getValueOfGrade(it)}"
                }

        companion object {
            fun getValueOfGrade(grade: UtilityGrade): Int {
                return when (grade) {
                    UtilityGrade.STRONGLY_DISAGREE -> 1
                    UtilityGrade.DISAGREE -> 2
                    UtilityGrade.AGREE -> 3
                    UtilityGrade.STRONGLY_AGREE -> 4
                }
            }
        }
    }

    @GetMapping("mean", "/mean")
    fun getMean(
        authentication: Authentication,
        @RequestParam("type") type: EvaluationType? = null,
    ): List<MeanUtilityGradeStat> {
        val user = authentication.principal as User

        var (allChatGptEvaluation, allDraxoEvaluation) = getNoNullEvaluations()

        when (type) {
            EvaluationType.CHAT_GPT -> allDraxoEvaluation = emptyList()
            EvaluationType.DRAXO -> allChatGptEvaluation = emptyList()
            null -> {}
        }

        val meanGradeOfLearnerForChatGPT = allChatGptEvaluation
            .mapNotNull { it.utilityGrade }
            .map { MeanUtilityGradeStat.getValueOfGrade(it) }
            .average()

        val meanGradeOfTeacherForChatGPT = allChatGptEvaluation
            .mapNotNull { it.teacherUtilityGrade }
            .map { MeanUtilityGradeStat.getValueOfGrade(it) }
            .average()

        val meanGradeOfLearnerForDraxo = allDraxoEvaluation
            .mapNotNull { it.utilityGrade }
            .map { MeanUtilityGradeStat.getValueOfGrade(it) }
            .average()

        val meanGradeOfTeacherForDraxo = -1.0

        return listOf(
            MeanUtilityGradeStat(meanGradeOfLearnerForChatGPT, meanGradeOfTeacherForChatGPT, EvaluationType.CHAT_GPT),
            MeanUtilityGradeStat(meanGradeOfLearnerForDraxo, meanGradeOfTeacherForDraxo, EvaluationType.DRAXO)
        )
    }
}