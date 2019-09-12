package org.elaastic.questions.persistence.pagination

import org.springframework.stereotype.Service


@Service
class PaginationUtil {

    companion object {
        fun buildInfo(totalPages: Int,
                      currentPage: Int? = null,
                      specifiedSize: Int? = null): PaginationInfo {
            return when {
                totalPages > 1 -> PaginationInfo(
                        paginated = true,
                        pageList = (1..totalPages).toList(),
                        currentPage = currentPage ?: 1,
                        specifiedSize = specifiedSize
                )
                else -> PaginationInfo(paginated = false)
            }
        }
    }
}