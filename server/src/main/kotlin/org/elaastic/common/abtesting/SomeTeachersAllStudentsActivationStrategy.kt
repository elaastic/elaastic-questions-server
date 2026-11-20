/*
 * Elaastic - formative assessment system
 * Copyright (C) 2019-2026. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.common.abtesting

import org.elaastic.user.Role
import org.togglz.core.activation.UsernameActivationStrategy
import org.togglz.core.repository.FeatureState
import org.togglz.core.user.FeatureUser


/**
 * Strategy that activate a feature for some teachers and all students
 */
class SomeTeachersAllStudentsActivationStrategy : UsernameActivationStrategy() {
    companion object {
        const val ID = "some-teachers-all-students"
    }

    override fun getId() = ID

    override fun getName() = "Some Teachers All Students Strategy"

    override fun isActive(featureState: FeatureState?, user: FeatureUser?): Boolean {
        val roles = user?.getAttribute("roles")
        val isStudent = roles !== null &&
                roles is Set<*> &&
                roles.contains(Role.RoleId.STUDENT.roleName)
        return isStudent || super.isActive(featureState, user)
    }

}