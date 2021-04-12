package dev.gleroy.ivanachess.api.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.MatchConverter
import dev.gleroy.ivanachess.io.MatchmakingMessage
import dev.gleroy.ivanachess.io.WebSocketSender
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * RabbitMQ implementation of matchmaking queue listener.
 *
 * @param gameService Game service.
 * @param userService User service.
 * @param matchConverter Match converter.
 * @param objectMapper Object mapper.
 * @param rabbitTemplate Rabbit template.
 * @param webSocketSender Web socket sender.
 * @param props Properties.
 */
@Component
class RabbitMqMatchmaking(
    private val gameService: GameService,
    private val userService: UserService,
    private val matchConverter: MatchConverter,
    private val objectMapper: ObjectMapper,
    private val rabbitTemplate: RabbitTemplate,
    private val webSocketSender: WebSocketSender,
    private val props: Properties
) : Matchmaking {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(RabbitMqMatchmaking::class.java)
    }

    /**
     * Queue of user IDs.
     */
    internal val queue = LinkedBlockingQueue<UUID>()

    override fun put(user: User) {
        val message = MatchmakingMessage.Join(user.id, user.pseudo)
        rabbitTemplate.convertAndSend(props.broker.matchmakingExchange, "", objectMapper.writeValueAsString(message))
        logMessageBroadcast(message)
    }

    override fun remove(user: User) {
        val message = MatchmakingMessage.Leave(user.id, user.pseudo)
        rabbitTemplate.convertAndSend(props.broker.matchmakingExchange, "", objectMapper.writeValueAsString(message))
        logMessageBroadcast(message)
    }

    /**
     * Handle message received from queue.
     *
     * @param message JSON-serialized matchmaking message.
     */
    @RabbitListener(queues = ["\${ivana-chess.broker.client-id}_\${ivana-chess.broker.matchmaking-exchange}"])
    internal fun handleMessage(message: String) {
        Logger.debug(
            "Message '$message' received from '${props.broker.clientId}_${props.broker.matchmakingExchange}' queue"
        )
        val matchmakingMessage = try {
            objectMapper.readValue<MatchmakingMessage>(message)
        } catch (exception: IOException) {
            Logger.error("Message '$message' cannot be deserialized as matchmaking message", exception)
            return
        }
        when (matchmakingMessage) {
            is MatchmakingMessage.Join -> handleJoinMessage(matchmakingMessage)
            is MatchmakingMessage.Leave -> handleLeaveMessage(matchmakingMessage)
        }
    }

    /**
     * Put user ID in queue.
     *
     * @param message Matchmaking message.
     */
    private fun addUserToQueue(message: MatchmakingMessage.Join) {
        queue.put(message.id)
        Logger.debug("User '${message.pseudo}' (${message.id}) joined matchmaking queue")
    }

    /**
     * Handle join message received from queue.
     *
     * @param message Join message.
     */
    private fun handleJoinMessage(message: MatchmakingMessage.Join) {
        when {
            queue.isEmpty() -> addUserToQueue(message)
            queue.contains(message.id) -> Logger.debug(
                "User '${message.pseudo}' (${message.id}) tried to join matchmaking queue but it is already there"
            )
            else -> {
                val blackPlayer = try {
                    userService.getById(message.id)
                } catch (exception: EntityNotFoundException) {
                    Logger.error("Matchmaking aborted", exception)
                    return
                }
                try {
                    val whitePlayer = userService.getById(queue.poll())
                    val match = gameService.create(whitePlayer, blackPlayer)
                    webSocketSender.sendGame(matchConverter.convertToRepresentation(match))
                    remove(whitePlayer)
                    remove(blackPlayer)
                } catch (exception: EntityNotFoundException) {
                    Logger.error("Matchmaking aborted", exception)
                    addUserToQueue(message)
                }
            }
        }
    }

    /**
     * Handle leave message received from queue.
     *
     * @param message Matchmaking message.
     */
    private fun handleLeaveMessage(message: MatchmakingMessage.Leave) {
        if (queue.remove(message.id)) {
            Logger.debug("User '${message.pseudo}' (${message.id}) left matchmaking queue")
        } else {
            Logger.debug(
                "User '${message.pseudo}' (${message.id}) tries to leave matchmaking queue but it is not there"
            )
        }
    }

    /**
     * Log message broadcast if debug logging is enabled.
     *
     * @param message Matchmaking message.
     */
    private fun logMessageBroadcast(message: MatchmakingMessage) {
        Logger.debug(
            "Message of type '${message.type}' for user '${message.pseudo}' (${message.id}) " +
                    "broadcast to '${props.broker.matchmakingExchange}' exchange"
        )
    }
}
