/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

/**
 * This class represents the evaluation of a ChatGPT response.
 *
 * A ChatGptEvaluation is associated with a [Response].
 *
 * By implementing the [ReportCandidate] interface, a ChatGptEvaluation can
 * be reported by a student and can be moderated by a teacher.
 *
 * @see Response
 * @see ReportCandidate
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class ChatGptEvaluation(
    var grade: BigDecimal? = null,

    /** The comment of the evaluation given to the evaluated response. */
    var annotation: String? = null,

    /**
     * The status of the evaluation.
     *
     * @see ChatGptEvaluationStatus
     */
    var status: String = ChatGptEvaluationStatus.UNKNOWN.name,

    override var reportReasons: String? = null,
    override var reportComment: String? = null,
    override var utilityGrade: UtilityGrade? = null,

    override var hiddenByTeacher: Boolean = false,
    override var removedByTeacher: Boolean = false,

    /**
     * The response evaluated.
     *
     * @see Response
     */
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