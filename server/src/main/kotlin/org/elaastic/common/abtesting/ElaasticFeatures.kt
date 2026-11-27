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

import org.togglz.core.annotation.ActivationParameter
import org.togglz.core.annotation.DefaultActivationStrategy
import org.togglz.core.annotation.EnabledByDefault
import org.togglz.core.context.FeatureContext
import org.togglz.spring.activation.SpringProfileActivationStrategy

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
    @EnabledByDefault
    @DefaultActivationStrategy(
        id = SpringProfileActivationStrategy.ID,
        parameters = [
            ActivationParameter(name ="profiles", value = "testing")
        ]
    )
    FUNCTIONAL_TESTING,

    /**
     * Import and Export Subjects as a ZIP archive
     */
    @EnabledByDefault
    IMPORT_EXPORT,

    @EnabledByDefault
    @DefaultActivationStrategy(
        id = SomeTeachersAllStudentsActivationStrategy.ID,
        parameters = [
            ActivationParameter(name = "users", value = "fsil")
        ]
    )
    CHATGPT_EVALUATION,

    // Disabled by default, accessible on username strategy
    REVISION_ASSIGNMENT;

    fun isActive(): Boolean {
        return FeatureContext.getFeatureManager().isActive { name }
    }
}
