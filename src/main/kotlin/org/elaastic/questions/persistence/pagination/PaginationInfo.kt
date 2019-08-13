package org.elaastic.questions.persistence.pagination

/**
 * @author John Tranier
 */
class PaginationInfo(
        val paginated: Boolean,
        val pageList: List<Int>? = null,
        val currentPage: Int? = null
)