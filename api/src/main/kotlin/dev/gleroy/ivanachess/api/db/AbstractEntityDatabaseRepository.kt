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

    protected companion object {
        /**
         * ID column.
         */
        internal val IdColumn = TableColumn.Update(DatabaseConstants.Common.IdColumnName)

        /**
         * Creation date column.
         */
        internal val CreationDateColumn = TableColumn.Update(DatabaseConstants.Common.CreationDateColumnName)
    }

    /**
     * Set of columns used in UPDATE statement.
     */
    protected abstract val columnsToUpdate: Set<TableColumn.Update>

    /**
     * INSERT statement.
     */
    @Suppress("LeakingThis")
    override val insertStatement = buildInsertStatement(tableName, setOf(IdColumn, CreationDateColumn) + insertColumns)

    /**
     * UPDATE statement.
     */
    @Suppress("LeakingThis")
    protected val updateStatement = buildUpdateStatement(tableName, columnsToUpdate)

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM \"$tableName\" WHERE \"${IdColumn.name}\" = :${IdColumn.name}"
        val params = mapOf(IdColumn.name to id)
        logStatement(sql, params)
        return jdbcTemplate.update(sql, params) > 0
    }

    override fun save(entity: E): E {
        val sql: String
        val params: Map<String, *>
        if (existsWithId(entity.id)) {
            sql = "$updateStatement WHERE \"${DatabaseConstants.Common.IdColumnName}\" = :${IdColumn.name}"
            params = updateParameters(entity) + mapOf(IdColumn.name to entity.id)
        } else {
            sql = insertStatement
            params = insertParameters(entity) +
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
    protected open fun buildUpdateStatement(tableName: String, columns: Set<TableColumn.Update>): String {
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
    protected abstract fun updateParameters(entity: E): Map<String, *>
}
