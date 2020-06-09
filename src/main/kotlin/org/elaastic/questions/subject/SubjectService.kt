package org.elaastic.questions.subject

import org.elaastic.questions.assignment.Assignment
import org.elaastic.questions.directory.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import javax.persistence.EntityNotFoundException
import javax.transaction.Transactional


@Service
@Transactional
class SubjectService (
        @Autowired val subjectRepository: SubjectRepository


) {

    fun get(id: Long, fetchStatements: Boolean = false, fetchAssignments: Boolean = false): Subject {
        // TODO (+) i18n error message
        var fetchBoth = Pair(fetchStatements,fetchAssignments)
        return when (fetchBoth){
            Pair(first = true, second = true) -> subjectRepository.findOneById(id)
            Pair(first = true, second = false) -> subjectRepository.findOneWithOnlyStatementsById(id)
            Pair(first = false, second = true) -> subjectRepository.findOneEmptyById(id)
            Pair(first = false, second = false) -> subjectRepository.findOneEmptyById(id)
            else -> throw EntityNotFoundException("There is no subject for id \"$id\"")
        } ?: throw EntityNotFoundException("There is no subject for id \"$id\"")
    }

    fun get(user: User, id: Long, fetchStatements: Boolean = false, fetchAssignments: Boolean = false): Subject {
        get(id, fetchStatements, fetchAssignments).let {
            if (it.owner != user) {
                throw AccessDeniedException("You are not autorized to access to this subject")
            }
            return it
        }
    }

    fun save(subject: Subject): Subject {
        return subjectRepository.save(subject)
    }

    fun findAllByOwner(owner: User,
                       pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "lastUpdated")))
            : Page<Subject> {
        return subjectRepository.findAllByOwner(owner, pageable)
    }

}
