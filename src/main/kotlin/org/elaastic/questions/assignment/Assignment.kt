package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

/**
 * @author John Tranier
 */
@Entity
@NamedEntityGraph(
        name = "Assignment.sequences",
        attributeNodes = [
            NamedAttributeNode(
                    value = "sequences",
                    subgraph = "Sequence.statement"
            )
        ],
        subgraphs = [
            NamedSubgraph(
                    name = "Sequence.statement",
                    attributeNodes = [NamedAttributeNode("statement")]
            )
        ]
)
@EntityListeners(AuditingEntityListener::class)
class Assignment(
        @field:NotBlank
        var title: String,

        @field:NotNull
        @field:ManyToOne(fetch = FetchType.LAZY)
        var owner: User,

        @field:NotNull
        @field:NotBlank
        var globalId: String
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

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "assignment",
            targetEntity = Sequence::class)
    var sequences: List<Sequence> = listOf()

    // TODO List<Sequence> getSequences()

    // TODO Integer countSequences()

    // TODO Sequence getLastSequence()

    // TODO Integer registeredUserCount()
}