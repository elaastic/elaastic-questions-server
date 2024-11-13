package org.elaastic.material.instructional.subject

import org.elaastic.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SharedSubjectRepository : JpaRepository<SharedSubject, Long> {

    fun findByTeacherAndSubject(learner: User, subject: Subject): SharedSubject?

    @Query("select shared.subject from SharedSubject as shared where shared.teacher = ?1")
    fun findAllSubjectsForTeacher(user: User,
                                  pageable: Pageable = PageRequest.of(
                                      0,
                                      10,
                                      Sort.by(Sort.Direction.DESC, "dateCreated")
                                  )
    ): Page<Subject>

    fun countAllBySubject(subject: Subject): Int
}