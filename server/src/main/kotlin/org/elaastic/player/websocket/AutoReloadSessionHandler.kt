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

package org.elaastic.player.websocket

import java.io.IOException
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.logging.Logger
import javax.websocket.Session

object AutoReloadSessionHandler{

    val logger = Logger.getLogger(AutoReloadSessionHandler::class.java.name)
    val sessions = ConcurrentLinkedQueue<Session>()

    fun addSession(session:Session) {
        sessions.add(session)
    }

    fun removeSession(session: Session) {
        sessions.remove(session)
    }

    fun broadcastReload(sequenceId: Long) {
        val seqIdAsStr = sequenceId.toString()
        try {
            sessions.forEach {
                if (it.isOpen && it.userProperties["sequenceId"] == seqIdAsStr) {
                    it.basicRemote.sendText("reload")
                }
            }
        } catch (e: IOException) {
            logger.severe(e.message)
        }

    }

}
