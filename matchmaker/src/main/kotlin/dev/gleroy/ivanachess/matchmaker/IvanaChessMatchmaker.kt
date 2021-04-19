package dev.gleroy.ivanachess.matchmaker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Ivana Chess Matchmaker application.
 */
@ConfigurationPropertiesScan
@SpringBootApplication
class IvanaChessMatchmaker

/**
 * Start Ivana Chess Matchmaker.
 *
 * @param args Command line arguments.
 */
fun main(args: Array<String>) {
    runApplication<IvanaChessMatchmaker>(*args)
}
