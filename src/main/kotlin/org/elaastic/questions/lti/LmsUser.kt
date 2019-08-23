package org.elaastic.questions.lti

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
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
