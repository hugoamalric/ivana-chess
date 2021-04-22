package dev.gleroy.ivanachess.api

import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.FanoutExchange
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
     * Instantiate match queue.
     *
     * @param props Properties.
     * @return Queue.
     */
    @Bean
    fun matchQueue(props: Properties): Queue = Queue(props.broker.matchQueue)

    /**
     * Instantiate matchmaking queue.
     *
     * @param props Properties.
     * @return Queue.
     */
    @Bean
    fun matchmakingQueue(props: Properties): Queue = Queue(props.broker.matchmakingQueue)

    /**
     * Instantiate matchmaking leave exchange.
     *
     * @param props Properties.
     * @return Matchmaking leave exchange.
     */
    @Bean
    fun matchmakingLeaveExchange(props: Properties): Declarables {
        val leaveExchange = FanoutExchange(props.broker.matchmakingLeaveExchange)
        val bindings = props.broker.matchmakingInstancesIds.split(',')
            .map { BindingBuilder.bind(Queue(it, false)).to(leaveExchange) }
        return Declarables(leaveExchange, *bindings.toTypedArray())
    }
}

/**
 * Start Ivana Chess API.
 *
 * @param args Command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
