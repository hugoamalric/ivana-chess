package dev.gleroy.ivanachess.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class IvanaChessApi

fun main(args: Array<String>) {
    runApplication<IvanaChessApi>(*args)
}
