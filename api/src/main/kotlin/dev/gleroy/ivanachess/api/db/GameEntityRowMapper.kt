package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.GameEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for game entity.
 *
 * @param alias Alias of game table.
 * @param whitePlayerRowMapper Row mapper used to map white player user.
 * @param blackPlayerRowMapper Row mapper used to map black player user.
 */
class GameEntityRowMapper(
    private val alias: String,
    private val whitePlayerRowMapper: UserRowMapper,
    private val blackPlayerRowMapper: UserRowMapper
) : RowMapper<GameEntity> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = GameEntity(
        id = rs.getUuid(DatabaseConstants.Common.IdColumnName.withAlias(alias)),
        creationDate = rs.getTypedObject(DatabaseConstants.Common.CreationDateColumnName.withAlias(alias))!!,
        whitePlayer = whitePlayerRowMapper.mapRow(rs, rowNum),
        blackPlayer = blackPlayerRowMapper.mapRow(rs, rowNum),
        turnColor = rs.getColorType(DatabaseConstants.Game.TurnColorColumnName.withAlias(alias))!!.color,
        state = rs.getGameStateType(DatabaseConstants.Game.StateColumnName.withAlias(alias)).state,
        winnerColor = rs.getColorType(DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias))?.color,
    )
}
