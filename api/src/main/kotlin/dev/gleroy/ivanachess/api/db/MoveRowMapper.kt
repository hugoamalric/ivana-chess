package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for move.
 */
internal class MoveRowMapper : RowMapper<Move> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Move {
        val from = rs.getPosition(DatabaseConstants.Move.FromColumnName)
        val to = rs.getPosition(DatabaseConstants.Move.ToColumnName)
        val promotion = rs.getNullablePieceType(DatabaseConstants.Move.PromotionColumnName)
        return if (promotion == null) {
            Move.Simple(
                from = from,
                to = to
            )
        } else {
            val order = rs.getInt(DatabaseConstants.Move.OrderColumnName)
            val color = if (order % 2 == 0) Piece.Color.Black else Piece.Color.White
            Move.Promotion(
                from = from,
                to = to,
                promotion = promotion.instantiatePiece(color)
            )
        }
    }
}
