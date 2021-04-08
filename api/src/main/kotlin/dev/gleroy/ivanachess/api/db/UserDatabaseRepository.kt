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
) : AbstractDatabaseSearchableEntityRepository<User>(), UserRepository {
    internal companion object {
        /**
         * Create set of table columns used in SELECT statement.
         *
         * @param alias Table alias.
         * @return Set of columns.
         */
        internal fun createSelectColumns(alias: String) = setOf(
            SelectColumn(DatabaseConstants.Common.IdColumnName, alias),
            SelectColumn(DatabaseConstants.User.PseudoColumnName, alias),
            SelectColumn(DatabaseConstants.User.EmailColumnName, alias),
            SelectColumn(DatabaseConstants.Common.CreationDateColumnName, alias),
            SelectColumn(DatabaseConstants.User.BCryptPasswordColumnName, alias),
            SelectColumn(DatabaseConstants.User.RoleColumnName, alias),
        )

        /**
         * Alias for user table.
         */
        private const val UserTableAlias = "u"

        /**
         * Pseudo column.
         */
        private val PseudoColumn = UpdateColumn(DatabaseConstants.User.PseudoColumnName)

        /**
         * Email column.
         */
        private val EmailColumn = UpdateColumn(DatabaseConstants.User.EmailColumnName)

        /**
         * BCrypt password column.
         */
        private val BCryptPasswordColumn = UpdateColumn(DatabaseConstants.User.BCryptPasswordColumnName)

        /**
         * Role column.
         */
        private val RoleColumn = UpdateColumn(DatabaseConstants.User.RoleColumnName, DatabaseConstants.Type.Role)
    }

    override val tableName get() = DatabaseConstants.User.TableName

    final override val tableAlias get() = UserTableAlias

    override val selectColumns get() = createSelectColumns(tableAlias)

    override val selectJoins get() = emptyList<Join>()

    override val sortableColumns: Map<SortableEntityField<User>, SelectColumn>
        get() = mapOf(
            CommonSortableEntityField.Id to SelectColumn(DatabaseConstants.Common.IdColumnName, tableAlias),
            CommonSortableEntityField.CreationDate to SelectColumn(
                name = DatabaseConstants.Common.CreationDateColumnName,
                tableAlias = tableAlias
            ),
            UserSortableField.Email to SelectColumn(DatabaseConstants.User.EmailColumnName, tableAlias),
            UserSortableField.Pseudo to SelectColumn(DatabaseConstants.User.PseudoColumnName, tableAlias),
        )

    override val searchableColumns: Map<SearchableEntityField<User>, SelectColumn>
        get() = mapOf(
            UserSearchableField.Email to SelectColumn(DatabaseConstants.User.EmailColumnName, tableAlias),
            UserSearchableField.Pseudo to SelectColumn(DatabaseConstants.User.PseudoColumnName, tableAlias),
        )

    override val insertColumns: Set<UpdateColumn>
        get() = setOf(
            PseudoColumn,
            EmailColumn,
            BCryptPasswordColumn,
            RoleColumn
        )

    override val updateColumns: Set<UpdateColumn>
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

    override fun insertParams(entity: User) = mapOf(
        EmailColumn.name to entity.email,
        PseudoColumn.name to entity.pseudo,
        BCryptPasswordColumn.name to entity.bcryptPassword,
        RoleColumn.name to RoleType.from(entity.role).sqlValue,
    )

    override fun updateParams(entity: User) = mapOf(
        EmailColumn.name to entity.email,
        BCryptPasswordColumn.name to entity.bcryptPassword,
        RoleColumn.name to RoleType.from(entity.role).sqlValue,
    )
}
