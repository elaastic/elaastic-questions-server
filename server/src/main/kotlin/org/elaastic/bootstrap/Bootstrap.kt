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

import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


/**
 * Bootstrap the application.
 *
 * @param bootstrapService the bootstrap service
 */
@Component
class Bootstrap(val bootstrapService: BootstrapService) {

    val logger: Logger = Logger.getLogger(Bootstrap::class.java.name)

    @PostConstruct
    fun init() {
        logger.info("Bootstrapping elaastic-questions in all modes...")
        logger.info("Migrate to 4.0.0...")
        bootstrapService.migrateTowardVersion400()
        logger.info("End of Migration to 4.0.0")
        logger.info("End of the bootstrap")
    }

}
