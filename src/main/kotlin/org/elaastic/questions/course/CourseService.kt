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

package org.elaastic.questions.course

import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
@Transactional
class CourseService (
        @Autowired val courseRepository: CourseRepository,
        @Autowired val entityManager: EntityManager
){

    fun get(id: Long, fetchSubjects: Boolean = false): Course {
        return when (fetchSubjects){
            true -> courseRepository.findOneWithSubjectsById(id)
            false -> courseRepository.findOneById(id)
        } ?: throw EntityNotFoundException("There is no course for id \"$id\"")
    }

    fun save(course: Course): Course {
        return courseRepository.save(course)
    }

    fun count(): Long {
        return courseRepository.count()
    }

    fun findByGlobalId(globalId: String) : Course {
        return courseRepository.findByGlobalId(globalId)
    }

    fun findOneById(id: Long) : Course {
        return courseRepository.findOneById(id)
    }

    fun findAllByOwner(owner: User,
                       pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated")))
            : Page<Course> {
        return courseRepository.findAllByOwner(owner, pageable)
    }
}