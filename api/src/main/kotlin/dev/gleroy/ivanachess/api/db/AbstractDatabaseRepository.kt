@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.Serializable
import kotlin.math.ceil

/**
 * Abstract database implementation of repository.
 *
 * @param I Type of item ID.
 * @param T Type of items.
 */
abstract class AbstractDatabaseRepository<I : Serializable, T : Item<I>> : Repository<I, T> {
    private companion object {
        /**
         * Filter parameter prefix.
         */
        private const val FilterParamPrefix = "filter_"

        /**
         * Limit parameter name.
         */
        private const val LimitParamName = "limit"

        /**
         * Offset parameter name.
         */
        private const val OffsetParamName = "offset"
    }

    /**
     * JDBC template.
     */
    protected abstract val jdbcTemplate: NamedParameterJdbcTemplate

    /**
     * Table name.
     */
    protected abstract val tableName: String

    /**
     * Table alias used in SELECT statement.
     */
    protected abstract val tableAlias: String

    /**
     * Set of columns used in SELECT statement.
     */
    protected abstract val selectColumns: Set<TableColumn.Select>

    /**
     * List of joins used in SELECT statement.
     */
    protected abstract val selectJoins: List<Join>

    /**
     * Map which associates sortable field to column.
     */
    internal abstract val sortableColumns: Map<ItemField, TableColumn.Select>

    /**
     * Map which associates filterable field to column.
     */
    internal abstract val filterableColumns: Map<ItemField, TableColumn.Select>

    /**
     * Set of columns used in INSERT statement.
     */
    protected abstract val insertColumns: Set<TableColumn.Update>

    /**
     * SELECT statement.
     */
    @Suppress("LeakingThis")
    protected val selectStatement = buildSelectStatement(tableName, tableAlias, selectColumns, selectJoins)

    /**
     * INSERT statement.
     */
    @Suppress("LeakingThis")
    protected abstract val insertStatement: String

    /**
     * Row mapper.
     */
    protected abstract val rowMapper: RowMapper<T>

    /**
     * Logger.
     */
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun count() = count(emptySet())

    override fun existsWithId(id: I) = existsWith(DatabaseConstants.Common.IdColumnName, id)

    override fun fetchById(id: I) = fetchBy(DatabaseConstants.Common.IdColumnName, id)

    override fun fetchPage(pageOpts: PageOptions): Page<T> {
        val whereSql = if (pageOpts.filters.isEmpty()) {
            ""
        } else {
            "WHERE ${buildFilterStatement(pageOpts.filters)}"
        }
        val sql = """
            $selectStatement
            $whereSql
            ${buildPageStatement(pageOpts)}
        """
        val params = createFilterParameters(pageOpts.filters) + createPageParameters(pageOpts)
        logStatement(sql, params)
        val items = jdbcTemplate.query(sql, params, rowMapper)
        val totalItems = count(pageOpts.filters)
        return createPage(items, pageOpts, totalItems)
    }

    /**
     * Build filter statement.
     *
     * @param filters Set of filters.
     * @return Filter statement.
     * @throws UnsupportedFieldException If one of filterable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    protected fun buildFilterStatement(filters: Set<ItemFilter>): String {
        val filtersSql = filters
            .map { filter ->
                val field = filter.field
                val column = filterableColumns[field]
                    ?: throw UnsupportedFieldException(
                        field.label,
                        filterableColumns.keys
                    ).apply { logger.debug(message) }
                "${column.tableAlias}.\"${column.name}\"::varchar = :$FilterParamPrefix${column.name}"
            }
            .reduce { acc, filterSql -> "$acc OR $filterSql" }
        return "($filtersSql)"
    }

    /**
     * Build INSERT statement.
     *
     * @param tableName Table name.
     * @param columns Set of columns.
     * @param returningColumnName Name of column to use in RETURNING statement.
     * @return INSERT statement.
     */
    protected fun buildInsertStatement(
        tableName: String,
        columns: Set<TableColumn.Update>,
        returningColumnName: String? = null
    ): String {
        val columnsSql = columns
            .map { "\"${it.name}\"" }
            .reduce { acc, columnSql -> "$acc, $columnSql" }
        val valuesSql = columns
            .map { ":${it.paramNameWithTypeOverride()}" }
            .reduce { acc, valueSql -> "$acc, $valueSql" }
        return StringBuilder("INSERT INTO \"$tableName\" ($columnsSql) VALUES ($valuesSql)")
            .apply {
                if (returningColumnName != null) {
                    append(" RETURNING \"$returningColumnName\"")
                }
            }
            .toString()
    }

