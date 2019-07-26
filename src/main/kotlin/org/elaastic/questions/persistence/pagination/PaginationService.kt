package org.elaastic.questions.persistence.pagination

import org.springframework.stereotype.Service

/**
 * @author John Tranier
 */
@Service
class PaginationService {

    fun buildInfo(totalPages: Int, currentPage: Int?): PaginationInfo {
        return when {
            totalPages > 1 -> PaginationInfo(
                    paginated = true,
                    pageList = (1..totalPages).toList(),
                    currentPage = currentPage ?: 1
            )
            else -> PaginationInfo(paginated = false)
        }
    }
}