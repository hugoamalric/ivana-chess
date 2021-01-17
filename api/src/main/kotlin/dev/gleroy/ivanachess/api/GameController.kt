package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * Game API controller.
 *
 * @param service Game service.
 * @param converter Game info converter.
 * @param props Properties.
 */
@RestController
@RequestMapping(GameApiPath)
class GameController(
    private val service: GameService,
    private val converter: GameInfoConverter,
    private val props: Properties
) {
    /**
     * Create new game.
     *
     * @return New game.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): GameDto {
        val gameInfo = service.create()
        return converter.convert(gameInfo)
    }
}
