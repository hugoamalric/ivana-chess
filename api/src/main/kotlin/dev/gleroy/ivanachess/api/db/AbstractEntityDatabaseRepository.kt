@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.Entity
import dev.gleroy.ivanachess.core.EntityRepository
import java.util.*

/**
 * Abstract database implementation of entity repository.
 *
 * @param E Type of entity.
 */
abstract class AbstractEntityDatabaseRepository<E : Entity> :
    AbstractDatabaseRepository<UUID, E>(), EntityRepository<E> {

    private companion object {
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
     * Set of columns used in UPDATE statement.
     */
    protected abstract val updateColumns: Set<UpdateColumn>

    /**
     * INSERT statement.
     */
    @Suppress("LeakingThis")
    override val insertStatement = buildInsertStatement(tableName, setOf(IdColumn, CreationDateColumn) + insertColumns)

    /**
     * UPDATE statement.
     */
    @Suppress("LeakingThis")
    protected val updateStatement = buildUpdateStatement(tableName, updateColumns)

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
     * Get UPDATE statement parameters from an entity.
     *
     * @param entity Entity.
     * @return Map which associates column name to its value.
     */
    protected abstract fun updateParams(entity: E): Map<String, *>
}
