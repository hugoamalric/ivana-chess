@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

/**
 * Game API controller.
 *
 * @param gameService Game service.
 * @param userService User service.
 * @param matchmakingQueue Matchmaking queue.
 * @param moveConverter Move converter.
 * @param gameConverter Game converter.
 * @param matchConverter Match converter.
 * @param pageConverter Page converter.
 * @param webSocketSender Web socket sender.
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
    private val matchConverter: MatchConverter,
    private val pageConverter: PageConverter,
    private val webSocketSender: WebSocketSender,
    override val props: Properties,
) : AbstractController() {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(GameController::class.java)
    }

    /**
     * Create new game.
     *
     * @return Representation of game.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid representation: GameCreation): GameRepresentation.Complete {
        val whitePlayer = getPlayer(representation.whitePlayer) { PlayerNotFoundException.White(it) }
        val blackPlayer = getPlayer(representation.blackPlayer) { PlayerNotFoundException.Black(it) }
        val match = gameService.create(whitePlayer, blackPlayer)
        return matchConverter.convertToRepresentation(match)
    }

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Representation of game.
     */
    @GetMapping("/{id:${ApiConstants.UuidRegex}}")
    @ResponseStatus(HttpStatus.OK)
    fun get(@PathVariable id: UUID): GameRepresentation {
        val gameEntity = gameService.getById(id)
        val game = gameService.getGameById(id)
        return matchConverter.convertToRepresentation(Match(gameEntity, game))
    }

    /**
     * Get page of games.
     *
     * @param pageParams Page parameters.
     * @return Page.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPage(@Valid pageParams: PageQueryParameters): PageRepresentation<GameRepresentation.Summary> {
        val pageOpts = pageConverter.convertToOptions(pageParams, GameField.values())
        return pageConverter.convertToRepresentation(gameService.getPage(pageOpts)) { gameEntity ->
            gameConverter.convertToRepresentation(gameEntity)
        }
    }

    /**
     * Put authenticated user to matchmaking queue.
     *
     * @param auth Authentication.
     */
    @PutMapping(ApiConstants.Game.MatchPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun joinMatchmakingQueue(auth: Authentication) {
        matchmakingQueue.put(authenticatedUser(auth))
    }

    /**
     * Remove authenticated user from matchmaking queue.
     *
     * @param auth Authentication.
     */
    @DeleteMapping(ApiConstants.Game.MatchPath)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun leaveMatchmakingQueue(auth: Authentication) {
        matchmakingQueue.remove(authenticatedUser(auth))
    }

    /**
     * Play move.
     *
     * @param id Game ID.
     * @param representation Move.
     * @param auth Authentication.
     * @return Representation of game.
     */
    @PutMapping("/{id:${ApiConstants.UuidRegex}}/play")
    @ResponseStatus(HttpStatus.OK)
    fun play(
        @PathVariable id: UUID,
        @RequestBody @Valid representation: MoveRepresentation,
        auth: Authentication
    ): GameRepresentation {
        val match = gameService.play(id, authenticatedUser(auth), moveConverter.convertToMove(representation))
        return matchConverter.convertToRepresentation(match).apply { webSocketSender.sendGame(this) }
    }

    /**
     * Get player.
     *
     * @param userId User ID.
     * @param onError Function called when user does not exist.
     * @return Player.
     */
    private fun getPlayer(userId: UUID, onError: (EntityNotFoundException) -> PlayerNotFoundException) = try {
        userService.getById(userId)
    } catch (exception: EntityNotFoundException) {
        throw onError(exception).apply { Logger.debug(message) }
    }
}
