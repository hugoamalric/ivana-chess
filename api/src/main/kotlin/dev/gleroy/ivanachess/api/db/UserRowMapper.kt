package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.User
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for user.
 */
internal class UserRowMapper : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = User(
        id = rs.getUuid(DatabaseConstants.User.IdColumnName),
        pseudo = rs.getString(DatabaseConstants.User.PseudoColumnName),
        creationDate = rs.getTypedObject(DatabaseConstants.User.CreationDateColumnName)!!,
        bcryptPassword = rs.getString(DatabaseConstants.User.BCryptPasswordColumnName)
    )
}
