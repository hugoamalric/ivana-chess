package dev.gleroy.ivanachess.matchmaker.broker

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.io.MatchQueueMessage
import dev.gleroy.ivanachess.io.MatchmakingQueueMessage
import dev.gleroy.ivanachess.io.UserQueueMessage
import dev.gleroy.ivanachess.matchmaker.Properties
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.concurrent.LinkedBlockingQueue

/**
 * RabbitMQ implementation of matchmaker.
 *
 * @param objectMapper Object mapper.
 * @param rabbitTemplate Rabbit template.
 * @param props Properties.
 */
@Component
class RabbitMqMatchmaker(
    private val objectMapper: ObjectMapper,
    private val rabbitTemplate: RabbitTemplate,
    private val props: Properties,
) {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(RabbitMqMatchmaker::class.java)
    }

    /**
     * Queue of user messages.
     */
    internal val queue = LinkedBlockingQueue<UserQueueMessage>()

    /**
     * Handle message received from queue.
     *
     * @param json JSON-serialized matchmaking message.
     */
    @RabbitListener(queues = ["\${ivana-chess.broker.matchmaking-queue}", "\${ivana-chess.broker.instance-id}"])
    internal fun handleMessage(json: String) {
        Logger.debug("Message '$json' received from '${props.broker.matchmakingQueue}' queue")
        val message = try {
            objectMapper.readValue<MatchmakingQueueMessage>(json)
        } catch (exception: IOException) {
            Logger.error("Message '$json' cannot be deserialized as matchmaking message", exception)
            return
        }
        when (message) {
            is MatchmakingQueueMessage.Join -> handleJoinMessage(message)
            is MatchmakingQueueMessage.Leave -> handleLeaveMessage(message)
        }
    }

    /**
     * Handle join message received from queue.
     *
     * @param message Join message.
     */
    private fun handleJoinMessage(message: MatchmakingQueueMessage.Join) {
        when {
            queue.isEmpty() -> queue.add(message.user).apply {
                Logger.info("User '${message.user.pseudo}' (${message.user.id}) joined matchmaking queue")
            }
            queue.contains(message.user) -> {
                Logger.debug("User '${message.user.pseudo}' (${message.user.id}) is already in matchmaking queue")
            }
            else -> {
                val whitePlayer = queue.poll()
                val matchMessage = MatchQueueMessage(whitePlayer, message.user)
                val json = objectMapper.writeValueAsString(matchMessage)
                rabbitTemplate.convertAndSend(props.broker.matchQueue, json)
                Logger.info(
                    "New match between '${whitePlayer.pseudo}' (${whitePlayer.id}) and " +
                            "'${message.user.pseudo}' (${message.user.id})"
                )
                Logger.debug("Message '$json' sent on '${props.broker.matchQueue}' queue")
            }
        }
    }

    /**
     * Handle leave message received from queue.
     *
     * @param message Matchmaking message.
     */
    private fun handleLeaveMessage(message: MatchmakingQueueMessage.Leave) {
        if (queue.remove(message.user)) {
            Logger.info("User '${message.user.pseudo}' (${message.user.id}) left matchmaking queue")
        }
    }
}
