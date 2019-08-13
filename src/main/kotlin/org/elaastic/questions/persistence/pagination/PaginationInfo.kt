package org.elaastic.questions.persistence.pagination


class PaginationInfo(
        val paginated: Boolean,
        val pageList: List<Int>? = null,
        val currentPage: Int? = null
)