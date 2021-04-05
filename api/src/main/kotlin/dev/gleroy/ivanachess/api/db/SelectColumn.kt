package dev.gleroy.ivanachess.api.db

/**
 * Table column used in SELECT statement.
 *
 * @param name Name.
 * @param tableAlias Table alias.
 */
data class SelectColumn(
    val name: String,
    val tableAlias: String,
)
