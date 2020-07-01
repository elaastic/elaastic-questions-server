/*
 * Elaastic - formative assessment system
 * Copyright (C) 2020. University Toulouse 1 Capitole, University Toulouse 3 Paul Sabatier
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

package org.elaastic.questions.player.websocket

import java.util.logging.Logger
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@ServerEndpoint(value = "/player/sequence/auto-reload/{id}")
class AutoReloadServer {

    private val autoReloadSessionHandler = AutoReloadSessionHandler
    val logger = Logger.getLogger(AutoReloadServer::class.java.name)

    @OnOpen
    fun onOpen(session: Session, @PathParam("id") sequenceId: String) {
        session.userProperties.put("sequenceId", sequenceId)
        autoReloadSessionHandler.addSession(session)
    }

    @OnError
    fun onError(session: Session, error:Throwable) {
        logger.severe(error.toString())
        autoReloadSessionHandler.removeSession(session)
    }

    @OnMessage
    fun onMessage(session:Session, msg: String) {
        // do nothing
    }

    @OnClose
    fun onClose(session: Session) {
        autoReloadSessionHandler.removeSession(session)
    }
}
