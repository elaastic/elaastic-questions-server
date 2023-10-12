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

package org.elaastic.questions.assignment.sequence.peergrading

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*
import javax.persistence.*


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "`type`", length = 12)
@EntityListeners(AuditingEntityListener::class)
class PeerGrading(
    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", insertable = false, updatable = false)
    var type: PeerGradingType = PeerGradingType.LIKERT,

    @field:ManyToOne
    var grader: User,

    @field:ManyToOne
    var response: Response,

    var grade: BigDecimal?,
    var annotation: String? = null,

    // Indicates that this grading is the last one for this grader & this sequence (useful in ONE-BY-ONE & DRAXO strategies)
    var lastSequencePeerGrading: Boolean = true,
    ) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null
}
