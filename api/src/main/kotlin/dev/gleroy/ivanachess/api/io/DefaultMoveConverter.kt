package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.dto.MoveDto
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
    override fun convertToDto(move: Move) = when (move) {
        is Move.Simple -> MoveDto.Simple(
            from = posConverter.convertToDto(move.from),
            to = posConverter.convertToDto(move.to)
        )
        is Move.Promotion -> pieceConverter.convertToDto(move.promotion, move.from).let { pieceDto ->
            MoveDto.Promotion(
                from = posConverter.convertToDto(move.from),
                to = posConverter.convertToDto(move.to),
                promotionColor = pieceDto.color,
                promotionType = pieceDto.type
            )
        }
    }

    override fun convertToMove(dto: MoveDto) = when (dto) {
        is MoveDto.Simple -> Move.Simple(
            from = posConverter.convertToPosition(dto.from),
            to = posConverter.convertToPosition(dto.to)
        )
        is MoveDto.Promotion -> Move.Promotion(
            from = posConverter.convertToPosition(dto.from),
            to = posConverter.convertToPosition(dto.to),
            promotion = pieceConverter.convertToPiece(dto.promotionColor, dto.promotionType)
        )
    }
}
