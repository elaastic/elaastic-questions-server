package org.elaastic.questions.assignment.sequence

import org.elaastic.questions.assignment.sequence.interaction.response.Response
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@EntityListeners(AuditingEntityListener::class)
class PeerGrading(
        var grade: Float?,
        var annotation: String?,

        @field:ManyToOne
        var grader: User,

        @field:ManyToOne
        var response: Response

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