package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.*
import org.springframework.stereotype.Component
import java.net.URI

/**
 * Default implementation of game information converter.
 *
 * @param props Properties.
 */
@Component
class DefaultGameInfoConverter(
    private val props: Properties
) : GameInfoConverter {
    override fun convert(gameInfo: GameInfo) = GameDto(
        id = gameInfo.id,
        whiteUrl = URI("${props.webapp.baseUrl}${props.webapp.gamePath}/${gameInfo.whiteToken}"),
        blackUrl = URI("${props.webapp.baseUrl}${props.webapp.gamePath}/${gameInfo.blackToken}"),
        colorToPlay = gameInfo.game.colorToPlay.toDto(),
        state = gameInfo.game.state.toDto(),
        pieces = gameInfo.game.board.pieces().map { it.toDto() }.toSet(),
        moves = gameInfo.game.moves.map { it.toDto() },
        possibleMoves = gameInfo.game.nextPossibleMoves.map { it.move.toDto() }.toSet()
    )

    /**
     * Convert game state to DTO.
     *
     * @return Game state DTO.
     */
    private fun Game.State.toDto() = when (this) {
        Game.State.InGame -> GameDto.State.InGame
        Game.State.Checkmate -> GameDto.State.Checkmate
        Game.State.Draw -> GameDto.State.Draw
    }

    /**
     * Convert move to DTO.
     *
     * @return Move DTO.
     */
    private fun Move.toDto() = MoveDto(
        from = from.toDto(),
        to = to.toDto()
    )

    /**
     * Get type of this piece.
     *
     * @return Type of this piece.
     */
    private fun Piece.type() = when (this) {
        is Piece.Pawn -> PieceDto.Type.Pawn
        is Piece.Rook -> PieceDto.Type.Rook
        is Piece.Knight -> PieceDto.Type.Knight
        is Piece.Bishop -> PieceDto.Type.Bishop
        is Piece.Queen -> PieceDto.Type.Queen
        is Piece.King -> PieceDto.Type.King
    }

    /**
     * Convert piece color to DTO.
     *
     * @return Color.
     */
    private fun Piece.Color.toDto() = when (this) {
        Piece.Color.White -> PieceDto.Color.White
        Piece.Color.Black -> PieceDto.Color.Black
    }

    /**
     * Convert position to DTO.
     *
     * @return Position DTO.
     */
    private fun Position.toDto() = PositionDto(
        col = col,
        row = row
    )

    /**
     * Convert positioned piece to DTO.
     *
     * @return Piece DTO.
     */
    private fun PositionedPiece.toDto() = PieceDto(
        color = piece.color.toDto(),
        type = piece.type(),
        pos = pos.toDto()
    )
}
