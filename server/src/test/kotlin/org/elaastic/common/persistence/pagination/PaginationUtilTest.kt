/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.elaastic.common.persistence.pagination

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.junit.jupiter.api.Test


internal class PaginationUtilTest {

    @Test
    fun `test buildInfo`() {
        PaginationUtil.buildInfo(0).let {
            assertThat(it.paginated, equalTo(false))
        }

        PaginationUtil.buildInfo(4).let {
            assertThat(it.paginated, equalTo(true))
            assertThat(it.pageList, equalTo(listOf(1, 2, 3, 4)))
            assertThat(it.currentPage, equalTo(1))
        }

        PaginationUtil.buildInfo(4, 2).let {
            assertThat(it.paginated, equalTo(true))
            assertThat(it.pageList, equalTo(listOf(1, 2, 3, 4)))
            assertThat(it.currentPage, equalTo(2))
        }
    }
}
