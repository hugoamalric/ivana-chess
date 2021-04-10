package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Position
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.sql.ResultSet
import java.util.*

/**
 * Fetch nullable object from database.
 *
 * @param sql SQL statement.
 * @param params SQL statement parameters.
 * @param mapper Row mapper.
 * @return Object or null if result is empty.
 */
internal fun <T> NamedParameterJdbcTemplate.queryForNullableObject(
    sql: String,
    params: Map<String, *>,
    mapper: RowMapper<T>
) = try {
    queryForObject(sql, params, mapper)
} catch (exception: EmptyResultDataAccessException) {
    null
}

/**
 * Get SQL enumeration value.
 *
 * @param alias Column alias.
 * @return SQL enumeration value or null if column value is NULL.
 */
internal fun <V> ResultSet.getSqlEnumValue(alias: String, type: SqlEnumType<V>) where V : SqlEnumValue, V : Enum<V> =
    getString(alias)?.let { type.getValue(it) }

/**
 * Get position.
 *
 * @param alias Column alias.
 * @return Position.
 */
internal fun ResultSet.getPosition(alias: String) = Position.fromCoordinates(getString(alias))

/**
 * Get object.
 *
 * @param alias Column alias.
 * @return Object or null if column value is NULL.
 */
internal inline fun <reified T> ResultSet.getTypedObject(alias: String): T? = getObject(alias, T::class.java)

/**
 * Get UUID.
 *
 * @param alias Column alias.
 * @return UUID.
 */
internal fun ResultSet.getUuid(alias: String) = UUID.fromString(getString(alias))

/**
 * Remove new line, multiple spaces and trim this string.
 *
 * @return Inlined string.
 */
internal fun String.inlined() = replace("\n", " ").replace(Regex(" +"), " ").trim()

/**
 * Create alias for column name.
 *
 * @param alias Alias.
 * @return Alias for column name.
 */
internal fun String.withAlias(alias: String) = "${alias}_$this"
