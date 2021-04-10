@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.broker

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.io.DefaultMatchConverter
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.ApiConstants
import dev.gleroy.ivanachess.io.MatchmakingMessage
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate

internal class RabbitMqMatchmakingQueueTest {
    private val props = Properties()
    private val matchConverter = DefaultMatchConverter()
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    private lateinit var gameService: GameService
    private lateinit var userService: UserService
    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var messagingTemplate: SimpMessagingTemplate
    private lateinit var queue: RabbitMqMatchmakingQueue

    @BeforeEach
    fun beforeEach() {
        gameService = mockk()
        userService = mockk()
        rabbitTemplate = mockk()
        messagingTemplate = mockk()
        queue = RabbitMqMatchmakingQueue(
            gameService = gameService,
            userService = userService,
            matchConverter = matchConverter,
            objectMapper = objectMapper,
            rabbitTemplate = rabbitTemplate,
            messagingTemplate = messagingTemplate,
            props = props,
        )
    }

    @Nested
    inner class put {
        private val user = User(
            pseudo = "user",
            email = "user@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
        private val message = MatchmakingMessage.Join(user.id)
        private val messageJson = objectMapper.writeValueAsString(message)

        @Test
        fun `should send join message on queue`() {
            every { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) } returns Unit

            queue.put(user)

            verify { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) }
            confirmVerified(rabbitTemplate)
        }
    }

    @Nested
    inner class remove {
        private val user = User(
            pseudo = "user",
            email = "user@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
        private val message = MatchmakingMessage.Leave(user.id)
        private val messageJson = objectMapper.writeValueAsString(message)

        @Test
        fun `should leave message on queue`() {
            every { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) } returns Unit

            queue.remove(user)

            verify { rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, messageJson) }
            confirmVerified(rabbitTemplate)
        }
    }

    @Nested
    inner class handleMessage {
        private val whitePlayer = User(
            pseudo = "white",
            email = "white@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
        private val blackPlayer = User(
            pseudo = "black",
            email = "black@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
        private val joinMessageJson = objectMapper.writeValueAsString(MatchmakingMessage.Join(blackPlayer.id))
        private val leaveMessageJson = objectMapper.writeValueAsString(MatchmakingMessage.Leave(blackPlayer.id))
        private val match = Match(
            entity = GameEntity(
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer,
            ),
        )
        private val gameRepresentation = matchConverter.convertToRepresentation(match)

        @Test
        fun `should do nothing if message is not valid`() {
            queue.handleMessage("")
            queue.queue.shouldBeEmpty()
        }

        @Test
        fun `should put user ID in queue if message is join and queue is empty`() {
            queue.handleMessage(joinMessageJson)
            queue.queue.peek() shouldBe blackPlayer.id
        }

        @Test
        fun `should do nothing if message is join and black player does not exist`() {
            queue.queue.put(whitePlayer.id)

            every { userService.getById(blackPlayer.id) } throws EntityNotFoundException("")

            queue.handleMessage(joinMessageJson)

            verify { userService.getById(blackPlayer.id) }
            confirmVerified(userService)
        }

        @Test
        fun `should put black player in queue if message is join and white player does not exist`() {
            queue.queue.put(whitePlayer.id)

            every { userService.getById(blackPlayer.id) } returns blackPlayer
            every { userService.getById(whitePlayer.id) } throws EntityNotFoundException("")

            queue.handleMessage(joinMessageJson)
            queue.queue shouldHaveSize 1
            queue.queue.peek() shouldBe blackPlayer.id

            verify { userService.getById(blackPlayer.id) }
            verify { userService.getById(whitePlayer.id) }
            confirmVerified(userService)
        }

        @Test
        fun `should create game if message is join and all players exists`() {
            queue.queue.put(whitePlayer.id)

            every { userService.getById(blackPlayer.id) } returns blackPlayer
            every { userService.getById(whitePlayer.id) } returns whitePlayer
            every { gameService.create(whitePlayer, blackPlayer) } returns match
            every {
                messagingTemplate.convertAndSend(
                    ApiConstants.WebSocket.MatchPath,
                    gameRepresentation
                )
            } returns Unit

            queue.handleMessage(joinMessageJson)
            queue.queue.shouldBeEmpty()

            verify { userService.getById(blackPlayer.id) }
            verify { userService.getById(whitePlayer.id) }
            verify { gameService.create(whitePlayer, blackPlayer) }
            verify { messagingTemplate.convertAndSend(ApiConstants.WebSocket.MatchPath, gameRepresentation) }
            confirmVerified(userService, gameService)
        }

        @Test
        fun `should do nothing if user if message is leave and user is not in queue`() {
            queue.queue.put(whitePlayer.id)

            queue.handleMessage(leaveMessageJson)
            queue.queue.peek() shouldBe whitePlayer.id
        }

        @Test
        fun `should remove user from queue if message is leave and user is in queue`() {
            queue.queue.put(whitePlayer.id)
            queue.queue.put(blackPlayer.id)

            queue.handleMessage(leaveMessageJson)
            queue.queue.peek() shouldBe whitePlayer.id
        }
    }
}
