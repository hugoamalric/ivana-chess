package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.io.MoveConverter
import dev.gleroy.ivanachess.io.MoveRepresentation
import dev.gleroy.ivanachess.io.PieceConverter
import dev.gleroy.ivanachess.io.PositionConverter
import org.springframework.stereotype.Component

/**
 * Default implementation of move converter.
 *
 * @param posConverter Position converter.
 * @param pieceConverter Piece converter.
 */
@Component
class DefaultMoveConverter(
    private val posConverter: PositionConverter = DefaultPositionConverter(),
    private val pieceConverter: PieceConverter = DefaultPieceConverter()
) : MoveConverter {
    override fun convertToRepresentation(move: Move) = when (move) {
        is Move.Simple -> MoveRepresentation.Simple(
            from = posConverter.convertToRepresentation(move.from),
            to = posConverter.convertToRepresentation(move.to)
        )
        is Move.Promotion -> pieceConverter.convertToRepresentation(move.promotion, move.from)
            .let { pieceRepresentation ->
                MoveRepresentation.Promotion(
                    from = posConverter.convertToRepresentation(move.from),
                    to = posConverter.convertToRepresentation(move.to),
                    promotionColor = pieceRepresentation.color,
                    promotionType = pieceRepresentation.type
                )
            }
    }

    override fun convertToMove(representation: MoveRepresentation) = when (representation) {
        is MoveRepresentation.Simple -> Move.Simple(
            from = posConverter.convertToPosition(representation.from),
            to = posConverter.convertToPosition(representation.to)
        )
        is MoveRepresentation.Promotion -> Move.Promotion(
            from = posConverter.convertToPosition(representation.from),
            to = posConverter.convertToPosition(representation.to),
            promotion = pieceConverter.convertToPiece(representation.promotionColor, representation.promotionType)
        )
    }
}
