package org.elaastic.questions.lti

import org.jetbrains.annotations.NotNull
import java.io.Serializable
import javax.persistence.*

@Entity
class LtiUser(

        @field:EmbeddedId
        val ltiUserId: LtiUserId,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(name = "consumer_key", insertable = false, updatable = false, referencedColumnName = "consumer_key")
        val lms: LtiConsumer,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumns(
                JoinColumn(name = "consumer_key", insertable = false, updatable = false, referencedColumnName = "consumer_key"),
                JoinColumn(name = "context_id", insertable = false, updatable = false, referencedColumnName = "context_id")
        )
        val lmsActivity: LtiContext,

        @field:NotNull
        @field:Column(name = "user_id", insertable = false, updatable = false)
        val lmsUserId: String,

        @field:NotNull
        @field:Column(name = "lti_result_sourcedid")
        val resultSourcedId: String = "default"

) {

    @Embeddable
    data class LtiUserId(
            @field:Column(name = "consumer_key")
            val lmsKey: String,
            @field:Column(name = "context_id")
            val lmsActivityId: String,
            @field:Column(name = "user_id")
            val lmsUserId: String
    ) : Serializable

}
