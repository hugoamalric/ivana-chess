package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import java.util.*

/**
 * Game service.
 */
interface GameService {
    /**
     * Create new game.
     *
     * @param whitePlayer White player.
     * @param blackPlayer Black user.
     * @return Match.
     * @throws PlayersAreSameUserException If white and black player are same user.
     */
    @Throws(PlayersAreSameUserException::class)
    fun create(whitePlayer: User, blackPlayer: User): Match

    /**
     * Get game entity by ID.
     *
     * @param id Game ID.
     * @return Game entity.
     * @throws GameNotFoundException If game entity does not exist.
     */
    @Throws(GameNotFoundException::class)
    fun getEntityById(id: UUID): GameEntity

    /**
     * Get game by the entity ID.
     *
     * @param id Game entity ID.
     * @return Game.
     * @throws GameNotFoundException If game entity does not exist.
     */
    @Throws(GameNotFoundException::class)
    fun getGameById(id: UUID): Game

    /**
     * Get page of game entities.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getPage(page: Int, size: Int): Page<GameEntity>

    /**
     * Play move.
     *
     * @param id Game ID.
     * @param user Player.
     * @param move Move.
     * @return Match.
     * @throws GameNotFoundException If game does not exist.
     * @throws NotAllowedPlayerException If user is not player in given game.
     * @throws InvalidPlayerException If player steals turn of other player.
     * @throws InvalidMoveException If move is invalid.
     */
    @Throws(
        exceptionClasses = [
            GameNotFoundException::class,
            NotAllowedPlayerException::class,
            InvalidPlayerException::class,
            InvalidMoveException::class
        ]
    )
    fun play(id: UUID, user: User, move: Move): Match
}
