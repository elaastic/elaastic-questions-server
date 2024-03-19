package org.elaastic.questions.features

import org.elaastic.questions.directory.Role
import org.togglz.core.activation.Parameter
import org.togglz.core.activation.ParameterBuilder
import org.togglz.core.activation.UsernameActivationStrategy
import org.togglz.core.context.FeatureContext
import org.togglz.core.repository.FeatureState
import org.togglz.core.spi.ActivationStrategy
import org.togglz.core.user.FeatureUser
import org.togglz.core.util.Strings

/**
 * Strategy that activate a feature only for teachers
 */
class OnlyKonsolidationExperimentationTeachersStrategy : ActivationStrategy  {
    companion object {
        const val ID = "only-konsolidation-experimentation-teachers"
        const val PARAM_USERS = "users"
    }

    override fun getId() = ID

    override fun getName() = "Only Konsolidation Experimentation Teachers Strategy"

    override fun isActive(featureState: FeatureState?, user: FeatureUser?): Boolean {
        if(isActiveForThisUser(featureState, user)) {
            return true
        }

        return false
    }

    private fun isActiveForThisUser(featureState: FeatureState?, user: FeatureUser?): Boolean {
        val usersAsString: String = featureState!!.getParameter(UsernameActivationStrategy.PARAM_USERS)

        if (Strings.isNotBlank(usersAsString)) {
            val users = Strings.splitAndTrim(usersAsString, ",")
            return users.contains(user?.name)
        }
        return false
    }

    override fun getParameters(): Array<Parameter> {
        return arrayOf(
            ParameterBuilder.create(PARAM_USERS)
            .label("Users")
            .largeText()
            .description("A list of users for which the feature is active.")
        )
    }
}