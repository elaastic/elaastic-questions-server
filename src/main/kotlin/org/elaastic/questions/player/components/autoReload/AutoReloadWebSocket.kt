package org.elaastic.questions.player.components.autoReload

import org.springframework.stereotype.Component
import java.util.concurrent.CopyOnWriteArraySet
import javax.websocket.*
import javax.websocket.server.PathParam
import javax.websocket.server.ServerEndpoint

@Component
@ServerEndpoint(value = "/player/sequence/{id}/auto-reload-websocket")
class AutoReloadWebSocket {
    companion object {
        val autoReloadWebSockets = CopyOnWriteArraySet<AutoReloadWebSocket>()

        fun broadcastReload(sequenceId: Long) {
            autoReloadWebSockets.forEach { ws ->
                if (ws.sequenceId == sequenceId) {
                    synchronized(ws) {
                        ws.session!!.basicRemote.sendText("reload")
                    }
                }
            }
        }
    }

    var session: Session? = null
    var sequenceId = -1L

    @OnOpen
    fun onOpen(session: Session, @PathParam("id") sequenceId: Long) {
        this.session = session
        autoReloadWebSockets.add(this)
        this.sequenceId = sequenceId
    }

    @OnClose
    fun onClose(session: Session) {
        autoReloadWebSockets.remove(this)
    }
}
