package dev.gleroy.ivanachess.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@ConfigurationPropertiesScan
@EnableWebMvc
@SpringBootApplication
class IvanaChessApi

fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
