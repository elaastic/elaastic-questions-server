package org.elaastic.questions.course

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.statement.Statement
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@NamedEntityGraph(
        name = "Course.subjects",
        attributeNodes = [
            NamedAttributeNode(
                    value = "subjects"
            )]
)
@EntityListeners(AuditingEntityListener::class)
class Course (
        @field:NotNull
        @field:NotBlank
        var title: String,

        @field:ManyToOne(fetch = FetchType.LAZY)
        var owner: User,

        @field:NotNull
        @field:NotBlank
        var globalId: String = UUID.randomUUID().toString()

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
    @OrderBy("rank ASC")
    var subjects: MutableList<Subject> = mutableListOf()

    /*
    fun addSubject(subject: Subject): Subject {
        require(subject.owner == owner) {
            "The owner of the subject cannot be different from the owner of course"
        }

        subjects.add(subject);
        subject.course = this.title;
        subject.owner = owner

        return subject
    }*/

}