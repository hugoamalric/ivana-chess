package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.InvalidMoveException
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

    override fun create(): GameSummary {
        val gameInfo = repository.save()
        Logger.info("New game (${gameInfo.id}) created")
        return gameInfo
    }

    override fun getSummaryById(id: UUID) = repository.getById(id) ?: throw GameIdNotFoundException(id).apply {
        Logger.error(message)
    }

    override fun getSummaryByToken(token: UUID) = repository.getByToken(token)
        ?: throw GameTokenNotFoundException(token).apply { Logger.error(message) }

    override fun getAllSummaries(page: Int, size: Int) = repository.getAll(page, size)

    override fun getGameById(id: UUID): Game {
        if (!repository.exists(id)) {
            throw GameIdNotFoundException(id).apply { Logger.error(message) }
        }
        return Game(repository.getMoves(id))
    }

    override fun play(token: UUID, move: Move): GameSummary {
        val gameSummary = getSummaryByToken(token)
        val playerTriesToStealTurn = gameSummary.whiteToken == token &&
                gameSummary.turnColor != Piece.Color.White ||
                gameSummary.blackToken == token &&
                gameSummary.turnColor != Piece.Color.Black
        if (playerTriesToStealTurn) {
            throw PlayException.InvalidPlayer(gameSummary.id, token, gameSummary.turnColor.opponent()).apply {
                Logger.warn(message)
            }
        }
        val game = Game(repository.getMoves(gameSummary.id))
        if (game.board.pieceAt(move.from)?.color != gameSummary.turnColor) {
            throw PlayException.InvalidMove(
                id = gameSummary.id,
                token = token,
                color = gameSummary.turnColor,
                move = move,
                cause = InvalidMoveException("Piece at ${move.from} should be ${gameSummary.turnColor}")
            ).apply { Logger.error(message) }
        }
        try {
            val newGame = game.play(move)
            val newGameSummary = gameSummary.copy(
                turnColor = newGame.turnColor,
                state = newGame.state
            )
            return repository.save(newGameSummary).apply {
                Logger.info("Player $token (${gameSummary.turnColor}) plays $move in game ${gameSummary.id}")
            }
        } catch (exception: InvalidMoveException) {
            throw PlayException.InvalidMove(
                id = gameSummary.id,
                token = token,
                color = gameSummary.turnColor,
                move = move,
                cause = exception
            ).apply { Logger.error(message) }
        }
    }
}
