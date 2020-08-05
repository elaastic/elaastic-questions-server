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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface SharedSubjectRepository : JpaRepository<SharedSubject, Long> {

    fun findByTeacherAndSubject(learner: User, subject:Subject): SharedSubject?

    @Query("select shared.subject from SharedSubject as shared where shared.teacher = ?1")
    fun findAllSubjectsForTeacher(user: User,
                                     pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated"))): Page<Subject>

    fun countAllBySubject(subject: Subject): Int
}
