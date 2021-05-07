@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.broker

import dev.gleroy.ivanachess.io.ColorRepresentation
import dev.gleroy.ivanachess.io.GameRepresentation
import dev.gleroy.ivanachess.io.UserRepresentation
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.messaging.simp.SimpMessagingTemplate
import java.time.OffsetDateTime
import java.util.*

internal class StompWebSocketSenderTest {
    private lateinit var messagingTemplate: SimpMessagingTemplate

    private lateinit var webSocketSender: StompWebSocketSender

    @BeforeEach
    fun beforeEach() {
        messagingTemplate = mockk()

        webSocketSender = StompWebSocketSender(messagingTemplate)
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(messagingTemplate)
    }

    @Nested
    inner class sendGame {
        private val gameRepresentation = GameRepresentation.Complete(
            id = UUID.randomUUID(),
            creationDate = OffsetDateTime.now(),
            whitePlayer = UserRepresentation.Public(
                id = UUID.randomUUID(),
                pseudo = "white",
                creationDate = OffsetDateTime.now(),
                role = UserRepresentation.Role.Simple,
            ),
            blackPlayer = UserRepresentation.Public(
                id = UUID.randomUUID(),
                pseudo = "black",
                creationDate = OffsetDateTime.now(),
                role = UserRepresentation.Role.Simple,
            ),
            turnColor = ColorRepresentation.White,
            state = GameRepresentation.State.InGame,
            winnerColor = null,
            pieces = emptySet(),
            moves = emptyList(),
            possibleMoves = emptySet(),
        )

        @Test
        fun `should send game representation`() {
            every { messagingTemplate.convertAndSend(StompWebSocketSender.GamePath, gameRepresentation) } returns Unit
            every {
                messagingTemplate.convertAndSend(
                    "${StompWebSocketSender.GamePath}-${gameRepresentation.id}",
                    gameRepresentation,
                )
            } returns Unit
            webSocketSender.sendGame(gameRepresentation)
            verify { messagingTemplate.convertAndSend(StompWebSocketSender.GamePath, gameRepresentation) }
            verify {
                messagingTemplate.convertAndSend(
                    "${StompWebSocketSender.GamePath}-${gameRepresentation.id}",
                    gameRepresentation,
                )
            }
        }
    }
}
