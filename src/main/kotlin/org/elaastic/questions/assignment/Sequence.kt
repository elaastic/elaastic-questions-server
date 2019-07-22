package org.elaastic.questions.assignment

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * @author John Tranier
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class Sequence(
        var rank: Int,

        @field:ManyToOne
        var owner: User,

        @field:OneToOne
        var assignment: Assignment,

        @field:OneToOne
        var statement: Statement,

        @field:Enumerated(EnumType.STRING)
        var executionContext: ExecutionContext = ExecutionContext.FaceToFace

) :  AbstractJpaPersistable<Long>() {

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

    // TODO Interaction

    @Enumerated(EnumType.STRING)
    var state: State = State.beforeStart

    var resultsArePublished: Boolean = false

    // TODO Methods...
}

enum class State {
    beforeStart,
    show,
    afterStop
}

