package dev.gleroy.ivanachess.matchmaker

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Queue
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * Ivana Chess Matchmaker application.
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class IvanaChessMatchmaker {
    /**
     * Instantiate instance queue.
     *
     * @param props Properties.
     * @return Queue.
     */
    @Bean
    fun instanceQueue(props: Properties): Queue = Queue(props.broker.instanceId, false)

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
     * Instantiate object mapper.
     */
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper()
        .findAndRegisterModules()
        .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
}

/**
 * Start Ivana Chess Matchmaker.
 *
 * @param args Command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<IvanaChessMatchmaker>(*args)
}
