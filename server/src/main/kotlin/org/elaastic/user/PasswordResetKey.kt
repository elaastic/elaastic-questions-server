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

package org.elaastic.user

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
class PasswordResetKey(
    @field:NotBlank var passwordResetKey: String,
    @field:NotNull @field:OneToOne var user: User,
    @field:NotNull var passwordResetEmailSent: Boolean = false,
    @field:NotNull val subscriptionSource: String = "elaastic"
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null


    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date
}

