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

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


/**
 * Apply the bootstrap operations specific to development and test modes.
 */
@Component
@Profile("!prod")
@DependsOn("bootstrap")
class DevBootstrap(
    val devUserInitializationService: DevUserInitializationService,
    val devLtiInitializationService: DevLtiInitializationService
) {

    private val logger : Logger = Logger.getLogger(DevBootstrap::class.java.name)

    var mailServer: GreenMail? = null

    @PostConstruct
    fun init() {
        logger.info("Bootstrapping elaastic-questions in development and test modes...")
        devUserInitializationService.initializeDevUsers()
        devLtiInitializationService.initializeDevLtiObjects()
        startDevLocalSmtpServer()
        logger.info("End of the bootstrap")
    }

    fun startDevLocalSmtpServer() {
        mailServer = GreenMail(ServerSetup(10025, "localhost", "smtp"))
        try {
            with(mailServer!!) {
                setUser("elaastic", "elaastic")
                start()
            }
        } catch (e: Exception) {
        }
    }

    fun stopDevLocalSmtpServer() {
        try {
            mailServer?.stop()
        } catch (e: Exception) {
        }
    }

}
