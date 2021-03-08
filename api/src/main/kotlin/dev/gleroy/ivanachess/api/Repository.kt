package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Repository.
 *
 * @param E Entity type.
 */
interface Repository<E : Entity> {
    /**
     * Check if entity exists.
     *
     * @param id Entity ID.
     * @return True if entity exists, false otherwise.
     */
    fun existsById(id: UUID): Boolean

    /**
     * Get page of entities.
     *
     * @param page Page number.
     * @param size Page size.
     * @return Page.
     * @throws IllegalArgumentException If offset or limit is negative or equal to zero.
     */
    @Throws(IllegalArgumentException::class)
    fun getAll(page: Int, size: Int): Page<E>

    /**
     * Get entity by its ID.
     *
     * @param id Entity ID.
     * @return Entity or null if no entity with this ID.
     */
    fun getById(id: UUID): E?
}
