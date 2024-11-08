package org.elaastic.material.instructional.subject

import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.user.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@Entity
@EntityListeners(AuditingEntityListener::class)
class SharedSubject(

    @field:ManyToOne
        var teacher: User,

    @field:ManyToOne(fetch = FetchType.EAGER)
        var subject: Subject
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