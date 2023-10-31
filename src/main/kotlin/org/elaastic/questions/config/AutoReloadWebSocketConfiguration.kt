package org.elaastic.questions.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.server.standard.ServerEndpointExporter

@Configuration
@EnableWebSocket
class AutoReloadWebSocketConfiguration {
    @Bean
    fun serverEndpointExporter(): ServerEndpointExporter {
        return ServerEndpointExporter()
    }
}
