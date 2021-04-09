package dev.gleroy.ivanachess.api.broker

import dev.gleroy.ivanachess.io.GameRepresentation
import dev.gleroy.ivanachess.io.WebSocketSender
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

/**
 * STOMP implementation of web socket sender.
 *
 * @param messagingTemplate Messaging template.
 */
@Component
class StompWebSocketSender(
    private val messagingTemplate: SimpMessagingTemplate,
) : WebSocketSender {
    internal companion object {
        /**
         * Game path.
         */
        const val GamePath = "/topic/game"

        private val Logger = LoggerFactory.getLogger(StompWebSocketSender::class.java)
    }

    override fun sendGame(gameRepresentation: GameRepresentation.Complete) {
        messagingTemplate.convertAndSend("$GamePath-${gameRepresentation.id}", gameRepresentation)
        Logger.debug("Game ${gameRepresentation.id} sent to web socket")
    }
}
