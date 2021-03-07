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
     * @return Game and summary.
     */
    fun create(whitePlayer: User, blackPlayer: User): GameAndSummary

    /**
     * Get page of game summaries.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     */
    fun getAllSummaries(page: Int, size: Int): Page<GameSummary>

    /**
     * Get game summary by ID.
     *
     * @param id Game ID.
     * @return Game summary.
     * @throws GameNotFoundException If game does not exist.
     */
    @Throws(GameNotFoundException::class)
    fun getSummaryById(id: UUID): GameSummary

    /**
     * Get game by its ID.
     *
     * @param id Game ID.
     * @return Game.
     * @throws GameNotFoundException If game does not exist.
     */
    @Throws(GameNotFoundException::class)
    fun getGameById(id: UUID): Game

    /**
     * Play move.
     *
     * @param id Game ID.
     * @param user Player.
     * @param move Move.
     * @return Game and summary.
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
    fun play(id: UUID, user: User, move: Move): GameAndSummary
}
