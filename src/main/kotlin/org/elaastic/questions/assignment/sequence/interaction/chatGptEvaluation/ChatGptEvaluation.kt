package org.elaastic.questions.assignment.sequence.interaction.chatGptEvaluation

import org.elaastic.questions.assignment.sequence.report.ReportCandidate
import org.elaastic.questions.assignment.sequence.UtilityGrade
import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class ChatGptEvaluation (
    var grade: BigDecimal? = null,
    var annotation: String? = null,
    var status: String = ChatGptEvaluationStatus.UNKNOWN.name,

    override var reportReasons: String? = null,
    override var reportComment: String? = null,
    override var utilityGrade: UtilityGrade? = null,

    override var hiddenByTeacher: Boolean = false,
    override var removedByTeacher: Boolean = false,

    @field:OneToOne
    var response: Response

) : AbstractJpaPersistable<Long>(), ReportCandidate {

    @Version
    var version: Long? = null

    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    var lastUpdated: Date? = null

}