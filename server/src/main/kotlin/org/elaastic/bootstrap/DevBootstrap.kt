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
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.logging.Level
import java.util.logging.Logger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


/**
 * Apply the bootstrap operations specific to development and test modes.
 */
@Component
@Profile("!prod")
class DevBootstrap(
    val devUserInitializationService: DevUserInitializationService,
    val devLtiInitializationService: DevLtiInitializationService
) {

    private val logger : Logger = Logger.getLogger(DevBootstrap::class.java.name)
    var mailServer: GreenMail? = null

    @PostConstruct
    fun init() {
        logger.info("Elaastic bootstrap started (with development features)")
        devUserInitializationService.initializeDevUsers()
        devLtiInitializationService.initializeDevLtiObjects()
        startDevLocalSmtpServer()
        logger.info("Elaastic bootstrap completed")    }

    fun startDevLocalSmtpServer() {
        val server = GreenMail(ServerSetup(10025, "localhost", "smtp"))
        mailServer = server
        try {
            server.setUser("elaastic", "elaastic")
            server.start()
        } catch (e: IllegalStateException) {
            logger.log(Level.WARNING, "Failed to start the local SMTP server", e)
        }
    }

    @PreDestroy
    fun stopDevLocalSmtpServer() {
        try {
            mailServer?.stop()
        } catch (e: IllegalStateException) {
            logger.log(Level.WARNING, "Failed to stop the local SMTP server", e)
        }
    }

}
