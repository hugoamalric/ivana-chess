package dev.gleroy.ivanachess.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker

@ConfigurationPropertiesScan
@EnableWebMvc
@EnableWebSocketMessageBroker
@SpringBootApplication
class IvanaChessApi

fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
