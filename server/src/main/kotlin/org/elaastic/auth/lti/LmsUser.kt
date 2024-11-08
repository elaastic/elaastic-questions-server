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

package org.elaastic.auth.lti

import org.elaastic.user.User
import org.elaastic.common.persistence.AbstractJpaPersistable
import javax.persistence.*

@Entity
class LmsUser(

    @field:Column(name = "lti_user_id")
        val lmsUserId: String,

    @field:ManyToOne
        @JoinColumn(name = "lti_consumer_key")
        val lms: LtiConsumer,

    @field:OneToOne
        @JoinColumn(name = "tsaap_user_id")
        val user: User

): AbstractJpaPersistable<Long>() {
}
