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