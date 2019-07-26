package org.elaastic.questions.persistence.pagination

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test

/**
 * @author John Tranier
 */
internal class PaginationServiceTest {

    val paginationService = PaginationService()


    @Test
    fun `test buildInfo`() {
        paginationService.buildInfo(0).let {
            assertThat(it.paginated, equalTo(false))
        }

        paginationService.buildInfo(4).let {
            assertThat(it.paginated, equalTo(true))
            assertThat(it.pageList, equalTo(listOf(1, 2, 3, 4)))
            assertThat(it.currentPage, equalTo(1))
        }

        paginationService.buildInfo(4, 2).let {
            assertThat(it.paginated, equalTo(true))
            assertThat(it.pageList, equalTo(listOf(1, 2, 3, 4)))
            assertThat(it.currentPage, equalTo(2))
        }
    }
}