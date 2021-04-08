package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.game.Piece
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for move.
 *
 * @param alias Alias of move table.
 */
internal class MoveRowMapper(
    private val alias: String
) : RowMapper<Move> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Move {
        val from = rs.getPosition(DatabaseConstants.Move.FromColumnName.withAlias(alias))
        val to = rs.getPosition(DatabaseConstants.Move.ToColumnName.withAlias(alias))
        val promotion = rs.getNullablePieceType(DatabaseConstants.Move.PromotionColumnName.withAlias(alias))
        return if (promotion == null) {
            Move.Simple(
                from = from,
                to = to
            )
        } else {
            val order = rs.getInt(DatabaseConstants.Move.OrderColumnName.withAlias(alias))
            val color = if (order % 2 == 0) Piece.Color.Black else Piece.Color.White
            Move.Promotion(
                from = from,
                to = to,
                promotion = promotion.instantiatePiece(color)
            )
        }
    }
}
