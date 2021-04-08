package dev.gleroy.ivanachess.api.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.io.GameConverter
import dev.gleroy.ivanachess.core.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

/**
 * RabbitMQ implementation of matchmaking queue listener.
 *
 * @param gameService Game service.
 * @param userService User service.
 * @param gameConverter Game converter.
 * @param objectMapper Object mapper.
 * @param rabbitTemplate Rabbit template.
 * @param messagingTemplate Messaging template.
 * @param props Properties.
 */
@Component
class RabbitMqMatchmakingQueue(
    private val gameService: GameService,
    private val userService: UserService,
    private val gameConverter: GameConverter,
    private val objectMapper: ObjectMapper,
    private val rabbitTemplate: RabbitTemplate,
    private val messagingTemplate: SimpMessagingTemplate,
    private val props: Properties
) : MatchmakingQueue {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(RabbitMqMatchmakingQueue::class.java)
    }

    /**
     * Queue of user IDs.
     */
    internal val queue = LinkedBlockingQueue<UUID>()

    override fun put(user: User) {
        val message = MatchmakingMessage.Join(user.id)
        rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, objectMapper.writeValueAsString(message))
        Logger.debug("Join message for user ${user.id} sent to '${props.broker.matchmakingQueue}' queue")
    }

    override fun remove(user: User) {
        val message = MatchmakingMessage.Leave(user.id)
        rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, objectMapper.writeValueAsString(message))
        Logger.debug("User '${user.pseudo}' (${user.id}) sent to '${props.broker.matchmakingQueue}' queue")
    }

    /**
     * Handle message received from queue.
     *
     * @param message User ID.
     */
    @RabbitListener(queues = ["\${ivana-chess.broker.matchmaking-queue}"])
    internal fun handleMessage(message: String) {
        Logger.debug("Message '$message' received from matchmaking queue")
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
     * Handle join message received from queue.
     *
     * If this queue is empty, the received user ID is put into it.
     * If this queue already contains user ID, nothing is done.
     * If this queue contains at least one user, a game is created.
     *
     * @param message Join message.
     */
    private fun handleJoinMessage(message: MatchmakingMessage.Join) {
        when {
            queue.isEmpty() -> doPut(message.userId)
            queue.contains(message.userId) ->
                Logger.debug("User ${message.userId} tried to join matchmaking queue but it is already there")
            else -> {
                val blackPlayer = try {
                    userService.getById(message.userId)
                } catch (exception: EntityNotFoundException) {
                    Logger.error("Matchmaking aborted: ${exception.message}")
                    return
                }
                try {
                    val whitePlayer = userService.getById(queue.poll())
                    val match = gameService.create(whitePlayer, blackPlayer)
                    messagingTemplate.convertAndSend(
                        ApiConstants.WebSocket.MatchPath,
                        gameConverter.convertToCompleteRepresentation(match)
                    )
                    Logger.debug(
                        "Game ${match.entity.id} sent to websocket broker " +
                                "on ${ApiConstants.WebSocket.MatchPath}"
                    )
                } catch (exception: EntityNotFoundException) {
                    Logger.error("Matchmaking aborted: ${exception.message}")
                    doPut(message.userId)
                }
            }
        }
    }

    /**
     * Handle leave message received from queue.
     *
     * If this queue contains user ID, it is removed.
     *
     * @param message Leave message.
     */
    private fun handleLeaveMessage(message: MatchmakingMessage.Leave) {
        if (queue.remove(message.userId)) {
            Logger.info("User ${message.userId} removed from matchmaking queue")
        } else {
            Logger.debug("User ${message.userId} tries to leave matchmaking queue but it is not there")
        }
    }

    /**
     * Put user ID in queue.
     *
     * @param userId User ID.
     */
    private fun doPut(userId: UUID) {
        queue.put(userId)
        Logger.info("User $userId joined matchmaking queue")
    }
}
