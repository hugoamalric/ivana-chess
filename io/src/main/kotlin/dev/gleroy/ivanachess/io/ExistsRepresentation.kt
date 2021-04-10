package dev.gleroy.ivanachess.io

/**
 * Representation of entity existence.
 *
 * @param exists True if the entity exists, false otherwise.
 */
data class ExistsRepresentation(
    val exists: Boolean,
) : Representation
