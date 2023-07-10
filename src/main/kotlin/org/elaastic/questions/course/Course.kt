package org.elaastic.questions.course

import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.subject.Subject

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


@Entity
@NamedEntityGraph(
        name = "Course_subjects",
        attributeNodes = [
            NamedAttributeNode(
                    value = "subjects"
            )
        ]
)
@EntityListeners(AuditingEntityListener::class)

class Course (

        @field:NotNull
        @field:NotBlank
        var title: String,

        @field:ManyToOne(fetch = FetchType.LAZY) // TODO Lazy ?
        var owner: User

): AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "course",
            targetEntity = Subject::class)
    @OrderBy("lastUpdated DESC")
    var subjects: MutableSet<Subject> = mutableSetOf()


    @field:NotNull
    @field:NotBlank
    @Column(name="`uuid`")
    var globalId:String = UUID.randomUUID().toString()

    fun updateFrom(otherCourse: Course) {
        require(id == otherCourse.id)
        if (this.version != otherCourse.version) {
            throw OptimisticLockException()
        }

        this.title = otherCourse.title
    }
}