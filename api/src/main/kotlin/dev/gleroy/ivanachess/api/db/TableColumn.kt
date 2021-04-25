package dev.gleroy.ivanachess.api.db

sealed class TableColumn {
    /**
     * Table column used in SELECT statement.
     *
     * @param name Name.
     * @param tableAlias Table alias.
     * @param type Type.
     */
    data class Select(
        override val name: String,
        val tableAlias: String,
        override val type: String? = null,
    ) : TableColumn()

    /**
     * Table column used in INSERT and UPDATE statement.
     *
     * @param name Name.
     * @param type Type.
     */
    data class Update(
        override val name: String,
        override val type: String? = null,
    ) : TableColumn()

    /**
     * Name.
     */
    abstract val name: String

    /**
     * Type.
     */
    abstract val type: String?
}
