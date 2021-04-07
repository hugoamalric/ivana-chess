@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*
import kotlin.math.ceil

/**
 * Abstract database implementation of entity repository.
 *
 * @param E Type of entity.
 */
abstract class AbstractDatabaseEntityRepository<E : Entity> : EntityRepository<E> {
    private companion object {
        /**
         * Limit parameter name.
         */
        private const val LimitParamName = "limit"

        /**
         * Offset parameter name.
         */
        private const val OffsetParamName = "offset"

        /**
         * ID column.
         */
        private val IdColumn = UpdateColumn(DatabaseConstants.Common.IdColumnName)

        /**
         * Creation date column.
         */
        private val CreationDateColumn = UpdateColumn(DatabaseConstants.Common.CreationDateColumnName)
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
    protected abstract val selectColumns: Set<SelectColumn>

    /**
     * List of joins used in SELECT statement.
     */
    protected abstract val selectJoins: List<Join>

    /**
     * Map which associates sortable field to column.
     */
    internal abstract val sortableColumns: Map<SortableEntityField<E>, SelectColumn>

    /**
     * Set of columns used in INSERT statement.
     */
    protected abstract val insertColumns: Set<UpdateColumn>

    /**
     * Set of columns used in UPDATE statement.
     */
    protected abstract val updateColumns: Set<UpdateColumn>

    /**
     * SELECT statement.
     */
    @Suppress("LeakingThis")
    protected val selectStatement = buildSelectStatement(tableName, tableAlias, selectColumns, selectJoins)

    /**
     * INSERT statement.
     */
    @Suppress("LeakingThis")
    protected val insertStatement = buildInsertStatement(tableName, setOf(IdColumn, CreationDateColumn) + insertColumns)

    /**
     * UPDATE statement.
     */
    @Suppress("LeakingThis")
    protected val updateStatement = buildUpdateStatement(tableName, updateColumns)

    /**
     * Row mapper.
     */
    protected abstract val rowMapper: RowMapper<E>

    /**
     * Logger.
     */
    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun count(): Int {
        val sql = "SELECT COUNT(*) FROM \"$tableName\""
        logStatement(sql)
        return jdbcTemplate.queryForObject(sql, emptyMap<String, Any>(), Int::class.java)!!
    }

    override fun existsWithId(id: UUID) = existsWith(DatabaseConstants.Common.IdColumnName, id)

    override fun fetchById(id: UUID) = fetchBy(DatabaseConstants.Common.IdColumnName, id)

    override fun fetchPage(pageOpts: PageOptions<E>): Page<E> {
        val sql = """
            $selectStatement
            ${buildPageStatement(pageOpts)}
        """
        val params = createPageParams(pageOpts)
        logStatement(sql, params)
        val entities = jdbcTemplate.query(sql, params, rowMapper)
        val totalItems = count()
        return createPage(entities, pageOpts, totalItems)
    }

    override fun save(entity: E): E {
        val sql: String
        val params: Map<String, *>
        if (existsWithId(entity.id)) {
            sql = "$updateStatement WHERE \"${DatabaseConstants.Common.IdColumnName}\" = :${IdColumn.name}"
            params = updateParams(entity) + mapOf(IdColumn.name to entity.id)
        } else {
            sql = insertStatement
            params = insertParams(entity) +
                    mapOf(
                        IdColumn.name to entity.id,
                        CreationDateColumn.name to entity.creationDate,
                    )
        }
        logStatement(sql, params)
        jdbcTemplate.update(sql, params)
        return entity
    }

    /**
     * Build INSERT statement.
     *
     * @param tableName Table name.
     * @param columns Set of columns.
     * @return INSERT statement.
     */
    protected fun buildInsertStatement(tableName: String, columns: Set<UpdateColumn>): String {
        val columnsSql = columns
            .map { "\"${it.name}\"" }
            .reduce { acc, columnSql -> "$acc, $columnSql" }
        val valuesSql = columns
            .map { ":${it.paramNameWithTypeOverride()}" }
            .reduce { acc, valueSql -> "$acc, $valueSql" }
        return "INSERT INTO \"$tableName\" ($columnsSql) VALUES ($valuesSql)"
    }

    /**
     * Build page statement.
     *
     * @param pageOpts Page options.
     * @return Page statement.
     * @throws UnsupportedFieldExceptionV2 If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldExceptionV2::class)
    protected fun buildPageStatement(pageOpts: PageOptions<E>): String {
        val sortsSql = pageOpts.sorts
            .map { sort ->
                val field = sort.field
                val column = sortableColumns[field]
                    ?: throw UnsupportedFieldExceptionV2(field, sortableColumns.keys).apply { logger.debug(message) }
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
        columns: Set<SelectColumn>,
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
     * Build UPDATE statement.
     *
     * @param tableName Table name.
     * @param columns Set of columns.
     * @return UPDATE statement.
     */
    protected fun buildUpdateStatement(tableName: String, columns: Set<UpdateColumn>): String {
        val setSql = columns
            .map { "\"${it.name}\" = :${it.paramNameWithTypeOverride()}" }
            .reduce { acc, columnSql -> "$acc, $columnSql" }
        return "UPDATE \"$tableName\" SET $setSql"
    }

    /**
     * Create page.
     *
     * @param content List of entities.
     * @param pageOpts Page options.
     * @param totalItems Total number of entities.
     */
    protected fun createPage(content: List<E>, pageOpts: PageOptions<E>, totalItems: Int) = Page(
        content = content,
        number = pageOpts.number,
        totalItems = totalItems,
        totalPages = ceil(totalItems.toDouble() / pageOpts.size.toDouble()).toInt()
    )

    /**
     * Create page parameters.
     *
     * @param pageOpts Page options.
     * @return Page parameters.
     */
    protected fun createPageParams(pageOpts: PageOptions<E>) = mapOf(
        OffsetParamName to (pageOpts.number - 1) * pageOpts.size,
        LimitParamName to pageOpts.size,
    )

    /**
     * Check if entity exists with specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @param excluding Set of entity IDs excluded from the search.
     * @return True if entity exists, false otherwise.
     */
    protected fun existsWith(columnName: String, columnValue: Any, excluding: Set<UUID> = emptySet()): Boolean {
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
     * Fetch entity by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return Entity or null if it does not exist.
     */
    protected fun fetchBy(columnName: String, columnValue: Any): E? {
        val sql = "$selectStatement WHERE $tableAlias.\"$columnName\" = :value"
        val params = mapOf("value" to columnValue)
        logStatement(sql, params)
        return jdbcTemplate.queryForNullableObject(sql, params, rowMapper)
    }

    /**
     * Get INSERT statement parameters from an entity.
     *
     * @param entity Entity.
     * @return Map which associates column name to its value.
     */
    protected abstract fun insertParams(entity: E): Map<String, *>

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
     * Get UPDATE statement parameters from an entity.
     *
     * @param entity Entity.
     * @return Map which associates column name to its value.
     */
    protected abstract fun updateParams(entity: E): Map<String, *>

    /**
     * Transform entity sort order to SQL.
     *
     * @return SQL.
     */
    protected fun EntitySort.Order.sql() = when (this) {
        EntitySort.Order.Ascending -> "ASC"
        EntitySort.Order.Descending -> "DESC"
    }

    /**
     * Get parameter name with type override if it is present.
     *
     * @return Parameter name.
     */
    protected fun UpdateColumn.paramNameWithTypeOverride() = if (type == null) {
        name
    } else {
        "$name::$type"
    }
}
