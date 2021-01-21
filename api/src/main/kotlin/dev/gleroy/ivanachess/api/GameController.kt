package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.validation.Valid

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

    /**
     * Play move.
     *
     * @param token Player token.
     * @param dto Move.
     * @return Updated game.
     */
    @PutMapping("/{token:^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$}/play")
    @ResponseStatus(HttpStatus.OK)
    fun play(@PathVariable token: UUID, @RequestBody @Valid dto: MoveDto): GameDto {
        val gameInfo = service.play(token, dto.toMove())
        return converter.convert(gameInfo)
    }

    /**
     * Convert DTO to move.
     *
     * @return Move.
     */
    private fun MoveDto.toMove() = Move(
        from = from.toPosition(),
        to = to.toPosition()
    )

    /**
     * Convert DTO to position.
     *
     * @return Position.
     */
    private fun PositionDto.toPosition() = Position(
        col = col,
        row = row
    )
}
