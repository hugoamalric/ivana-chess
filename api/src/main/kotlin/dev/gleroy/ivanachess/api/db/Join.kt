package dev.gleroy.ivanachess.api.db

/**
 * Join.
 *
 * @param leftColumn Left column.
 * @param rightColumn Right column.
 */
data class Join(
    val leftColumn: Column,
    val rightColumn: TableColumn.Select,
) {
    /**
     * Column used by join.
     *
     * @param name Name.
     * @param tableName Table name.
     * @param tableAlias Table alias.
     */
    data class Column(
        val name: String,
        val tableName: String,
        val tableAlias: String,
    )
}
