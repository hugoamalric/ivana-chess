@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import java.util.*

/**
 * Abstract database implementation of searchable entity repository.
 *
 * @param E Type of entity.
 */
abstract class AbstractSearchableEntityDatabaseRepository<E : SearchableEntity> :
    AbstractEntityDatabaseRepository<E>(), SearchableEntityRepository<E> {

    /**
     * Map which associates searchable field to column.
     */
    internal abstract val searchableColumns: Map<ItemField, TableColumn.Select>

    override fun search(
        term: String,
        fields: Set<ItemField>,
        pageOpts: PageOptions,
        excluding: Set<UUID>,
    ): Page<E> {
        require(fields.isNotEmpty()) { "fields must not be empty" }
        require(!fields.any { !it.isSearchable }) { "fields must contains only searchable fields" }
        val likesSql = fields
            .map { field ->
                val column = searchableColumns[field]
                    ?: throw UnsupportedFieldException(field.label, searchableColumns.keys)
                "LOWER(${column.tableAlias}.\"${column.name}\") LIKE CONCAT('%', LOWER(:term), '%')"
            }
            .reduce { acc, likeSql -> "$acc OR $likeSql" }
        val whereSql = StringBuilder("WHERE ($likesSql)")
            .apply {
                if (excluding.isNotEmpty()) {
                    append(" AND $tableAlias.\"${DatabaseConstants.Common.IdColumnName}\" NOT IN (:excluding)")
                }
                if (pageOpts.filters.isNotEmpty()) {
                    append(" AND ${buildFilterStatement(pageOpts)}")
                }
            }
            .toString()
        val selectSql = "$selectStatement $whereSql ${buildPageStatement(pageOpts)}"
        val params = createPageParameters(pageOpts) + mapOf(
            "term" to term,
            "excluding" to excluding,
        )
        logStatement(selectSql, params)
        val entities = jdbcTemplate.query(selectSql, params, rowMapper)
        val countSql = """
            SELECT COUNT(*)
            FROM "$tableName" $tableAlias
            $whereSql
        """
        logStatement(countSql, params)
        val totalItems = jdbcTemplate.queryForObject(countSql, params, Int::class.java)!!
        return createPage(entities, pageOpts, totalItems)
    }
}
