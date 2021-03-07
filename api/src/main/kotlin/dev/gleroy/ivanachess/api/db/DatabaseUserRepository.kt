@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.math.ceil

/**
 * Database implementation of user repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class DatabaseUserRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : UserRepository {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DatabaseUserRepository::class.java)

        /**
         * Alias for user table.
         */
        const val UserAlias = "u"
    }

    override fun getAll(page: Int, size: Int): Page<User> {
        require(page > 0) { "page must be strictly positive" }
        require(size > 0) { "size must be strictly positive" }
        val gameSummaries = jdbcTemplate.query(
            // @formatter:off
            """
                SELECT
                    u."${DatabaseConstants.User.IdColumnName}" AS ${DatabaseConstants.User.IdColumnName.withAlias(UserAlias)},
                    u."${DatabaseConstants.User.PseudoColumnName}" AS ${DatabaseConstants.User.PseudoColumnName.withAlias(UserAlias)},
                    u."${DatabaseConstants.User.EmailColumnName}" AS ${DatabaseConstants.User.EmailColumnName.withAlias(UserAlias)},
                    u."${DatabaseConstants.User.CreationDateColumnName}" AS ${DatabaseConstants.User.CreationDateColumnName.withAlias(UserAlias)},
                    u."${DatabaseConstants.User.BCryptPasswordColumnName}" AS ${DatabaseConstants.User.BCryptPasswordColumnName.withAlias(UserAlias)},
                    u."${DatabaseConstants.User.RoleColumnName}" AS ${DatabaseConstants.User.RoleColumnName.withAlias(UserAlias)}
                FROM "${DatabaseConstants.User.TableName}" u
                ORDER BY u."${DatabaseConstants.User.CreationDateColumnName}"
                OFFSET :offset
                LIMIT :limit
            """,
            // @formatter:on
            ComparableMapSqlParameterSource(
                mapOf(
                    "offset" to (page - 1) * size,
                    "limit" to size
                )
            ),
            UserRowMapper(UserAlias)
        )
        val totalItems = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM "${DatabaseConstants.User.TableName}"
            """,
            ComparableMapSqlParameterSource(),
            Int::class.java
        )!!
        return Page(
            content = gameSummaries,
            number = page,
            totalItems = totalItems,
            totalPages = ceil(totalItems.toDouble() / size.toDouble()).toInt()
        )
    }

    override fun existsByEmail(email: String) = existsBy(DatabaseConstants.User.EmailColumnName, email)

    override fun existsByEmail(email: String, id: UUID) = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT u.*
                FROM "${DatabaseConstants.User.TableName}" u
                WHERE u."${DatabaseConstants.User.EmailColumnName}" = :email
                    AND u."${DatabaseConstants.User.IdColumnName}" != :id
            )
        """,
        ComparableMapSqlParameterSource(
            mapOf(
                "email" to email,
                "id" to id
            )
        ),
        Boolean::class.java
    )!!

    override fun existsById(id: UUID) = existsBy(DatabaseConstants.User.IdColumnName, id)

    override fun existsByPseudo(pseudo: String) = existsBy(DatabaseConstants.User.PseudoColumnName, pseudo)

    override fun getByEmail(email: String) = getBy(DatabaseConstants.User.EmailColumnName, email)

    override fun getById(id: UUID) = getBy(DatabaseConstants.User.IdColumnName, id)

    override fun getByPseudo(pseudo: String) = getBy(DatabaseConstants.User.PseudoColumnName, pseudo)

    @Transactional
    override fun save(user: User) = if (existsById(user.id)) {
        update(user)
    } else {
        create(user)
    }

    /**
     * Save new user in database.
     *
     * @param user User.
     * @return Saved user.
     */
    private fun create(user: User): User {
        jdbcTemplate.update(
            """
                INSERT INTO "${DatabaseConstants.User.TableName}"
                (
                    "${DatabaseConstants.User.IdColumnName}",
                    "${DatabaseConstants.User.PseudoColumnName}",
                    "${DatabaseConstants.User.EmailColumnName}",
                    "${DatabaseConstants.User.CreationDateColumnName}",
                    "${DatabaseConstants.User.BCryptPasswordColumnName}",
                    "${DatabaseConstants.User.RoleColumnName}"
                ) VALUES (
                    :id,
                    :pseudo,
                    :email,
                    :creation_date,
                    :bcrypt_password,
                    :role::${DatabaseConstants.RoleType}
                )
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "id" to user.id,
                    "email" to user.email,
                    "pseudo" to user.pseudo,
                    "creation_date" to user.creationDate,
                    "bcrypt_password" to user.bcryptPassword,
                    "role" to RoleType.from(user.role).sqlValue
                )
            )
        )
        Logger.debug("User ${user.id} saved in database")
        return user
    }

    /**
     * Check if user exists by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return True if user exists, false otherwise.
     */
    private fun existsBy(columnName: String, columnValue: Any): Boolean = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT *
                FROM "${DatabaseConstants.User.TableName}"
                WHERE "$columnName" = :value
            )
        """,
        ComparableMapSqlParameterSource(mapOf("value" to columnValue)),
        Boolean::class.java
    )!!

    /**
     * Get user by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return User or null if it does not exist.
     */
    protected fun getBy(columnName: String, columnValue: Any) = jdbcTemplate.queryForNullableObject(
        // @formatter:off
        """
            SELECT 
                u."${DatabaseConstants.User.IdColumnName}" AS ${DatabaseConstants.User.IdColumnName.withAlias(UserAlias)},
                u."${DatabaseConstants.User.PseudoColumnName}" AS ${DatabaseConstants.User.PseudoColumnName.withAlias(UserAlias)},
                u."${DatabaseConstants.User.EmailColumnName}" AS ${DatabaseConstants.User.EmailColumnName.withAlias(UserAlias)},
                u."${DatabaseConstants.User.CreationDateColumnName}" AS ${DatabaseConstants.User.CreationDateColumnName.withAlias(UserAlias)},
                u."${DatabaseConstants.User.BCryptPasswordColumnName}" AS ${DatabaseConstants.User.BCryptPasswordColumnName.withAlias(UserAlias)},
                u."${DatabaseConstants.User.RoleColumnName}" AS ${DatabaseConstants.User.RoleColumnName.withAlias(UserAlias)}
            FROM "${DatabaseConstants.User.TableName}" u
            WHERE u."$columnName" = :value
        """,
        // @formatter:on
        mapOf("value" to columnValue),
        UserRowMapper(UserAlias)
    )

    /**
     * Update user.
     *
     * @param user User.
     * @return User.
     */
    private fun update(user: User): User {
        jdbcTemplate.update(
            """
                UPDATE "${DatabaseConstants.User.TableName}"
                SET "${DatabaseConstants.User.EmailColumnName}" = :email,
                    "${DatabaseConstants.User.BCryptPasswordColumnName}" = :bcrypt_password
                WHERE "${DatabaseConstants.User.IdColumnName}" = :id
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "email" to user.email,
                    "bcrypt_password" to user.bcryptPassword,
                    "id" to user.id
                )
            )
        )
        Logger.debug("User ${user.id} updated")
        return user
    }
}
