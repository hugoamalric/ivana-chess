package dev.gleroy.ivanachess.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class IvanaChessApi

fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
