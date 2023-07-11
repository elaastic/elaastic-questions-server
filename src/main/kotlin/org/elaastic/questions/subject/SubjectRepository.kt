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

import org.elaastic.questions.directory.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID


interface SubjectRepository : JpaRepository<Subject?, Long> {

    fun findAllByOwner(owner: User, pageable: Pageable): Page<Subject>

    fun findAllByOwnerAndCourseIsNull(owner: User, pageable: Pageable): Page<Subject>

    @EntityGraph(value = "Subject.statements_assignments", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneWithStatementsAndAssignmentsById(id: Long): Subject?

    fun findOneById(id: Long): Subject?

    fun findByGlobalId(globalId: UUID): Subject?

    @Query("select count(s.id) from Subject as s where s.owner=?1 AND s.parentSubject = ?2")
    fun countAllByParentSubject(owner: User, parentSubject: Subject): Int

    @Query("select count(s.id) from Subject as s where s.owner=?1 AND s.title like ?2%")
    fun countAllStartingWithTitle(owner: User, title: String): Int

    fun countByCourseIsNullAndOwner(owner: User): Long

    fun findFirstByOwner(owner: User): Subject?

}