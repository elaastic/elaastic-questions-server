package org.elaastic.questions.assignment.sequence.interaction.feedback

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*

@Entity
@Table(name="sequence_feedback_teacher")
@EntityListeners(AuditingEntityListener::class)
class TeacherFeedback (

    @field:ManyToOne
    var teacher: User,

    @field:ManyToOne
    var sequence: Sequence,

    var recommendRating: Int,

    var reuseRating: Int,

    var explanation: String?

): AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name="date_created")
    @CreatedDate
    lateinit var date: Date
}