package dev.gleroy.ivanachess.core

/**
 * Exception thrown when entity does not exist.
 */
class EntityNotFoundException(
    override val message: String
) : RuntimeException()
