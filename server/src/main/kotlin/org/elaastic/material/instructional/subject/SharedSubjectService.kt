package org.elaastic.material.instructional.subject

import org.elaastic.user.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Transactional
class SharedSubjectService(
        @Autowired val sharedSubjectRepository: SharedSubjectRepository
) {

    fun getSharedSubject(teacher: User, subject: Subject): SharedSubject? {
        sharedSubjectRepository.findByTeacherAndSubject(teacher, subject).let{
            return it
        }
    }
}