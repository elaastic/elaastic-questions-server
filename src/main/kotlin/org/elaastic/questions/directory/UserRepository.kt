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

package org.elaastic.questions.directory

import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*


interface UserRepository : CrudRepository<User, Long>, JpaRepository<User, Long> {

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    override fun findById(id: Long): Optional<User>

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByUsername(username: String): User?

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun getByUsername(username: String): User

    @EntityGraph(value = "User.roles", type = EntityGraph.EntityGraphType.LOAD)
    fun findByEmail(email: String): User?

    @Query("select u from User u join PasswordResetKey key on key.user = u where key.passwordResetKey = ?1")
    fun findByPasswordResetKeyValue(passwordResetKeyValue: String): User?

}
