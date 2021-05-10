package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.PasswordResetToken
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

/**
 * Row mapper for password reset token.
 *
 * @param alias Alias of password reset token table.
 */
class PasswordResetTokenRowMapper(
    private val alias: String,
) : RowMapper<PasswordResetToken> {
    override fun mapRow(rs: ResultSet, rowNum: Int) = PasswordResetToken(
        id = rs.getUuid(DatabaseConstants.Common.IdColumnName.withAlias(alias)),
        creationDate = rs.getTypedObject(DatabaseConstants.Common.CreationDateColumnName.withAlias(alias))!!,
        userId = rs.getUuid(DatabaseConstants.PasswordResetToken.UserColumnName.withAlias(alias)),
        expirationDate = rs.getTypedObject(
            alias = DatabaseConstants.PasswordResetToken.ExpirationDateColumnName.withAlias(alias)
        )!!,
    )
}
