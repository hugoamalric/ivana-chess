package dev.gleroy.ivanachess.api

import org.springframework.amqp.core.Queue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import java.time.Clock

/**
 * Ivana Chess API application.
 */
@ConfigurationPropertiesScan
@EnableWebMvc
@EnableWebSocketMessageBroker
@SpringBootApplication
class IvanaChessApi {
    /**
     * Instantiate clock.
     *
     * @return Clock.
     */
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

    /**
     * Instantiate matchmaking queue.
     *
     * @param props Properties.
     * @return Matchmaking queue.
     */
    @Bean
    fun matchmakingQueue(props: Properties): Queue = Queue(props.broker.matchmakingQueue)
}

/**
 * Start Ivana Chess API.
 *
 * @param args Command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
