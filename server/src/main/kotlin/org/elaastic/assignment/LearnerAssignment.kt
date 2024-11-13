package org.elaastic.assignment

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.user.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

/**
 * This class represents the association between a learner and an assignment.
 * It is used to store the assignments that a learner can do.
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
class LearnerAssignment(

    @field:ManyToOne(fetch = FetchType.EAGER)
        var learner: User,

    @field:ManyToOne(fetch = FetchType.EAGER)
        var assignment: Assignment
) : AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

}