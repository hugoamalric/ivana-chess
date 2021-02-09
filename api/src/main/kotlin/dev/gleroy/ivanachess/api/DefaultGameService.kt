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

    override fun getById(id: UUID) = repository.getById(id) ?: throw PlayException.GameIdNotFound(id).apply {
        Logger.error(message)
    }

    override fun getByToken(token: UUID) = repository.getByToken(token)
        ?: throw PlayException.GameTokenNotFound(token).apply { Logger.error(message) }

    override fun getAll(page: Int, size: Int) = repository.getAll(page, size)

    override fun play(gameInfo: GameInfo, token: UUID, move: Move): GameInfo {
        if (token != gameInfo.whiteToken && token != gameInfo.blackToken) {
            throw IllegalArgumentException("Token $token does not match white token nor black token")
        }
        val playerTriesToStealTurn = gameInfo.whiteToken == token && gameInfo.game.colorToPlay != Piece.Color.White ||
                gameInfo.blackToken == token && gameInfo.game.colorToPlay != Piece.Color.Black
        if (playerTriesToStealTurn) {
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
