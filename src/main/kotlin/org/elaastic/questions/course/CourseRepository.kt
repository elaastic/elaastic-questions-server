package org.elaastic.questions.course

import org.elaastic.questions.directory.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CourseRepository : JpaRepository<Course?, Long> {

    fun findAllByOwner(owner: User, pageable: Pageable): Page<Course>

    fun findOneById(id: Long): Course

    fun findByGlobalId(globalId: String): Course

    @EntityGraph(value = "Course_subjects", type = EntityGraph.EntityGraphType.LOAD)
    fun findOneWithSubjectsById(id: Long): Course?

}