package dev.gleroy.ivanachess.api.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * RabbitMQ implementation of matchmaking queue.
 *
 * @param userService User service.
 * @param gameService Game service.
 * @param matchConverter Match converter.
 * @param objectMapper Object mapper.
 * @param rabbitTemplate Rabbit template.
 * @param webSocketSender Web socket sender.
 * @param props Properties.
 */
@Component
class RabbitMqMatchmakingQueue(
    private val userService: UserService,
    private val gameService: GameService,
    private val matchConverter: MatchConverter,
    private val objectMapper: ObjectMapper,
    private val rabbitTemplate: RabbitTemplate,
    private val webSocketSender: WebSocketSender,
    private val props: Properties,
) : MatchmakingQueue {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(RabbitMqMatchmakingQueue::class.java)
    }

    override fun put(user: User) {
        val message = MatchmakingQueueMessage.Join(UserQueueMessage(user.id, user.pseudo))
        val json = objectMapper.writeValueAsString(message)
        rabbitTemplate.convertAndSend(props.broker.matchmakingQueue, json)
        Logger.debug("Message '$json' sent on ${props.broker.matchmakingQueue} queue")
    }

    override fun remove(user: User) {
        val message = MatchmakingQueueMessage.Leave(UserQueueMessage(user.id, user.pseudo))
        val json = objectMapper.writeValueAsString(message)
        rabbitTemplate.convertAndSend(props.broker.matchmakingLeaveExchange, "", json)
        Logger.debug("Message '$json' sent on ${props.broker.matchmakingQueue} queue")
    }

    /**
     * Handle message from match queue.
     *
     * @param json JSON-serialized match message.
     */
    @RabbitListener(queues = ["\${ivana-chess.broker.match-queue}"])
    internal fun handleMatchMessage(json: String) {
        Logger.debug("Message '$json' received from '${props.broker.matchQueue}' queue")
        val message = try {
            objectMapper.readValue<MatchQueueMessage>(json)
        } catch (exception: IOException) {
            Logger.error("Message '$json' cannot be deserialized as match message", exception)
            return
        }
        try {
            val whitePlayer = userService.getById(message.whitePlayer.id)
            val blackPlayer = userService.getById(message.blackPlayer.id)
            val match = gameService.create(whitePlayer, blackPlayer)
            webSocketSender.sendGame(matchConverter.convertToRepresentation(match))
        } catch (exception: EntityNotFoundException) {
            Logger.error(
                "Unable to create game '${message.whitePlayer.pseudo}' (${message.whitePlayer.id} vs. " +
                        "'${message.blackPlayer.pseudo}' (${message.blackPlayer.id})",
                exception
            )
        }
    }
}
