@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Entity
import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.Repository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*
import kotlin.math.ceil

/**
 * Abstract database repository.
 *
 * @param E Entity type.
 */
abstract class AbstractDatabaseRepository<E : Entity> : Repository<E> {
    /**
     * JDBC template.
     */
    protected abstract val jdbcTemplate: NamedParameterJdbcTemplate

    /**
     * Entity table name.
     */
    internal abstract val tableName: String

    /**
     * ID column name.
     */
    internal abstract val idColumnName: String

    /**
     * Creation date column name.
     */
    internal abstract val creationDateColumnName: String

    /**
     * Row mapper.
     */
    internal abstract val rowMapper: RowMapper<E>

    override fun existsById(id: UUID) = existsBy(idColumnName, id)

    override fun getAll(page: Int, size: Int): Page<E> {
        checkNumberIsStrictlyPositive(page, "page")
        checkNumberIsStrictlyPositive(size, "size")
        val gameSummaries = jdbcTemplate.query(
            """
                SELECT *
                FROM "$tableName"
                ORDER BY "$creationDateColumnName"
                OFFSET :offset
                LIMIT :limit
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "offset" to (page - 1) * size,
                    "limit" to size
                )
            ),
            rowMapper
        )
        val totalItems = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM "$tableName"
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

    override fun getById(id: UUID) = getBy(idColumnName, id)

    /**
     * Check if entity exists by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return True if entity exists, false otherwise.
     */
    protected fun existsBy(columnName: String, columnValue: Any): Boolean = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT *
                FROM "$tableName"
                WHERE "$columnName" = :value
            )
        """,
        ComparableMapSqlParameterSource(mapOf("value" to columnValue)),
        Boolean::class.java
    )!!

    /**
     * Check if entity exists by specific column value ignoring one entity.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @param id ID of entity to ignore.
     * @return True if entity exists and it is not given entity, false otherwise.
     */
    protected fun existsBy(columnName: String, columnValue: Any, id: UUID): Boolean = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT *
                FROM "$tableName"
                WHERE "$columnName" = :value
                    AND "$idColumnName" != :id
            )
        """,
        ComparableMapSqlParameterSource(
            mapOf(
                "value" to columnValue,
                "id" to id
            )
        ),
        Boolean::class.java
    )!!

    /**
     * Get entity by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return Entity or null if it does not exist.
     */
    protected fun getBy(columnName: String, columnValue: Any) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "$tableName"
            WHERE "$columnName" = :value
        """,
        mapOf("value" to columnValue),
        rowMapper
    )

    /**
     * Check if given number is strictly positive.
     *
     * @param number Number to check.
     * @param parameterName Parameter name.
     * @throws IllegalArgumentException If number is not strictly positive.
     */
    @Throws(IllegalArgumentException::class)
    private fun checkNumberIsStrictlyPositive(number: Int, parameterName: String) {
        if (number < 1) {
            throw IllegalArgumentException("$parameterName must be strictly positive")
        }
    }
}
