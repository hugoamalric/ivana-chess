package dev.gleroy.ivanachess.api

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * WebSocket configuration.
 *
 * @param props Properties.
 */
@Configuration
class WebSocketConfiguration(
    private val props: Properties
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry
            .setApplicationDestinationPrefixes("/app")
            .enableSimpleBroker(ApiConstants.WebSocket.TopicPath)
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint(ApiConstants.WebSocket.Path)
            .setAllowedOriginPatterns(*props.server.allowedOrigins.split(",").toTypedArray())
            .withSockJS()
    }
}
