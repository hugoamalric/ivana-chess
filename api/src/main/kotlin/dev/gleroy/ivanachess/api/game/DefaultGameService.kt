package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

/**
 * Default implementation of game service.
 *
 * @param repository Game repository.
 */
@Service
class DefaultGameService(
    private val repository: GameRepository
) : GameService {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DefaultGameService::class.java)
    }

    override fun create(whitePlayer: User, blackPlayer: User): GameAndSummary {
        if (whitePlayer.id == blackPlayer.id) {
            throw PlayersAreSameUserException()
        }
        val gameSummary = repository.save(
            gameSummary = GameSummary(
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer
            )
        )
        Logger.info("New game '${whitePlayer.pseudo}' vs. '${blackPlayer.pseudo}' (${gameSummary.id}) created")
        return GameAndSummary(gameSummary)
    }

    override fun getSummaryById(id: UUID) = repository.getById(id) ?: throw GameNotFoundException(id).apply {
        Logger.debug(message)
    }

    override fun getAllSummaries(page: Int, size: Int) = repository.getAll(page, size)

    override fun getGameById(id: UUID): Game {
        if (!repository.existsById(id)) {
            throw GameNotFoundException(id).apply { Logger.debug(message) }
        }
        return Game(repository.getMoves(id))
    }

    override fun play(id: UUID, user: User, move: Move): GameAndSummary {
        val gameSummary = getSummaryById(id)
        if (user != gameSummary.whitePlayer && user != gameSummary.blackPlayer) {
            throw NotAllowedPlayerException(id, user).apply { Logger.warn(message) }
        }
        val playerTriesToStealTurn = gameSummary.whitePlayer == user &&
                gameSummary.turnColor != Piece.Color.White ||
                gameSummary.blackPlayer == user &&
                gameSummary.turnColor != Piece.Color.Black
        if (playerTriesToStealTurn) {
            throw InvalidPlayerException(gameSummary.id, user).apply { Logger.info(message) }
        }
        val game = Game(repository.getMoves(gameSummary.id))
        if (game.board.pieceAt(move.from)?.color != gameSummary.turnColor) {
            throw InvalidMoveException(
                id = gameSummary.id,
                player = user,
                move = move
            ).apply { Logger.info(message) }
        }
        try {
            val newGame = game.play(move)
            val newGameSummary = repository.save(
                gameSummary = gameSummary.copy(
                    turnColor = newGame.turnColor,
                    state = newGame.state,
                    winnerColor = newGame.winnerColor
                ),
                moves = newGame.moves
            )
            Logger.info("Player '${user.pseudo}' (${user.id}) plays $move in game ${newGameSummary.id}")
            return GameAndSummary(
                summary = newGameSummary,
                game = newGame
            )
        } catch (exception: dev.gleroy.ivanachess.core.InvalidMoveException) {
            Logger.debug(exception.message)
            throw InvalidMoveException(
                id = gameSummary.id,
                player = user,
                move = move
            ).apply { Logger.info(message) }
        }
    }
}
