package dev.gleroy.ivanachess.api

/**
 * Exception thrown when tries to use unsupported field to search an entity.
 *
 * @param field Field.
 * @param supportedFields Set of supported fields.
 */
data class UnsupportedFieldExceptionV2(
    val field: EntityField<*>,
    val supportedFields: Set<EntityField<*>>
) : RuntimeException() {
    override val message = "Unsupported field '${field.label}' (expected one of ${supportedFields.map { it.label }})"
}
