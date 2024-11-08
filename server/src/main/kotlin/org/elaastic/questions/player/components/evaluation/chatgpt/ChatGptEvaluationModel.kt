package org.elaastic.questions.player.components.evaluation.chatgpt

import org.elaastic.ai.evaluation.chatgpt.ChatGptEvaluationStatus
import org.elaastic.moderation.ReportCandidate
import org.elaastic.moderation.ReportReason
import org.elaastic.moderation.UtilityGrade
import java.math.BigDecimal

data class ChatGptEvaluationModel(
    val evaluationId: Long? = null,
    val annotation: String? = null,
    val grade: BigDecimal? = null,
    val status: String? = ChatGptEvaluationStatus.UNKNOWN.name,
    val sequenceId: Long,
    val utilityGradeValues: Array<UtilityGrade> = UtilityGrade.values(),
    val reportValues: Array<ReportReason> = ReportReason.values(),
    var viewedByTeacher: Boolean = false,
    override var utilityGrade: UtilityGrade? = null,
    override var hiddenByTeacher: Boolean = false,
    override var removedByTeacher: Boolean = false,
    override var reportReasons: String? = null,
    override var reportComment: String? = null,
    val gradingID: Long? = null,
    val canHideGrading: Boolean = false,
    val responseId: Long? = null,
    override var teacherUtilityGrade: UtilityGrade? = null,
    ) : ReportCandidate {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatGptEvaluationModel

        return evaluationId == other.evaluationId
    }

    override fun hashCode(): Int {
        return evaluationId?.hashCode() ?: 0
    }

    /**
     * Check if the student has reported this evaluation
     *
     * @return true if the student has reported this evaluation, false
     *    otherwise
     */
    fun isReported() = reportReasons.isNullOrBlank().not()
}