    /**
     * Build page statement.
     *
     * @param pageOpts Page options.
     * @return Page statement.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    protected fun buildPageStatement(pageOpts: PageOptions): String {
        val sortsSql = pageOpts.sorts
            .map { sort ->
                val field = sort.field
                val column = sortableColumns[field]
                    ?: throw UnsupportedFieldException(
                        field.label,
                        sortableColumns.keys
                    ).apply { logger.debug(message) }
                "${column.tableAlias}.\"${column.name}\" ${sort.order.sql()}"
            }
            .reduce { acc, sortSql -> "$acc, $sortSql" }
        return "ORDER BY $sortsSql LIMIT :$LimitParamName OFFSET :$OffsetParamName"
    }

    /**
     * Build SELECT statement.
     *
     * @param tableName Table name.
     * @param tableAlias Table alias.
     * @param columns Set of columns.
     * @param joins List of joins.
     * @return SELECT statement.
     */
    protected fun buildSelectStatement(
        tableName: String,
        tableAlias: String,
        columns: Set<TableColumn.Select>,
        joins: List<Join> = emptyList()
    ): String {
        val columnsSql = columns
            .map { "${it.tableAlias}.\"${it.name}\" AS ${it.name.withAlias(it.tableAlias)}" }
            .reduce { acc, columnSql -> "$acc, $columnSql" }
        val joinsSql = joins
            .map {
                "JOIN \"${it.leftColumn.tableName}\" ${it.leftColumn.tableAlias} " +
                        "ON ${it.leftColumn.tableAlias}.\"${it.leftColumn.name}\" " +
                        "= ${it.rightColumn.tableAlias}.\"${it.rightColumn.name}\""
            }
            .fold(" ") { acc, joinSql -> "$acc $joinSql" }
        return "SELECT $columnsSql FROM \"$tableName\" $tableAlias$joinsSql"
    }

    /**
     * Fetch total number of items.
     *
     * @param filters Set of filters.
     * @return Total number of items.
     */
    protected fun count(filters: Set<ItemFilter>): Int {
        val selectSql = "SELECT COUNT(*) FROM \"$tableName\" $tableAlias"
        val whereSql = if (filters.isEmpty()) {
            ""
        } else {
            " WHERE ${buildFilterStatement(filters)}"
        }
        val sql = "$selectSql$whereSql"
        val params = createFilterParameters(filters)
        logStatement(sql, params)
        return jdbcTemplate.queryForObject(sql, params, Int::class.java)!!
    }

    /**
     * Create page.
     *
     * @param content List of items.
     * @param pageOpts Page options.
     * @param totalItems Total number of items.
     */
    protected fun createPage(content: List<T>, pageOpts: PageOptions, totalItems: Int) = Page(
        content = content,
        number = pageOpts.number,
        totalItems = totalItems,
        totalPages = ceil(totalItems.toDouble() / pageOpts.size.toDouble()).toInt()
    )

    /**
     * Create filter parameters.
     *
     * @param filters Set of filters.
     * @return Filter parameters.
     */
    protected fun createFilterParameters(filters: Set<ItemFilter>) = filters.associate { filter ->
        val field = filter.field
        val column = filterableColumns[field]!!
        "$FilterParamPrefix${column.name}" to filter.value
    }

    /**
     * Create page parameters.
     *
     * @param pageOpts Page options.
     * @return Page parameters.
     */
    protected fun createPageParameters(pageOpts: PageOptions) = mapOf(
        OffsetParamName to (pageOpts.number - 1) * pageOpts.size,
        LimitParamName to pageOpts.size,
    )

    /**
     * Check if item exists with specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @param excluding Set of item IDs excluded from the search.
     * @return True if item exists, false otherwise.
     */
    protected fun existsWith(columnName: String, columnValue: Any, excluding: Set<I> = emptySet()): Boolean {
        val sql = StringBuilder("SELECT EXISTS(SELECT * FROM \"$tableName\" WHERE \"$columnName\" = :value")
            .apply {
                if (excluding.isNotEmpty()) {
                    append(" AND \"${DatabaseConstants.Common.IdColumnName}\" NOT IN (:excluding)")
                }
            }
            .append(")")
            .toString()
        val params = mapOf(
            "value" to columnValue,
            "excluding" to excluding
        )
        logStatement(sql, params)
        return jdbcTemplate.queryForObject(sql, params, Boolean::class.java)!!
    }

    /**
     * Fetch item by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return Item or null if it does not exist.
     */
    protected fun fetchBy(columnName: String, columnValue: Any): T? {
        val sql = "$selectStatement WHERE $tableAlias.\"$columnName\" = :value"
        val params = mapOf("value" to columnValue)
        logStatement(sql, params)
        return jdbcTemplate.queryForNullableObject(sql, params, rowMapper)
    }

    /**
     * Get INSERT statement parameters from an item.
     *
     * @param item Item.
     * @return Map which associates column name to its value.
     */
    protected abstract fun insertParameters(item: T): Map<String, *>

    /**
     * Log SQL statement if debug level is enabled.
     *
     * @param sql SQL statement.
     * @param params Statement parameters.
     */
    protected fun logStatement(sql: String, params: Map<String, *> = emptyMap<String, Any>()) {
        logStatements(sql, listOf(params))
    }

    /**
     * Log SQL statement if debug level is enabled.
     *
     * @param sql SQL statement.
     * @param params List of statement parameters.
     */
    protected fun logStatements(sql: String, params: List<Map<String, *>> = emptyList<Map<String, Any>>()) {
        if (logger.isDebugEnabled) {
            params.forEach { logger.debug("Executing ${sql.inlined()} with parameters $it") }
        }
    }

    /**
     * Transform sort order to SQL.
     *
     * @return SQL.
     */
    protected fun ItemSort.Order.sql() = when (this) {
        ItemSort.Order.Ascending -> "ASC"
        ItemSort.Order.Descending -> "DESC"
    }

    /**
     * Get parameter name with type override if it is present.
     *
     * @param prefix Prefix.
     * @return Parameter name.
     */
    protected fun TableColumn.paramNameWithTypeOverride(prefix: String = "") = if (type == null) {
        "$prefix$name"
    } else {
        "$prefix$name::$type"
    }
}
