package org.elaastic.common.abtesting

import org.elaastic.user.Role
import org.togglz.core.activation.Parameter
import org.togglz.core.repository.FeatureState
import org.togglz.core.spi.ActivationStrategy
import org.togglz.core.user.FeatureUser

/**
 * Strategy that activate a feature only for teachers
 */
class OnlyTeacherActivationStrategy : ActivationStrategy  {
    companion object {
        const val ID = "only-teacher"
    }

    override fun getId() = ID

    override fun getName() = "Only Teacher Strategy"

    override fun isActive(featureState: FeatureState?, user: FeatureUser?): Boolean {
        val roles = user?.getAttribute("roles")

        return roles !== null &&
                roles is Set<*> && 
                roles.contains(Role.RoleId.TEACHER.roleName)
    }

    override fun getParameters(): Array<Parameter> {
        return arrayOf()
    }
}