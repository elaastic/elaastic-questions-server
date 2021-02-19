package org.elaastic.questions.course

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.persistence.EntityManager

@Service
class CourseService (
        @Autowired val courseRepository: CourseRepository,
        @Autowired val entityManager: EntityManager
){

    fun save(course: Course): Course {
        return courseRepository.save(course)
    }

    fun count(): Long {
        return courseRepository.count()
    }

    fun findGlobalById(id: Long) : Course {
        return courseRepository.findByGlobalId(id)
    }

    fun findOneById(id: Long) : Course {
        return courseRepository.findOneById(id)
    }


}