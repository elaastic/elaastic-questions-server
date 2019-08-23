package org.elaastic.questions.lti

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.persistence.AbstractJpaPersistable
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

        @field:OneToOne
        val assignment: Assignment

        ) : AbstractJpaPersistable<Long>() {

    var source:String? = null

}
