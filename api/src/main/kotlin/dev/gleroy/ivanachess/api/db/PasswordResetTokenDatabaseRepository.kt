@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Database implementation of password reset token repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class PasswordResetTokenDatabaseRepository(
    override val jdbcTemplate: NamedParameterJdbcTemplate
) : AbstractEntityDatabaseRepository<PasswordResetToken>(), PasswordResetTokenRepository {
    internal companion object {
        /**
         * Create set of table columns used in SELECT statement.
         *
         * @param alias Table alias.
         * @return Set of columns.
         */
        internal fun createSelectColumns(alias: String) = setOf(
            TableColumn.Select(DatabaseConstants.Common.IdColumnName, alias),
            TableColumn.Select(DatabaseConstants.Common.CreationDateColumnName, alias),
            TableColumn.Select(DatabaseConstants.PasswordResetToken.UserColumnName, alias),
            TableColumn.Select(DatabaseConstants.PasswordResetToken.ExpirationDateColumnName, alias),
        )

        /**
         * User column.
         */
        private val UserColumn = TableColumn.Update(DatabaseConstants.PasswordResetToken.UserColumnName)

        /**
         * Expiration date column.
         */
        private val ExpirationDateColumn = TableColumn.Update(
            name = DatabaseConstants.PasswordResetToken.ExpirationDateColumnName
        )
    }

    override val tableName get() = DatabaseConstants.PasswordResetToken.TableName

    final override val tableAlias get() = "prt"

    override val selectColumns
        get() = createSelectColumns(tableAlias)

    override val selectJoins
        get() = emptyList<Join>()

    override val sortableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            CommonEntityField.Id to TableColumn.Select(DatabaseConstants.Common.IdColumnName, tableAlias),
            CommonEntityField.CreationDate to TableColumn.Select(
                name = DatabaseConstants.Common.CreationDateColumnName,
                tableAlias = tableAlias,
            ),
            PasswordResetTokenField.User to TableColumn.Select(
                name = DatabaseConstants.PasswordResetToken.UserColumnName,
                tableAlias = tableAlias,
            ),
            PasswordResetTokenField.ExpirationDate to TableColumn.Select(
                name = DatabaseConstants.PasswordResetToken.ExpirationDateColumnName,
                tableAlias = tableAlias,
            ),
        )

    override val filterableColumns: Map<ItemField, TableColumn.Select>
        get() = emptyMap()

    override val insertColumns: Set<TableColumn.Update>
        get() = setOf(UserColumn, ExpirationDateColumn)

    override val columnsToUpdate: Set<TableColumn.Update>
        get() = emptySet()

    override val rowMapper = PasswordResetTokenRowMapper(tableAlias)

    @Transactional
    override fun save(entity: PasswordResetToken): PasswordResetToken {
        deleteUserResetTokens(entity.userId)
        val sql = insertStatement
        val params = insertParameters(entity) +
                mapOf(
                    IdColumn.name to entity.id,
                    CreationDateColumn.name to entity.creationDate,
                )
        logStatement(sql, params)
        jdbcTemplate.update(sql, params)
        return entity
    }

    override fun buildUpdateStatement(tableName: String, columns: Set<TableColumn.Update>) = ""

    override fun insertParameters(item: PasswordResetToken) = mapOf(
        UserColumn.name to item.userId,
        ExpirationDateColumn.name to item.expirationDate,
    )

    override fun updateParameters(entity: PasswordResetToken) = emptyMap<String, Any>()

    /**
     * Delete password reset token of user.
     *
     * @param userId User ID.
     */
    private fun deleteUserResetTokens(userId: UUID) {
        val sql = "DELETE FROM \"$tableName\" WHERE \"${DatabaseConstants.PasswordResetToken.UserColumnName}\" = :id"
        val params = mapOf("id" to userId)
        logStatement(sql, params)
        jdbcTemplate.update(sql, params)
    }
}
