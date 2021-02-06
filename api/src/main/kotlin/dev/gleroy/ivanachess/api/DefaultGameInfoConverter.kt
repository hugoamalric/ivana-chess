package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.PositionedPiece
import dev.gleroy.ivanachess.dto.GameDto
import dev.gleroy.ivanachess.dto.MoveDto
import dev.gleroy.ivanachess.dto.PieceDto
import dev.gleroy.ivanachess.dto.PositionDto
import org.springframework.stereotype.Component

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
        whiteToken = gameInfo.whiteToken,
        blackToken = gameInfo.blackToken,
        colorToPlay = gameInfo.game.colorToPlay.toDto(),
        state = gameInfo.game.state.toDto(),
        pieces = gameInfo.game.board.pieces().map { it.toDto() }.toSet(),
        moves = gameInfo.game.moves.map { MoveDto.from(it) },
        possibleMoves = gameInfo.game.nextPossibleMoves.map { MoveDto.from(it.move) }.toSet()
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
     * Convert positioned piece to DTO.
     *
     * @return Piece DTO.
     */
    private fun PositionedPiece.toDto() = PieceDto(
        color = piece.color.toDto(),
        type = piece.type(),
        pos = PositionDto.from(pos)
    )
}
