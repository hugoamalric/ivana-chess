package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Match
import dev.gleroy.ivanachess.io.*
import org.springframework.stereotype.Component

/**
 * Default implementation of match converter.
 *
 * @param gameConverter Game converter.
 * @param moveConverter Move converter.
 * @param pieceConverter Piece converter.
 */
@Component
class DefaultMatchConverter(
    private val gameConverter: GameConverter = DefaultGameConverter(),
    private val moveConverter: MoveConverter = DefaultMoveConverter(),
    private val pieceConverter: PieceConverter = DefaultPieceConverter(),
) : MatchConverter {
    override fun convertToRepresentation(item: Match): GameRepresentation.Complete {
        val summaryRepresentation = gameConverter.convertToRepresentation(item.entity)
        return GameRepresentation.Complete(
            id = summaryRepresentation.id,
            creationDate = summaryRepresentation.creationDate,
            whitePlayer = summaryRepresentation.whitePlayer,
            blackPlayer = summaryRepresentation.blackPlayer,
            turnColor = summaryRepresentation.turnColor,
            state = summaryRepresentation.state,
            winnerColor = summaryRepresentation.winnerColor,
            pieces = item.game.board.pieces().map { pieceConverter.convertToRepresentation(it) }.toSet(),
            moves = item.game.moves.map { moveConverter.convertToRepresentation(it) },
            possibleMoves = item.game.nextPossibleMoves.map { moveConverter.convertToRepresentation(it.move) }.toSet(),
        )
    }
}
