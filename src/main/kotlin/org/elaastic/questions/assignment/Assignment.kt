/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.questions.assignment

import org.elaastic.questions.assignment.sequence.Sequence
import org.elaastic.questions.directory.User
import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.statement.Statement
import org.hibernate.annotations.SortNatural
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.collections.ArrayList

/**
 * An assignment is a set of sequences that are meant to be answered by a
 * group of students. It is owned by a user and can be shared with other
 * users.
 *
 * @see Sequence
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
    @Column(name = "`uuid`", columnDefinition = "BINARY(16)")
    var globalId: UUID = UUID.randomUUID(),

    /**
     * The subject of the assignment.
     *
     * @see Subject
     */
    @field:ManyToOne(fetch = FetchType.EAGER)
    var subject: Subject? = null,

    @field:NotNull
    @Column(name = "`rank`")
    var rank: Int = 0,

    @field:NotNull
    var audience: String = "",

    var description: String? = null,

    @Column(name = "scholar_year")
    @field:NotNull
    var scholarYear: String? = null,

    @Column(name = "accept_anonymous_users")
    @field:NotNull
    var acceptAnonymousUsers: Boolean = false,

    /**
     * The revision mode of the assignment.
     *
     * This revision mode is for the **Konsolidation** app.
     * @see RevisionMode
     */
    @Column(name = "revision_mode")
    @Enumerated(EnumType.STRING)
    var revisionMode: RevisionMode = RevisionMode.NotAtAll

) : AbstractJpaPersistable<Long>(), Comparable<Statement> {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    var dateCreated: Date = Date()

    // initializer block
    init {
        if (scholarYear.isNullOrBlank()) {
            var localDate: LocalDate = dateCreated.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            var month = localDate.month.value
            var year = localDate.year
            scholarYear = if (month < 7) "" + (year - 1) + " - " + (year) else "" + (year) + " - " + (year + 1)
        }
    }

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "assignment",
        targetEntity = Sequence::class
    )
    @OrderBy("rank ASC")
    @SortNatural
    var sequences: MutableList<Sequence> = ArrayList()

    override fun compareTo(other: Statement): Int {
        return rank.compareTo(other.rank)
    }

    fun updateFrom(otherAssignment: Assignment) {
        require(id == otherAssignment.id)
        if (this.version != otherAssignment.version) {
            throw OptimisticLockException()
        }

        this.title = otherAssignment.title
        this.audience = otherAssignment.audience
        this.scholarYear = otherAssignment.scholarYear
        this.description = otherAssignment.description
        this.acceptAnonymousUsers = otherAssignment.acceptAnonymousUsers
        this.revisionMode = otherAssignment.revisionMode
    }

    /**
     * Adds a sequence to the assignment.
     *
     * The sequence must have the same owner as the assignment.
     * @param sequence The sequence to add.
     * @return The added sequence.
     */
    fun addSequence(sequence: Sequence): Sequence {
        require(sequence.owner == owner) {
            "The owner of the assignment cannot be different from the owner of sequence"
        }

        sequences.add(sequence)
        sequence.assignment = this
        sequence.owner = owner

        return sequence
    }
}
