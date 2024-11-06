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

import org.junit.jupiter.api.Test
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.CoreMatchers.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.EnableCaching
import javax.transaction.Transactional



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableCaching
@Transactional
internal class RoleServiceIntegrationTest(
        @Autowired val roleService: RoleService
) {

    @Test
    fun loadAllRole() {
        // Expect :
        assertThat(roleService.roleStudent().name, equalTo(Role.RoleId.STUDENT.roleName))
        assertThat(roleService.roleTeacher().name, equalTo(Role.RoleId.TEACHER.roleName))
        assertThat(roleService.roleAdmin().name, equalTo(Role.RoleId.ADMIN.roleName))
    }

}
