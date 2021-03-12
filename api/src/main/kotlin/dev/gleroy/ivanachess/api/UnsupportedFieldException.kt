package dev.gleroy.ivanachess.api

/**
 * Exception thrown when tries to use unsupported field to search an entity.
 *
 * @param field Field.
 * @param entityType Entity type.
 * @param supportedFields Set of supported fields.
 */
data class UnsupportedFieldException(
    val field: String,
    val entityType: String,
    val supportedFields: Set<String>
) : RuntimeException() {
    override val message = "Unsupported field '$field' for entity '$entityType' (expected one of $supportedFields)"
}
