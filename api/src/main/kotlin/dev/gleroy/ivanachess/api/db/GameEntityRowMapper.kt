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
        turnColor = rs.getSqlEnumValue(
            alias = DatabaseConstants.Game.TurnColorColumnName.withAlias(alias),
            type = DatabaseConstants.Type.Color,
        )!!.color,
        state = rs.getSqlEnumValue(
            alias = DatabaseConstants.Game.StateColumnName.withAlias(alias),
            type = DatabaseConstants.Type.GameState,
        )!!.state,
        winnerColor = rs.getSqlEnumValue(
            alias = DatabaseConstants.Game.WinnerColorColumnName.withAlias(alias),
            type = DatabaseConstants.Type.Color,
        )?.color,
    )
}
