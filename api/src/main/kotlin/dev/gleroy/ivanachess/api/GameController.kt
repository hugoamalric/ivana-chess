@file:Suppress("RegExpUnexpectedAnchor")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.api.security.UserDetailsAdapter
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.io.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
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
 * @param gameConverter Game info converter.
 * @param pageConverter Page converter.
 * @param messagingTemplate Messaging template.
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
     * @return Game representation
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid representation: GameCreation): GameRepresentation.Complete {
        val whitePlayer = getPlayer(representation.whitePlayer) { PlayerNotFoundException.White(it) }
        val blackPlayer = getPlayer(representation.blackPlayer) { PlayerNotFoundException.Black(it) }
        val match = gameService.create(whitePlayer, blackPlayer)
        return gameConverter.convertToCompleteRepresentation(match)
    }

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game representation
     */
    @GetMapping("/{id:${ApiConstants.UuidRegex}}")
    @ResponseStatus(HttpStatus.OK)
    fun get(@PathVariable id: UUID): GameRepresentation {
        val gameEntity = gameService.getById(id)
        val game = gameService.getGameById(id)
        return gameConverter.convertToCompleteRepresentation(Match(gameEntity, game))
    }

    /**
     * Get all games.
     *
     * @param pageParams Page parameters.
     * @return Page.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getPage(@Valid pageParams: PageQueryParameters): PageRepresentation<GameRepresentation.Summary> {
        val pageOpts = pageConverter.convertToOptions<GameEntity>(pageParams)
        return pageConverter.convertToRepresentation(gameService.getPage(pageOpts)) {
            gameConverter.convertToSummaryRepresentation(
                it
            )
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
     * @param representation Move.
     * @param auth Authentication.
     * @return Game representation
     */
    @PutMapping("/{id:${ApiConstants.UuidRegex}}/play")
    @ResponseStatus(HttpStatus.OK)
    fun play(
        @PathVariable id: UUID,
        @RequestBody @Valid representation: MoveRepresentation,
        auth: Authentication
    ): GameRepresentation {
        val principal = auth.principal as UserDetailsAdapter
        val match = gameService.play(id, principal.user, moveConverter.convertToMove(representation))
        return gameConverter.convertToCompleteRepresentation(match).apply {
            val path = "${ApiConstants.WebSocket.GamePath}${match.entity.id}"
            messagingTemplate.convertAndSend(path, this)
            Logger.debug("Game ${match.entity.id} sent to websocket broker on $path")
        }
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
