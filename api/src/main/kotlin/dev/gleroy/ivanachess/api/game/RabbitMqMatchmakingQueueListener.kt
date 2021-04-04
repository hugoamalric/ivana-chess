package dev.gleroy.ivanachess.api.game

import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * RabbitMQ implementation of matchmaking queue listener.
 */
@Component
class RabbitMqMatchmakingQueueListener : MatchmakingQueueListener {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(RabbitMqMatchmakingQueueListener::class.java)
    }

    @RabbitListener(queues = ["\${ivana-chess.broker.matchmaking-queue}"])
    override fun handle(message: String) {

    }
}
