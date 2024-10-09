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

package org.elaastic.questions.lti

import org.elaastic.questions.assignment.Assignment
import org.elaastic.common.persistence.AbstractJpaPersistable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne

@Entity
class LmsAssignment(

        @field:OneToOne
        @JoinColumn(name = "lti_consumer_key")
        val lms: LtiConsumer,

        @field:Column(name = "lti_activity_id")
        val lmsActivityId: String,

        @field:Column(name = "lti_course_id")
        val lmsCourseId: String,

        @field:Column(name = "lti_course_title")
        val lmsCourseTitle: String,

        @field:OneToOne
        val assignment: Assignment

        ) : AbstractJpaPersistable<Long>() {

    var source:String? = null

}
