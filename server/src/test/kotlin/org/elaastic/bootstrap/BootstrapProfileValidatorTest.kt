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

package org.elaastic.bootstrap

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.context.support.GenericApplicationContext

class BootstrapProfileValidatorTest {

    private val validator = BootstrapProfileValidator()

    @ParameterizedTest
    @ValueSource(strings = ["demo", "test-data"])
    fun `rejects incompatible profiles in production`(profile: String) {
        GenericApplicationContext().use { context ->
            context.environment.setActiveProfiles("prod", profile)

            val exception = assertThrows(IllegalStateException::class.java) {
                validator.initialize(context)
            }

            assertEquals(
                "The Spring profiles 'prod' and '$profile' cannot be active at the same time.",
                exception.message
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["prod", "demo", "test-data", "demo,test-data"])
    fun `accepts compatible profiles`(profiles: String) {
        GenericApplicationContext().use { context ->
            context.environment.setActiveProfiles(*profiles.split(",").toTypedArray())

            assertDoesNotThrow { validator.initialize(context) }
        }
    }
}
