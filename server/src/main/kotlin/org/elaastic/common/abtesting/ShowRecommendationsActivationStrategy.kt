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
import org.togglz.core.activation.Parameter
import org.togglz.core.activation.ParameterBuilder
import org.togglz.core.activation.UsernameActivationStrategy
import org.togglz.core.repository.FeatureState
import org.togglz.core.user.FeatureUser
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Activation strategy that will activate a feature only for teachers that
 * became active after the provided date or that belong to the provided list
 * of usernames
 */
class ShowRecommendationsActivationStrategy : UsernameActivationStrategy() {
    companion object {
        const val ID = "show-recommendations"
        const val BECAME_ACTIVE_AFTER = "becameActiveAfter"
    }

    override fun getId() = ID

    override fun getName() = "Show Recommendations Strategy"

    override fun isActive(featureState: FeatureState?, user: FeatureUser?): Boolean {
        if(super.isActive(featureState, user)) {
            return true
        }

        val roles = user?.getAttribute("roles")

        val userActiveDate = user?.getAttribute("activeSince") as LocalDate? ?: LocalDate.of(1970, 1, 1)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val sinceDateStr = featureState?.getParameter(BECAME_ACTIVE_AFTER)
        val sinceDate = LocalDate.parse(sinceDateStr, formatter)

        return roles !== null &&
                roles is Set<*> &&
                roles.contains(Role.RoleId.TEACHER.roleName) &&
                userActiveDate.isAfter(sinceDate)
    }

    override fun getParameters(): Array<Parameter> {
        // return arrayOf(RecommendationDateParameter)
        return arrayOf(
            ParameterBuilder.create(BECAME_ACTIVE_AFTER)
                .label("Became active after")
                .description(
                    """
            the feature will be active if the user became active after the provided date
        """
                ),
            super.getParameters()[0] // the username parameter
        )
    }
}