@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.AsciiBoardSerializer
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
 * @param gameConverter Game info converter.
 * @param pageConverter Page converter.
 * @param messagingTemplate Messaging template.
 * @param asciiBoardSerializer ASCII board serializer.
 * @param props Properties.
 */
@RestController
@RequestMapping(GameApiPath)
@Validated
class GameController(
    private val service: GameService,
    private val gameConverter: GameConverter,
    private val pageConverter: PageConverter,
    private val messagingTemplate: SimpMessagingTemplate,
    private val asciiBoardSerializer: AsciiBoardSerializer,
    private val props: Properties
) {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(GameController::class.java)
    }

    /**
     * Get ASCII representation of board.
     *
     * @param id Game ID.
     * @return ASCII representation of board.
     */
    @GetMapping(value =["/{id:$UuidRegex}$BoardAsciiPath"], produces = ["text/plain;charset=UTF-8"])
    @ResponseStatus(HttpStatus.OK)
    fun asciiBoard(@PathVariable id: UUID): String {
        val game = service.getGameById(id)
        return String(asciiBoardSerializer.serialize(game.board))
    }

    /**
     * Create new game.
     *
     * @return Game DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(): GameDto.Complete {
        val gameAndSummary = service.create()
        return gameConverter.convert(gameAndSummary)
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
        val gameSummary = service.getSummaryById(id)
        val game = service.getGameById(id)
        return gameConverter.convert(GameAndSummary(gameSummary, game))
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
    ) = pageConverter.convert(service.getAllSummaries(page, size)) { gameConverter.convert(it) }

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
        val gameAndSummary = service.getSummaryByToken(token).let { service.play(it, token, dto.convert(it.turnColor)) }
        return gameConverter.convert(gameAndSummary).apply {
            val path = "$TopicPath$GameApiPath/${gameAndSummary.summary.id}"
            messagingTemplate.convertAndSend(path, this)
            Logger.debug("Game ${gameAndSummary.summary.id} sent to websocket broker on $path")
        }
    }
}
