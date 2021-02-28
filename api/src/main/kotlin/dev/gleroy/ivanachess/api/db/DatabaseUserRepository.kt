@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.User
import dev.gleroy.ivanachess.api.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * Database implementation of user repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class DatabaseUserRepository(
    override val jdbcTemplate: NamedParameterJdbcTemplate
) : AbstractDatabaseRepository<User>(), UserRepository {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DatabaseUserRepository::class.java)
    }

    override val tableName = DatabaseConstants.User.TableName

    override val idColumnName = DatabaseConstants.User.IdColumnName

    override val creationDateColumnName = DatabaseConstants.User.CreationDateColumnName

    override val rowMapper = UserRowMapper()

    override fun existsByPseudo(pseudo: String) = existsBy(DatabaseConstants.User.PseudoColumnName, pseudo)

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
                INSERT INTO "$tableName"
                (
                    "$idColumnName",
                    "${DatabaseConstants.User.PseudoColumnName}",
                    "$creationDateColumnName",
                    "${DatabaseConstants.User.BCryptPasswordColumnName}",
                    "${DatabaseConstants.User.RoleColumnName}"
                ) VALUES (
                    :id,
                    :pseudo,
                    :creation_date,
                    :bcrypt_password,
                    :role::${DatabaseConstants.RoleType}
                )
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "id" to user.id,
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
     * Update user.
     *
     * @param user User.
     * @return User.
     */
    private fun update(user: User): User {
        jdbcTemplate.update(
            """
                UPDATE "$tableName"
                SET "${DatabaseConstants.User.BCryptPasswordColumnName}" = :bcrypt_password
                WHERE "$idColumnName" = :id
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "bcrypt_password" to user.bcryptPassword,
                    "id" to user.id
                )
            )
        )
        Logger.debug("User ${user.id} updated")
        return user
    }
}
