package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.io.ApiConstants
import io.netty.handler.ssl.SslContextBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient
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
        val client = ReactorNettyTcpClient(
            { tcpClient ->
                val sslContextBuilder = SslContextBuilder.forClient()
                val configuredTcpClient = tcpClient
                    .host(props.stomp.host.hostAddress)
                    .port(props.stomp.port)
                if (props.stomp.sslEnabled) {
                    configuredTcpClient.secure { it.sslContext(sslContextBuilder) }
                } else {
                    configuredTcpClient
                }
            },
            StompReactorNettyCodec()
        )
        registry
            .setApplicationDestinationPrefixes("/app")
            .enableStompBrokerRelay(ApiConstants.WebSocket.GamePath)
            .setTcpClient(client)
            .setClientLogin(props.stomp.username)
            .setClientPasscode(props.stomp.password)
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry
            .addEndpoint(ApiConstants.WebSocket.Path)
            .setAllowedOriginPatterns(*props.server.allowedOrigins.split(",").toTypedArray())
            .withSockJS()
    }
}
