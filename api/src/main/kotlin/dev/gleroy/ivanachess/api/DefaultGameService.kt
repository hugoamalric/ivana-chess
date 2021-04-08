package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.game.Piece
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
    override val repository: GameRepository
) : AbstractEntityService<GameEntity>(), GameService {
    override fun create(whitePlayer: User, blackPlayer: User): Match {
        if (whitePlayer.id == blackPlayer.id) {
            throw PlayersAreSameUserException().apply { logger.debug(message) }
        }
        val gameEntity = repository.save(
            entity = GameEntity(
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer
            ),
        )
        logger.info("New game '${whitePlayer.pseudo}' vs. '${blackPlayer.pseudo}' (${gameEntity.id}) created")
        return Match(gameEntity)
    }

    override fun getGameById(id: UUID): Game {
        if (!repository.existsWithId(id)) {
            throw EntityNotFoundException("Entity $id does not exist").apply { logger.debug(message) }
        }
        return Game(repository.fetchMoves(id))
    }

    @Transactional
    override fun play(id: UUID, user: User, move: Move): Match {
        val gameEntity = getById(id)
        if (user != gameEntity.whitePlayer && user != gameEntity.blackPlayer) {
            throw NotAllowedPlayerException(id, user).apply { logger.debug(message) }
        }
        val playerTriesToStealTurn = gameEntity.whitePlayer == user &&
                gameEntity.turnColor != Piece.Color.White ||
                gameEntity.blackPlayer == user &&
                gameEntity.turnColor != Piece.Color.Black
        if (playerTriesToStealTurn) {
            throw InvalidPlayerException(gameEntity.id, user).apply { logger.debug(message) }
        }
        val game = Game(repository.fetchMoves(gameEntity.id))
        if (game.board.pieceAt(move.from)?.color != gameEntity.turnColor) {
            throw InvalidMoveException(
                id = gameEntity.id,
                player = user,
                move = move
            ).apply { logger.debug(message) }
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
            logger.info("Player '${user.pseudo}' (${user.id}) plays $move in game ${updatedGameEntity.id}")
            return Match(
                entity = updatedGameEntity,
                game = updatedGame
            )
        } catch (exception: dev.gleroy.ivanachess.game.InvalidMoveException) {
            logger.debug(exception.message)
            throw InvalidMoveException(
                id = gameEntity.id,
                player = user,
                move = move
            ).apply { logger.debug(message) }
        }
    }
}
