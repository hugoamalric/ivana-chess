package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Position
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
internal inline fun <reified T> NamedParameterJdbcTemplate.queryForNullableObject(
    sql: String,
    params: Map<String, *>,
    mapper: RowMapper<T>
) = try {
    queryForObject(sql, ComparableMapSqlParameterSource(params), mapper)
} catch (exception: EmptyResultDataAccessException) {
    null
}

/**
 * Get color type.
 *
 * @param alias Column alias.
 * @return Color type.
 */
internal fun ResultSet.getColorType(alias: String) = ColorType.from(getString(alias))

/**
 * Get game state type.
 *
 * @param alias Column alias.
 * @return Game state type.
 */
internal fun ResultSet.getGameStateType(alias: String) = GameStateType.from(getString(alias))

/**
 * Get piece type.
 *
 * @param alias Column alias.
 * @return Piece type or null if column value is NULL.
 * @throws IllegalArgumentException If column value is not a valid piece type.
 */
@Throws(IllegalArgumentException::class)
internal fun ResultSet.getNullablePieceType(alias: String) = getString(alias)?.let { pieceTypeSqlValue ->
    PieceType.values().find { it.sqlValue == pieceTypeSqlValue }
        ?: throw IllegalArgumentException("Unknown piece type '$pieceTypeSqlValue'")
}

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
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
internal inline fun <reified T> ResultSet.getTypedObject(alias: String): T? = getObject(alias, T::class.java)

/**
 * Get UUID.
 *
 * @param alias Column alias.
 * @return UUID.
 */
internal fun ResultSet.getUuid(alias: String) = UUID.fromString(getString(alias))
