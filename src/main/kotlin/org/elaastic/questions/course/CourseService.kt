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
import org.elaastic.questions.subject.Subject
import org.elaastic.questions.subject.SubjectRepository
import org.elaastic.questions.subject.SubjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional

@Service
@Transactional
class CourseService (
    @Autowired val courseRepository: CourseRepository,
    @Autowired val subjectRepository: SubjectRepository,
    @Autowired val subjectService: SubjectService,
    @Autowired val entityManager: EntityManager
){

    fun get(id: Long, fetchSubjects: Boolean = false): Course {
        return when (fetchSubjects){
            true -> courseRepository.findOneWithSubjectsById(id)
            false -> courseRepository.findOneById(id)
        } ?: throw EntityNotFoundException("There is no course for id \"$id\"")
    }

    fun get(user : User, id: Long) : Course{
        courseRepository.findOneById(id).let{
            if (!user.isTeacher()) {
                throw AccessDeniedException("You are not authorized to access to this course")
            }
            if(user != it.owner){
                throw AccessDeniedException("This course doesn't belong to you")
            }
            return it
        }
    }

    fun delete(user: User, course: Course) {
        require(user == course.owner) {
            "Only the owner can delete an assignment"
        }
        require(course.subjects.size == 0) {
            "The course must be empty to be deleted"
        }
        courseRepository.delete(course)
    }

    fun touch(course: Course) {
        course.lastUpdated = Date()
        courseRepository.save(course)
    }

    fun removeSubject(user : User, subject: Subject){
        require(user == subject.owner) {
            "Only the owner can delete a subject"
        }
        val course = subject.course!!
        touch(course)
        course.subjects.remove(subject)
        entityManager.flush()
        subjectRepository.delete(subject) // all other linked entities are deleted by DB cascade
        entityManager.flush()
        entityManager.clear()
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

    fun addSubject(course: Course?, subject: Subject): Subject {
        if (course == null) {
            subjectService.save(subject)
            subject.course = null
        } else {
            subject.course = course
            subjectService.save(subject)
            course.subjects.add(subject)
            touch(course)
        }
        return subject

    }
}