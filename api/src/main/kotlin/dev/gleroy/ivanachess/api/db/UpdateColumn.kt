package dev.gleroy.ivanachess.api.db

/**
 * Table column used in INSERT and UPDATE statement.
 *
 * @param name Name.
 * @param type Type.
 */
data class UpdateColumn(
    val name: String,
    val type: String? = null,
)
