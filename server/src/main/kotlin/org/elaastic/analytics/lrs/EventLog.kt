package org.elaastic.analytics.lrs

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.user.Role
import org.elaastic.user.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

/**
 * An action that happened on a sequence by a user.
 *
 * Every action possible is listed in the [Action] enum.
 *
 * @see Action
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class EventLog(

    @field:ManyToOne
        val sequence: Sequence,

    @field:ManyToOne(fetch = FetchType.LAZY)
        val user: User,

    /**
         * The role of the user at the time of the action.
         * @see Role
         */
        @field:Enumerated(EnumType.STRING)
        val role: Role.RoleId,

    /**
         * The action that was performed.
         * @see Action
         */
        @field:Enumerated(EnumType.STRING)
        val action: Action,

    @field:Enumerated(EnumType.STRING)
        @Column(name = "object")
        val obj: ObjectOfAction,

    /**
         * The user agent of the user at the time of the action.
         */
        @Column(name = "user_agent")
        val userAgent: String? = null,

    ) : AbstractJpaPersistable<Long>() {

    @Column(name = "date")
    @CreatedDate
    lateinit var date: Date

}