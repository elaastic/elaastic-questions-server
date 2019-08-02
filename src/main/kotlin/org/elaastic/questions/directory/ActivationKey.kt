package org.elaastic.questions.directory

import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@EntityListeners(AuditingEntityListener::class)
class ActivationKey(
        @field:NotNull @field:OneToOne var user: User,
        @field:NotBlank var activationKey: String,
        @field:NotNull var activationEmailSent: Boolean = false,
        @field:NotNull val subscriptionSource: String = "elaastic"
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @NotNull
    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date
}
