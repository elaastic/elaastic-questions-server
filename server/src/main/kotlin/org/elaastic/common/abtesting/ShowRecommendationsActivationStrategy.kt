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