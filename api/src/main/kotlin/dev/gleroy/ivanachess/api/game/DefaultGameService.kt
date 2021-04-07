package dev.gleroy.ivanachess.api.game

import dev.gleroy.ivanachess.api.PageOptions
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

    override fun create(whitePlayer: User, blackPlayer: User): Match {
        if (whitePlayer.id == blackPlayer.id) {
            throw PlayersAreSameUserException()
        }
        val gameEntity = repository.save(
            entity = GameEntity(
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer
            ),
        )
        Logger.info("New game '${whitePlayer.pseudo}' vs. '${blackPlayer.pseudo}' (${gameEntity.id}) created")
        return Match(gameEntity)
    }

    override fun getEntityById(id: UUID) = repository.fetchById(id) ?: throw GameNotFoundException(id).apply {
        Logger.debug(message)
    }

    override fun getGameById(id: UUID): Game {
        if (!repository.existsWithId(id)) {
            throw GameNotFoundException(id).apply { Logger.debug(message) }
        }
        return Game(repository.fetchMoves(id))
    }

    override fun getPage(page: Int, size: Int) = repository.fetchPage(PageOptions(page, size))

    @Transactional
    override fun play(id: UUID, user: User, move: Move): Match {
        val gameEntity = getEntityById(id)
        if (user != gameEntity.whitePlayer && user != gameEntity.blackPlayer) {
            throw NotAllowedPlayerException(id, user).apply { Logger.warn(message) }
        }
        val playerTriesToStealTurn = gameEntity.whitePlayer == user &&
                gameEntity.turnColor != Piece.Color.White ||
                gameEntity.blackPlayer == user &&
                gameEntity.turnColor != Piece.Color.Black
        if (playerTriesToStealTurn) {
            throw InvalidPlayerException(gameEntity.id, user).apply { Logger.info(message) }
        }
        val game = Game(repository.fetchMoves(gameEntity.id))
        if (game.board.pieceAt(move.from)?.color != gameEntity.turnColor) {
            throw InvalidMoveException(
                id = gameEntity.id,
                player = user,
                move = move
            ).apply { Logger.info(message) }
        }
        try {
            val updatedGame = game.play(move)
            val updatedGameEntity = repository.save(
                entity = gameEntity.copy(
                    turnColor = updatedGame.turnColor,
                    state = updatedGame.state,
                    winnerColor = updatedGame.winnerColor
                ),
            )
            repository.saveMoves(updatedGameEntity.id, updatedGame.moves)
            Logger.info("Player '${user.pseudo}' (${user.id}) plays $move in game ${updatedGameEntity.id}")
            return Match(
                entity = updatedGameEntity,
                game = updatedGame
            )
        } catch (exception: dev.gleroy.ivanachess.core.InvalidMoveException) {
            Logger.debug(exception.message)
            throw InvalidMoveException(
                id = gameEntity.id,
                player = user,
                move = move
            ).apply { Logger.info(message) }
        }
    }
}
