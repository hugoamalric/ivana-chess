package dev.gleroy.ivanachess.api

/**
 * Exception thrown when entity does not exist.
 */
class EntityNotFoundException(
    override val message: String
) : RuntimeException()
