package org.elaastic.questions.features

import org.togglz.core.annotation.ActivationParameter
import org.togglz.core.annotation.DefaultActivationStrategy
import org.togglz.core.annotation.EnabledByDefault
import org.togglz.core.context.FeatureContext

/**
 * This enum declares the available features for elaastic
 *
 * The default configuration is provided by resources/togglz.features-file.properties
 *
 * The Togglz console allows admin users to update the features config at runtime
 * (accessing /togglz-console/index)
 */
enum class ElaasticFeatures {
    /**
     * If the file togglz.features-file.properties is defined at the root classpath, it will overload
     * the default configuration defined bellow
     */
    @EnabledByDefault
    @DefaultActivationStrategy(
        id = ShowRecommendationsActivationStrategy.ID,
        parameters = [
            ActivationParameter(name = "becameActiveAfter", value = "01/01/2022"),
            ActivationParameter(name = "users", value = ""),
        ]
    )
    RECOMMENDATIONS,

    /**
     * Feature dedicated to testing purpose
     * It provides access to /test and /player/test pages
     * It also activate data generation for functional tests (including generating a test subject & scripting
     * learners interactions)
     */
    FUNCTIONAL_TESTING;

    fun isActive(): Boolean {
        return FeatureContext.getFeatureManager().isActive { name }
    }
}