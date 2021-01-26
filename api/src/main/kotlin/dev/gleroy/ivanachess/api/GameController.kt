@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Position
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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
    private companion object {
        /**
         * UUID regex.
         */
        private const val UuidRegex = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$"
    }

    /**
     * Create new game.
     *
     * @return Game DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): GameDto {
        val gameInfo = service.create()
        return converter.convert(gameInfo)
    }

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game DTO.
     */
    @GetMapping("/{id:$UuidRegex}")
    @ResponseStatus(HttpStatus.OK)
    fun get(@PathVariable id: UUID): GameDto {
        val gameInfo = service.get(id)
        return converter.convert(gameInfo)
    }

    /**
     * Play move.
     *
     * @param token Player token.
     * @param dto Move.
     * @return Game DTO.
     */
    @PutMapping("/{token:$UuidRegex}/play")
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
