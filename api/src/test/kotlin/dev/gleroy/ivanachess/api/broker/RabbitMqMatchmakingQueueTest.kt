@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.broker

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.io.DefaultMatchConverter
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.MatchQueueMessage
import dev.gleroy.ivanachess.io.MatchmakingQueueMessage
import dev.gleroy.ivanachess.io.UserQueueMessage
import dev.gleroy.ivanachess.io.WebSocketSender
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate

internal class RabbitMqMatchmakingQueueTest {
    private val props = Properties()
    private val objectMapper = ObjectMapper().findAndRegisterModules()
    private val matchConverter = DefaultMatchConverter()

    private lateinit var userService: UserService
    private lateinit var gameService: GameService
    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var webSocketSender: WebSocketSender
    private lateinit var queue: RabbitMqMatchmakingQueue

    @BeforeEach
    fun beforeEach() {
        userService = mockk()
        gameService = mockk()
        rabbitTemplate = mockk()
        webSocketSender = mockk()
        queue = RabbitMqMatchmakingQueue(
            userService = userService,
            gameService = gameService,
            matchConverter = matchConverter,
            objectMapper = objectMapper,
            rabbitTemplate = rabbitTemplate,
            webSocketSender = webSocketSender,
            props = props,
        )
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(userService, gameService, rabbitTemplate, webSocketSender)
    }

    @Nested
    inner class put {
        private val user = User(
            pseudo = "user",
            email = "user@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
        )
        private val message = MatchmakingQueueMessage.Join(UserQueueMessage(user.id, user.pseudo))
        private val messageJson = objectMapper.writeValueAsString(message)

        @Test
        fun `should send join message on queue`() {
            every { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) } returns Unit

            queue.put(user)

            verify { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) }
        }
    }

    @Nested
    inner class remove {
        private val user = User(
            pseudo = "user",
            email = "user@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
        )
        private val message = MatchmakingQueueMessage.Leave(UserQueueMessage(user.id, user.pseudo))
        private val messageJson = objectMapper.writeValueAsString(message)

        @Test
        fun `should leave message on queue`() {
            every { rabbitTemplate.convertAndSend(props.broker.matchmakingLeaveExchange, "", messageJson) } returns Unit

            queue.remove(user)

            verify { rabbitTemplate.convertAndSend(props.broker.matchmakingLeaveExchange, "", messageJson) }
        }
    }

    @Nested
    inner class handleMatchMessage {
        private val whitePlayer = User(
            pseudo = "white",
            email = "white@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
        )
        private val blackPlayer = User(
            pseudo = "white",
            email = "white@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS",
        )
        private val message = MatchQueueMessage(
            whitePlayer = UserQueueMessage(whitePlayer.id, whitePlayer.pseudo),
            blackPlayer = UserQueueMessage(blackPlayer.id, blackPlayer.pseudo)
        )
        private val json = objectMapper.writeValueAsString(message)
        private val match = Match(
            entity = GameEntity(
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer,
            ),
        )
        private val gameRepresentation = matchConverter.convertToRepresentation(match)

        @Test
        fun `should do nothing if message is invalid`() {
            queue.handleMatchMessage("")
        }

        @Test
        fun `should do nothing if white player does not exist`() {
            every { userService.getById(whitePlayer.id) } throws EntityNotFoundException("")

            queue.handleMatchMessage(json)

            verify { userService.getById(whitePlayer.id) }
        }

        @Test
        fun `should do nothing if black player does not exist`() {
            every { userService.getById(whitePlayer.id) } returns whitePlayer
            every { userService.getById(blackPlayer.id) } throws EntityNotFoundException("")

            queue.handleMatchMessage(json)

            verify { userService.getById(whitePlayer.id) }
            verify { userService.getById(blackPlayer.id) }
        }

        @Test
        fun `should create new game and send it to web socket`() {
            every { userService.getById(whitePlayer.id) } returns whitePlayer
            every { userService.getById(blackPlayer.id) } returns blackPlayer
            every { gameService.create(whitePlayer, blackPlayer) } returns match
            every { webSocketSender.sendGame(gameRepresentation) } returns Unit

            queue.handleMatchMessage(json)

            verify { userService.getById(whitePlayer.id) }
            verify { userService.getById(blackPlayer.id) }
            verify { gameService.create(whitePlayer, blackPlayer) }
            verify { webSocketSender.sendGame(gameRepresentation) }
        }
    }
}
