package dev.gleroy.ivanachess.api.db

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

/**
 * Comparable map SQL parameter source.
 *
 * @param values Map which associates parameter name to its value.
 */
internal class ComparableMapSqlParameterSource(
    values: Map<String, *> = emptyMap<String, Any>()
) : MapSqlParameterSource(values) {
    override fun equals(other: Any?) = other is MapSqlParameterSource && values == other.values

    override fun hashCode() = values.hashCode()
}
