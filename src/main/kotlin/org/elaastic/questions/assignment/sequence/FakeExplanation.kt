package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.Statement
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
class FakeExplanation(
        var correspondingItem: Int?=null,

        @field:NotBlank
        var content: String,

        @field:ManyToOne
        var author: User,

        @field:ManyToOne
        var statement: Statement
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @NotNull
    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @NotNull
    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null
}