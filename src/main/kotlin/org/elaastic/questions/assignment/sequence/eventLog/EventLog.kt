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

package org.elaastic.questions.assignment.sequence.eventLog

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.Role
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*


@Entity
@EntityListeners(AuditingEntityListener::class)
class EventLog(

        @field:ManyToOne
        var sequence: Sequence,

        @field:ManyToOne(fetch = FetchType.LAZY)
        var user: User,

        @field:Enumerated(EnumType.STRING)
        var role: Role.RoleId,

        @Column(name = "action_type")
        @field:Enumerated(EnumType.STRING)
        var action: Action,

        @field:Enumerated(EnumType.STRING)
        @Column(name = "object")
        var obj: ObjectOfAction

) : AbstractJpaPersistable<Long>() {

    @Column(name = "date")
    @CreatedDate
    lateinit var date: Date

}

