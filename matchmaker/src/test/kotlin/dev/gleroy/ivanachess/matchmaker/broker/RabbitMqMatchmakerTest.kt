@file:Suppress("ClassName")

package dev.gleroy.ivanachess.matchmaker.broker

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.io.MatchQueueMessage
import dev.gleroy.ivanachess.io.MatchmakingQueueMessage
import dev.gleroy.ivanachess.io.UserQueueMessage
import dev.gleroy.ivanachess.matchmaker.Properties
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.util.*

internal class RabbitMqMatchmakerTest {
    private val props = Properties()
    private val objectMapper = ObjectMapper().findAndRegisterModules()

    private lateinit var rabbitTemplate: RabbitTemplate
    private lateinit var matchmaker: RabbitMqMatchmaker

    @BeforeEach
    fun beforeEach() {
        rabbitTemplate = mockk()
        matchmaker = RabbitMqMatchmaker(
            objectMapper = objectMapper,
            rabbitTemplate = rabbitTemplate,
            props = props,
        )
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(rabbitTemplate)
    }

    @Nested
    inner class handleMessage {
        private val whitePlayer = UserQueueMessage(UUID.randomUUID(), "white")
        private val blackPlayer = UserQueueMessage(UUID.randomUUID(), "black")
        private val blackPlayerJoinMessageJson = objectMapper.writeValueAsString(
            MatchmakingQueueMessage.Join(blackPlayer)
        )
        private val blackPlayerLeaveMessageJson = objectMapper.writeValueAsString(
            MatchmakingQueueMessage.Leave(blackPlayer)
        )
        private val matchMessageJson = objectMapper.writeValueAsString(MatchQueueMessage(whitePlayer, blackPlayer))

        @Test
        fun `should do nothing if message is not valid`() {
            matchmaker.handleMessage("")
            matchmaker.queue.shouldBeEmpty()
        }

        @Test
        fun `should put user in queue if message is join and queue is empty`() {
            matchmaker.handleMessage(blackPlayerJoinMessageJson)
            matchmaker.queue.peek() shouldBe blackPlayer
        }

        @Test
        fun `should create game if message is join and queue contains at least one player`() {
            matchmaker.queue.put(whitePlayer)

            every { rabbitTemplate.convertAndSend(props.broker.matchQueue, matchMessageJson) } returns Unit

            matchmaker.handleMessage(blackPlayerJoinMessageJson)
            matchmaker.queue.shouldBeEmpty()

            verify { rabbitTemplate.convertAndSend(props.broker.matchQueue, matchMessageJson) }
        }

        @Test
        fun `should do nothing if user if message is leave and user is not in queue`() {
            matchmaker.queue.put(whitePlayer)

            matchmaker.handleMessage(blackPlayerLeaveMessageJson)
            matchmaker.queue.peek() shouldBe whitePlayer
        }

        @Test
        fun `should remove user from queue if message is leave and user is in queue`() {
            matchmaker.queue.put(whitePlayer)
            matchmaker.queue.put(blackPlayer)

            matchmaker.handleMessage(blackPlayerLeaveMessageJson)
            matchmaker.queue.peek() shouldBe whitePlayer
        }
    }
}
