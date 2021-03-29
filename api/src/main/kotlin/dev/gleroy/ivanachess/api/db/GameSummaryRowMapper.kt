package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.game.GameSummary
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for game summary.
 *
 * @param alias Alias of game table.
 * @param whitePlayerRowMapper Row mapper used to map white player user.
 * @param blackPlayerRowMapper Row mapper used to map black player user.
 */
internal class GameSummaryRowMapper(
    private val alias: String,
    private val whitePlayerRowMapper: UserRowMapper,
    private val blackPlayerRowMapper: UserRowMapper
) : RowMapper<GameSummary> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = GameSummary(
        id = rs.getUuid(DatabaseConstants.Game.IdColumnName.withAlias(alias)),
        creationDate = rs.getTypedObject(DatabaseConstants.Game.CreationDateColumnName.withAlias(alias))!!,
        whitePlayer = whitePlayerRowMapper.mapRow(rs, rowNum),
        blackPlayer = blackPlayerRowMapper.mapRow(rs, rowNum),
        turnColor = rs.getColorType(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias))!!.color,
        state = rs.getGameStateType(DatabaseConstants.Game.StateColumnName.withAlias(alias)).state,
        winnerColor = rs.getColorType(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias))?.color,
    )
}
