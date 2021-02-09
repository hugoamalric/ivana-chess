@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min

/**
 * Game API controller.
 *
 * @param service Game service.
 * @param gameInfoConverter Game info converter.
 * @param pageConverter Page converter.
 * @param messagingTemplate Messaging template.
 * @param props Properties.
 */
@RestController
@RequestMapping(GameApiPath)
@Validated
class GameController(
    private val service: GameService,
    private val gameInfoConverter: GameInfoConverter,
    private val pageConverter: PageConverter,
    private val messagingTemplate: SimpMessagingTemplate,
    private val props: Properties
) {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(GameController::class.java)
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
        return gameInfoConverter.convert(gameInfo)
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
        val gameInfo = service.getById(id)
        return gameInfoConverter.convert(gameInfo)
    }

    /**
     * Get all games.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getAll(
        @RequestParam(name = PageParam, required = false, defaultValue = "1") @Min(1) page: Int,
        @RequestParam(name = SizeParam, required = false, defaultValue = "10") @Min(1) size: Int
    ) = pageConverter.convert(service.getAll(page, size)) { gameInfoConverter.convert(it) }

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
        val gameInfo = service.getByToken(token).let { service.play(it, token, dto.convert(it.game.colorToPlay)) }
        return gameInfoConverter.convert(gameInfo).apply {
            val path = "$TopicPath$GameApiPath/${gameInfo.id}"
            messagingTemplate.convertAndSend(path, this)
            Logger.debug("Game ${gameInfo.id} sent to websocket broker on $path")
        }
    }
}
