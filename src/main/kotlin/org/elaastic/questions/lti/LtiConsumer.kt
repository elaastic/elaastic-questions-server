package org.elaastic.questions.lti

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Entity
@EntityListeners(AuditingEntityListener::class)
class LtiConsumer(
        @field:NotBlank
        @field:Size(max = 45)
        @field:Column(name = "name")
        var consumerName: String,

        @field:NotBlank
        @field:Size(max = 32)
        var secret: String

)  {

    @field:Id
    @field:NotBlank
    @field:Size(max = 255)
    @Column(name = "consumer_key")
    lateinit var id: String


    var ltiVersion: String? = "1.0"

    @Column(name = "consumer_name")
    var productName: String? = null

    @Column(name = "consumer_version")
    var productVersion: String? = null

    @Column(name = "consumer_guid")
    var productGuid: String? = null

    var cssPath: String? = null

    @Column(name = "protected")
    var isProtected: Int? = 0

    @Column(name = "enabled")
    var isEnabled: Int? = 1

    var enableFrom: Date? = null
    var enableUntil: Date? = null
    var lastAccess: Date? = null

    @NotNull
    @CreatedDate
    @Column(name = "created")
    var dateCreated: Date? = null

    @NotNull
    @LastModifiedDate
    @Column(name = "updated")
    var lastUpdated: Date? = null

    override fun toString(): String {
        return "LtiConsumer(consumerName='$consumerName', secret='$secret', id=$id, ltiVersion=$ltiVersion, productName=$productName, productVersion=$productVersion, productGuid=$productGuid, cssPath=$cssPath, isProtected=$isProtected, isEnabled=$isEnabled, enableFrom=$enableFrom, enableUntil=$enableUntil, lastAccess=$lastAccess, dateCreated=$dateCreated, lastUpdated=$lastUpdated)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LtiConsumer) return false

        if (id != other.id) return false
        if (consumerName != other.consumerName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = consumerName.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }


}
