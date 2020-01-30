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
package org.elaastic.questions.assignment.sequence.interaction.feedback

import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.*

@Entity
@Table(name = "sequence_feedback")
@EntityListeners(AuditingEntityListener::class)
class Feedback (

        @field:ManyToOne
        var learner: User,

        @field:ManyToOne
        var interaction: Interaction,

        @field:Column(name = "agreement_level")
        var agreementLevel: Int,

        @field:Column(name = "agreement_explanation")
        var agreementExplanation: String

) : AbstractJpaPersistable<Long>() {

        @Version
        var version: Long? = null

        @Column(name = "date created")
        lateinit var dateCreated: Date

        @LastModifiedDate
        @Column(name ="last_updated")
        var lastUpdated: Date? = null

    // TODO In process, I think it's ready but ...
}
