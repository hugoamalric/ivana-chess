package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.user.User
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for user.
 *
 * @param alias Alias of user table.
 */
internal class UserRowMapper(
    private val alias: String
) : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = User(
        id = rs.getUuid(DatabaseConstants.User.IdColumnName.withAlias(alias)),
        pseudo = rs.getString(DatabaseConstants.User.PseudoColumnName.withAlias(alias)),
        email = rs.getString(DatabaseConstants.User.EmailColumnName.withAlias(alias)),
        creationDate = rs.getTypedObject(DatabaseConstants.User.CreationDateColumnName.withAlias(alias))!!,
        bcryptPassword = rs.getString(DatabaseConstants.User.BCryptPasswordColumnName.withAlias(alias)),
        role = rs.getRoleType(DatabaseConstants.User.RoleColumnName.withAlias(alias)).role
    )
}
