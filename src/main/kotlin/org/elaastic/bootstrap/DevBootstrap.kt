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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


@Component
@Profile("!prod")
class DevBootstrap(
        @Autowired val bootstrapService: BootstrapService
) {

    val LOG : Logger = Logger.getLogger(DevBootstrap::class.toString())

    @PostConstruct
    fun init() {
        LOG.info("Bootstrapping elaastic-questions in development and test modes...")
        bootstrapService.initializeDevUsers()
        bootstrapService.initializeDevLtiObjects()
        bootstrapService.startDevLocalSmtpServer()
        LOG.info("End of the bootstrap")
    }

}
