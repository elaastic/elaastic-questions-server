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

package org.elaastic.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import java.util.*

interface PasswordResetKeyRepository : PagingAndSortingRepository<PasswordResetKey, Long>, QueryByExampleExecutor<PasswordResetKey>, JpaRepository<PasswordResetKey, Long> {

    fun findByUser(user: User): PasswordResetKey?

    fun findByPasswordResetKey(passwordResetKey: String): PasswordResetKey?

    @Query("select prk from PasswordResetKey prk left join prk.user user left join user.settings where prk.dateCreated > ?1 and prk.passwordResetEmailSent = false ")
    fun findAllPasswordResetKeys(expirationDate: Date):Collection<PasswordResetKey>

    fun findAllByDateCreatedLessThan(expirationDate:Date): Collection<PasswordResetKey>

}
