@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Database implementation of user repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class UserDatabaseRepository(
    override val jdbcTemplate: NamedParameterJdbcTemplate
) : AbstractSearchableEntityDatabaseRepository<User>(), UserRepository {
    internal companion object {
        /**
         * Create set of table columns used in SELECT statement.
         *
         * @param alias Table alias.
         * @return Set of columns.
         */
        internal fun createSelectColumns(alias: String) = setOf(
            TableColumn.Select(DatabaseConstants.Common.IdColumnName, alias),
            TableColumn.Select(DatabaseConstants.User.PseudoColumnName, alias),
            TableColumn.Select(DatabaseConstants.User.EmailColumnName, alias),
            TableColumn.Select(DatabaseConstants.Common.CreationDateColumnName, alias),
            TableColumn.Select(DatabaseConstants.User.BCryptPasswordColumnName, alias),
            TableColumn.Select(DatabaseConstants.User.RoleColumnName, alias, DatabaseConstants.Type.Role.label),
        )

        /**
         * Alias for user table.
         */
        private const val UserTableAlias = "u"

        /**
         * Pseudo column.
         */
        private val PseudoColumn = TableColumn.Update(DatabaseConstants.User.PseudoColumnName)

        /**
         * Email column.
         */
        private val EmailColumn = TableColumn.Update(DatabaseConstants.User.EmailColumnName)

        /**
         * BCrypt password column.
         */
        private val BCryptPasswordColumn = TableColumn.Update(DatabaseConstants.User.BCryptPasswordColumnName)

        /**
         * Role column.
         */
        private val RoleColumn =
            TableColumn.Update(DatabaseConstants.User.RoleColumnName, DatabaseConstants.Type.Role.label)
    }

    override val tableName get() = DatabaseConstants.User.TableName

    final override val tableAlias get() = UserTableAlias

    override val selectColumns get() = createSelectColumns(tableAlias)

    override val selectJoins get() = emptyList<Join>()

    override val sortableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            CommonEntityField.Id to TableColumn.Select(DatabaseConstants.Common.IdColumnName, tableAlias),
            CommonEntityField.CreationDate to TableColumn.Select(
                name = DatabaseConstants.Common.CreationDateColumnName,
                tableAlias = tableAlias
            ),
            UserField.Email to TableColumn.Select(DatabaseConstants.User.EmailColumnName, tableAlias),
            UserField.Pseudo to TableColumn.Select(DatabaseConstants.User.PseudoColumnName, tableAlias),
        )

    override val filterableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            UserField.Email to TableColumn.Select(DatabaseConstants.User.EmailColumnName, tableAlias),
            UserField.Pseudo to TableColumn.Select(DatabaseConstants.User.PseudoColumnName, tableAlias),
            UserField.Role to TableColumn.Select(
                name = DatabaseConstants.User.RoleColumnName,
                tableAlias = tableAlias,
                type = DatabaseConstants.Type.Role.label
            ),
        )

    override val searchableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            UserField.Email to TableColumn.Select(DatabaseConstants.User.EmailColumnName, tableAlias),
            UserField.Pseudo to TableColumn.Select(DatabaseConstants.User.PseudoColumnName, tableAlias),
        )

    override val insertColumns: Set<TableColumn.Update>
        get() = setOf(
            PseudoColumn,
            EmailColumn,
            BCryptPasswordColumn,
            RoleColumn
        )

    override val columnsToUpdate: Set<TableColumn.Update>
        get() = setOf(
            EmailColumn,
            BCryptPasswordColumn,
            RoleColumn
        )

    override val rowMapper = UserRowMapper(tableAlias)

    override fun existsWithEmail(email: String, excluding: Set<UUID>) =
        existsWith(DatabaseConstants.User.EmailColumnName, email, excluding)

    override fun existsWithPseudo(pseudo: String, excluding: Set<UUID>) =
        existsWith(DatabaseConstants.User.PseudoColumnName, pseudo, excluding)

    override fun fetchByEmail(email: String) = fetchBy(DatabaseConstants.User.EmailColumnName, email)

    override fun fetchByPseudo(pseudo: String) = fetchBy(DatabaseConstants.User.PseudoColumnName, pseudo)

    override fun insertParameters(item: User) = mapOf(
        EmailColumn.name to item.email,
        PseudoColumn.name to item.pseudo,
        BCryptPasswordColumn.name to item.bcryptPassword,
        RoleColumn.name to RoleSqlEnumValue.from(item.role).label,
    )

    override fun updateParameters(entity: User) = mapOf(
        EmailColumn.name to entity.email,
        BCryptPasswordColumn.name to entity.bcryptPassword,
        RoleColumn.name to RoleSqlEnumValue.from(entity.role).label,
    )
}
