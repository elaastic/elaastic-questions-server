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

package org.elaastic.questions.assignment.sequence.interaction.response

import org.elaastic.questions.assignment.choice.legacy.ChoiceListSpecification
import org.elaastic.questions.assignment.choice.legacy.ChoiceListSpecificationConverter
import org.elaastic.questions.assignment.sequence.interaction.Interaction
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "choice_interaction_response")
@EntityListeners(AuditingEntityListener::class)
class Response(

        @field:ManyToOne
        var learner: User,

        @field:ManyToOne
        var interaction: Interaction,

        var attempt: Int = 1,

        var explanation: String? = null,

        var confidenceDegree: Int? = null,

        var meanGrade: Float? = null,

        @field:Convert(converter = ChoiceListSpecificationConverter::class)
        var choiceListSpecification: ChoiceListSpecification? = null,

        var score: Float? = null


) : AbstractJpaPersistable<Long>() {
    @Version
    var version: Long? = null

    @NotNull
    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @NotNull
    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null
}