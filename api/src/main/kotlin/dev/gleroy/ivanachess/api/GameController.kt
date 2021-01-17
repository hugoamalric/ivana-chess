package dev.gleroy.ivanachess.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Game API controller.
 *
 * @param service Game service.
 */
@RestController
@RequestMapping(GameApiPath)
class GameController(
    private val service: GameService
) {
    @PostMapping
    fun create() {
        TODO()
    }
}
