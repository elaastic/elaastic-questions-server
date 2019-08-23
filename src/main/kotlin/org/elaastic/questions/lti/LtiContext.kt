package org.elaastic.questions.lti

import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class LtiContext(

        @field:EmbeddedId
        val ltiContextId: LtiContextId,

        @field:NotNull
        @field:Column(name = "context_id", insertable = false, updatable = false)
        val lmsActivityId: String,

        @field:NotNull
        val title: String,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(name = "consumer_key", insertable = false, updatable = false)
        val lms: LtiConsumer,

        @field:NotNull
        @field:Column(name = "lti_context_id")
        val lmsCourseId: String,

        @field:Column(name = "lti_resource_id")
        val lmsResourceId: String? = null,

        @field:Column(name = "settings")
        val settings: String? = null

) {

    @NotNull
    @CreatedDate
    @Column(name = "created")
    var dateCreated: Date? = null

    @javax.validation.constraints.NotNull
    @LastModifiedDate
    @Column(name = "updated")
    var lastUpdated: Date? = null

    @Embeddable
    data class LtiContextId(
            @field:Column(name = "consumer_key")
            val lmsKey: String,
            @field:Column(name = "context_id")
            val lmsActivityId: String
    ) : Serializable

}
