package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.User
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for user.
 *
 * @param alias Alias of user table.
 */
class UserRowMapper(
    private val alias: String
) : RowMapper<User> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = User(
        id = rs.getUuid(DatabaseConstants.Common.IdColumnName.withAlias(alias)),
        pseudo = rs.getString(DatabaseConstants.User.PseudoColumnName.withAlias(alias)),
        email = rs.getString(DatabaseConstants.User.EmailColumnName.withAlias(alias)),
        creationDate = rs.getTypedObject(DatabaseConstants.Common.CreationDateColumnName.withAlias(alias))!!,
        bcryptPassword = rs.getString(DatabaseConstants.User.BCryptPasswordColumnName.withAlias(alias)),
        role = rs.getSqlEnumValue(
            alias = DatabaseConstants.User.RoleColumnName.withAlias(alias),
            type = DatabaseConstants.Type.Role,
        )!!.role
    )
}
