package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameSummary
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for game summary.
 */
internal class GameSummaryRowMapper : RowMapper<GameSummary> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = GameSummary(
        id = rs.getUuid(DatabaseConstants.Game.IdColumnName),
        creationDate = rs.getTypedObject(DatabaseConstants.Game.CreationDateColumnName)!!,
        whiteToken = rs.getUuid(DatabaseConstants.Game.WhiteTokenColumnName),
        blackToken = rs.getUuid(DatabaseConstants.Game.BlackTokenColumnName),
        turnColor = rs.getColorType(DatabaseConstants.Game.TurnColorColumnName).color,
        state = rs.getGameStateType(DatabaseConstants.Game.StateColumnName).state
    )
}
