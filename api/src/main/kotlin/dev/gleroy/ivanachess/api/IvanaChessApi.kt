package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.AsciiBoardSerializer
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
     * Instantiate ASCII board serializer.
     *
     * @return ASCII board serializer.
     */
    @Bean
    fun asciiBoardSerializer() = AsciiBoardSerializer()

    /**
     * Instantiate clock.
     *
     * @return Clock.
     */
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}

/**
 * Start Ivana Chess API.
 *
 * @param args Command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
