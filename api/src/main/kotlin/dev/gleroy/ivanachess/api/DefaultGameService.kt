package dev.gleroy.ivanachess.api

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

    override fun create(): GameInfo {
        val gameInfo = repository.create()
        Logger.info("New game (${gameInfo.id}) created")
        return gameInfo
    }

    override fun play(token: UUID, move: Move): GameInfo {
        val gameInfo = repository.get(token) ?: throw PlayException.GameNotFound(token).apply { Logger.error(message) }
        val playerTriesToSteal = gameInfo.whiteToken == token && gameInfo.game.colorToPlay != Piece.Color.White ||
                gameInfo.blackToken == token && gameInfo.game.colorToPlay != Piece.Color.Black
        if (playerTriesToSteal) {
            throw PlayException.InvalidPlayer(gameInfo.id, token, gameInfo.game.colorToPlay.opponent()).apply {
                Logger.warn(message)
            }
        }
        try {
            val game = gameInfo.game.play(move)
            return repository.update(gameInfo.copy(game = game)).apply {
                Logger.info("Player $token (${gameInfo.game.colorToPlay}) plays $move in game ${gameInfo.id}")
            }
        } catch (exception: InvalidMoveException) {
            throw PlayException.InvalidMove(
                id = gameInfo.id,
                token = token,
                color = gameInfo.game.colorToPlay,
                move = move,
                cause = exception
            ).apply { Logger.error(message) }
        }
    }
}
