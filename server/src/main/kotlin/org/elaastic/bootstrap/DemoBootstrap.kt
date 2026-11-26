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

import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct

/**
 * Initialize the demo content at bootstrap when demo profile is active after test users are initialized.
 */
@Component
@Profile("demo")
@DependsOn("testUsersBootstrap")
class DemoBootstrap(val demoInitializationService: DemoInitializationService) {

    private val logger: Logger = Logger.getLogger(DemoBootstrap::class.java.name)

    @PostConstruct
    fun init() {
        logger.info("Bootstrapping elaastic-questions in demo and testing mode")
        demoInitializationService.initializeDemoContent()
        logger.info("End of the bootstrap")
    }

}
