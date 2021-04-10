package dev.gleroy.ivanachess.core

/**
 * Exception thrown when tries to use unsupported field to search an entity.
 *
 * @param fieldLabel Field label.
 * @param supportedFields Set of supported fields.
 */
data class UnsupportedFieldException(
    val fieldLabel: String,
    val supportedFields: Set<ItemField>,
) : RuntimeException() {
    override val message = "Unsupported field '$fieldLabel' (expected one of ${supportedFields.map { it.label }})"
}
