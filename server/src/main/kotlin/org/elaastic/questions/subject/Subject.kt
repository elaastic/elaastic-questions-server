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

package org.elaastic.questions.subject

import org.elaastic.assignment.Assignment
import org.elaastic.questions.course.Course
import org.elaastic.user.User
import org.elaastic.common.persistence.AbstractJpaPersistable
import org.elaastic.questions.subject.statement.Statement
import org.hibernate.annotations.SortNatural
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.collections.ArrayList

/**
 * A subject is a container for statements and assignments.
 * It is owned by a user and can be part of a course.
 *
 * @see Course
 * @see Statement
 * @see Assignment
 */
@Entity
@NamedEntityGraph(
        name = "Subject.statements_assignments",
        attributeNodes = [
            NamedAttributeNode(
                    value = "statements"
            ),
            NamedAttributeNode(
                    value = "assignments"
                    )
        ]
)
@EntityListeners(AuditingEntityListener::class)
class Subject (

    @field:NotNull
        @field:NotBlank
        var title: String,

    @field:ManyToOne(fetch = FetchType.LAZY)
        var owner: User,

    @field:ManyToOne(fetch = FetchType.LAZY)
        var parentSubject: Subject? = null,

    @field:ManyToOne(fetch = FetchType.EAGER)
        var course: Course? = null

): AbstractJpaPersistable<Long>() {

    @Version
    var version: Long? = null

    @Column(name = "date_created")
    @CreatedDate
    lateinit var dateCreated: Date

    @LastModifiedDate
    @Column(name = "last_updated")
    var lastUpdated: Date? = null

    /**
     * The list of assignments in the subject.
     *
     * The assignments are ordered by last updated date.
     * @see Assignment
     */
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "subject",
            targetEntity = Assignment::class)
    @OrderBy("lastUpdated DESC")
    var assignments: MutableSet<Assignment> = mutableSetOf()

    /**
     * The list of statements in the subject.
     *
     * The statements are ordered by rank.
     * @see Statement
     */
    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "subject",
            targetEntity = Statement::class)
    @OrderBy("rank ASC")
    @SortNatural
    var statements: MutableList<Statement> = ArrayList()

    @field:NotNull
    @Column(name="`uuid`", columnDefinition = "BINARY(16)")
    var globalId: UUID = UUID.randomUUID()


    /**
     * Update the subject with the values of another subject.
     *
     * @param otherSubject the subject to copy the values from
     * @throws IllegalArgumentException if the id of the other subject is different from this subject
     * @throws OptimisticLockException if the version of the other subject is different from this subject
     */
    fun updateFrom(otherSubject: Subject) {
        require(id == otherSubject.id)
        if (this.version != otherSubject.version) {
            throw OptimisticLockException()
        }

        this.title = otherSubject.title
        this.course = otherSubject.course
    }

    /**
     * Add a statement to the subject.
     *
     * @param statement the statement to add
     * @return the added statement
     * @throws IllegalArgumentException if the owner of the statement is different from the owner of the subject
     */
    fun addStatement(statement: Statement): Statement {
        require(statement.owner == owner) {
            "The owner of the statement cannot be different from the owner of subject"
        }

        statements.add(statement)
        statement.subject = this
        statement.owner = owner

        return statement
    }

    /**
     * Add an assignment to the subject.
     *
     * @param assignment the assignment to add
     * @return the added assignment
     * @throws IllegalArgumentException if the owner of the assignment is different from the owner of the subject
     */
    fun addAssignment(assignment: Assignment): Assignment {
        require(assignment.owner == owner) {
            "The owner of the assignment cannot be different from the owner of subject"
        }

        assignments.add(assignment)
        assignment.subject = this
        assignment.owner = owner

        return assignment
    }
}
