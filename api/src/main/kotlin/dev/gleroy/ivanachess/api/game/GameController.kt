@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.PageConverter
import dev.gleroy.ivanachess.api.Properties
import dev.gleroy.ivanachess.api.broker.MatchmakingQueue
import dev.gleroy.ivanachess.api.io.GameConverter
import dev.gleroy.ivanachess.api.io.MoveConverter
import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import dev.gleroy.ivanachess.api.user.UserIdNotFoundException
import dev.gleroy.ivanachess.api.user.UserService
import dev.gleroy.ivanachess.core.AsciiBoardSerializer
import dev.gleroy.ivanachess.dto.GameCreationDto
import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.Min

/**
 * Game API controller.
 *
 * @param gameService Game service.
 * @param userService User service.
 * @param matchmakingQueue Matchmaking queue.
 * @param moveConverter Move converter.
 * @param gameConverter Game info converter.
 * @param pageConverter Page converter.
 * @param messagingTemplate Messaging template.
 * @param asciiBoardSerializer ASCII board serializer.
 * @param props Properties.
 */
@RestController
@RequestMapping(ApiConstants.Game.Path)
@Validated
class GameController(
    private val gameService: GameService,
    private val userService: UserService,
    private val matchmakingQueue: MatchmakingQueue,
    private val moveConverter: MoveConverter,
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
    @GetMapping(
        value = ["/{id:${ApiConstants.UuidRegex}}${ApiConstants.Game.BoardAsciiPath}"],
        produces = ["text/plain;charset=UTF-8"]
    )
    @ResponseStatus(HttpStatus.OK)
    fun asciiBoard(@PathVariable id: UUID): String {
        val game = gameService.getGameById(id)
        return String(asciiBoardSerializer.serialize(game.board))
    }

    /**
     * Create new game.
     *
     * @return Game DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid dto: GameCreationDto) = try {
        val whitePlayer = userService.getById(dto.whitePlayer)
        val blackPlayer = userService.getById(dto.blackPlayer)
        val gameAndSummary = gameService.create(whitePlayer, blackPlayer)
        gameConverter.convertToCompleteDto(gameAndSummary)
    } catch (exception: UserIdNotFoundException) {
        throw PlayerNotFoundException(exception.id)
    }

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game DTO.
     */
    @GetMapping("/{id:${ApiConstants.UuidRegex}}")
    @ResponseStatus(HttpStatus.OK)
    fun get(@PathVariable id: UUID): GameDto {
        val gameSummary = gameService.getSummaryById(id)
        val game = gameService.getGameById(id)
        return gameConverter.convertToCompleteDto(GameAndSummary(gameSummary, game))
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
        @RequestParam(name = ApiConstants.QueryParams.Page, required = false, defaultValue = "1") @Min(1) page: Int,
        @RequestParam(name = ApiConstants.QueryParams.PageSize, required = false, defaultValue = "10") @Min(1) size: Int
    ) = pageConverter.convert(gameService.getAllSummaries(page, size)) { gameConverter.convertToSummaryDto(it) }

    /**
     * Put authenticated user to matchmaking queue.
     *
     * @param auth Authentication.
     */
    @PutMapping(ApiConstants.Game.MatchPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun joinMatchmakingQueue(auth: Authentication) {
        val principal = auth.principal as UserDetailsAdapter
        matchmakingQueue.put(principal.user)
    }

    /**
     * Remove authenticated user from matchmaking queue.
     *
     * @param auth Authentication.
     */
    @DeleteMapping(ApiConstants.Game.MatchPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveMatchmakingQueue(auth: Authentication) {
        val principal = auth.principal as UserDetailsAdapter
        matchmakingQueue.remove(principal.user)
    }

    /**
     * Play move.
     *
     * @param id Game ID.
     * @param dto Move.
     * @param auth Authentication.
     * @return Game DTO.
     */
    @PutMapping("/{id:${ApiConstants.UuidRegex}}/play")
    @ResponseStatus(HttpStatus.OK)
    fun play(@PathVariable id: UUID, @RequestBody @Valid dto: MoveDto, auth: Authentication): GameDto {
        val principal = auth.principal as UserDetailsAdapter
        val gameAndSummary = gameService.play(id, principal.user, moveConverter.convertToMove(dto))
        return gameConverter.convertToCompleteDto(gameAndSummary).apply {
            val path = "${ApiConstants.WebSocket.GamePath}${gameAndSummary.summary.id}"
            messagingTemplate.convertAndSend(path, this)
            Logger.debug("Game ${gameAndSummary.summary.id} sent to websocket broker on $path")
        }
    }
}
