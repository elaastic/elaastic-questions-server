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

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.directory.User
import org.elaastic.questions.persistence.AbstractJpaPersistable
import org.hibernate.annotations.SortNatural
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.collections.ArrayList

@Entity
@EntityListeners(AuditingEntityListener::class)
class Subject (

        @field:NotNull
        var title: String,

        var course: String,

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
            mappedBy = "subject",
            targetEntity = Assignment::class)
    @OrderBy("rank ASC")
    @SortNatural
    var assignments: MutableList<Assignment> = ArrayList()

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "subject",
            targetEntity = Statement::class)
    @OrderBy("rank ASC")
    @SortNatural
    var statements: MutableList<Statement> = ArrayList()

    fun updateFrom(otherSubject: Subject) {
        require(id == otherSubject.id)
        if (this.version != otherSubject.version) {
            throw OptimisticLockException()
        }

        this.title = otherSubject.title
        this.course = otherSubject.course
    }

    fun addStatement(statement: Statement): Statement {
        require(statement.owner == owner) {
            "The owner of the statement cannot be different from the owner of subject"
        }

        statements.add(statement);
        statement.subject = this;
        statement.owner = owner

        return statement
    }

    fun addAssignment(assignment: Assignment): Assignment {
        require(assignment.owner == owner) {
            "The owner of the assignment cannot be different from the owner of subject"
        }

        assignments.add(assignment);
        assignment.subject = this;
        assignment.owner = owner

        return assignment
    }
}