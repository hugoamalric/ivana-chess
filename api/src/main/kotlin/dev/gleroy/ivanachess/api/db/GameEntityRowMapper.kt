package dev.gleroy.ivanachess.api.db

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for game entity.
 */
internal class GameEntityRowMapper : RowMapper<GameEntity> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = GameEntity(
        id = rs.getUuid(DatabaseConstants.Game.IdColumnName),
        creationDate = rs.getTypedObject(DatabaseConstants.Game.CreationDateColumnName)!!,
        whiteToken = rs.getUuid(DatabaseConstants.Game.WhiteTokenColumnName),
        blackToken = rs.getUuid(DatabaseConstants.Game.BlackTokenColumnName)
    )
}
