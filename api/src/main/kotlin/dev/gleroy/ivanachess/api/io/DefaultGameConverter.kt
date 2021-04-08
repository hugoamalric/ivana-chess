package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.Match
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.io.GameRepresentation
import org.springframework.stereotype.Component

/**
 * Default implementation of game entity converter.
 *
 * @param moveConverter Move converter.
 * @param pieceConverter Piece converter.
 * @param userConverter User converter.
 */
@Component
class DefaultGameConverter(
    private val moveConverter: MoveConverter = DefaultMoveConverter(),
    private val pieceConverter: PieceConverter = DefaultPieceConverter(),
    private val userConverter: UserConverter = DefaultUserConverter()
) : GameConverter {
    override fun convertToSummaryRepresentation(gameEntity: GameEntity) = GameRepresentation.Summary(
        id = gameEntity.id,
        whitePlayer = userConverter.convertToRepresentation(gameEntity.whitePlayer),
        blackPlayer = userConverter.convertToRepresentation(gameEntity.blackPlayer),
        turnColor = pieceConverter.convertColorToRepresentation(gameEntity.turnColor),
        state = gameEntity.state.toRepresentation(),
        winnerColor = gameEntity.winnerColor?.let { pieceConverter.convertColorToRepresentation(it) }
    )

    override fun convertToCompleteRepresentation(match: Match) = GameRepresentation.Complete(
        id = match.entity.id,
        whitePlayer = userConverter.convertToRepresentation(match.entity.whitePlayer),
        blackPlayer = userConverter.convertToRepresentation(match.entity.blackPlayer),
        turnColor = pieceConverter.convertColorToRepresentation(match.entity.turnColor),
        state = match.entity.state.toRepresentation(),
        winnerColor = match.entity.winnerColor?.let { pieceConverter.convertColorToRepresentation(it) },
        pieces = match.game.board.pieces().map { pieceConverter.convertToRepresentation(it.piece, it.pos) }.toSet(),
        moves = match.game.moves.map { moveConverter.convertToRepresentation(it) },
        possibleMoves = match.game.nextPossibleMoves.map { moveConverter.convertToRepresentation(it.move) }.toSet()
    )

    /**
     * Convert game state to its representation.
     *
     * @return Representation of game state.
     */
    private fun Game.State.toRepresentation() = when (this) {
        Game.State.InGame -> GameRepresentation.State.InGame
        Game.State.Checkmate -> GameRepresentation.State.Checkmate
        Game.State.Stalemate -> GameRepresentation.State.Stalemate
    }
}
