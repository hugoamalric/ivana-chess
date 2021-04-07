package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Entity service.
 *
 * @param E Type of entity.
 */
interface EntityService<E : Entity> {
    /**
     * Check if entity with an ID exists.
     *
     * @param id Entity ID.
     * @return True if entity with id exists, false otherwise.
     */
    fun existsWithId(id: UUID): Boolean

    /**
     * Get entity by its ID.
     *
     * @param id Entity ID.
     * @return Entity.
     * @throws EntityNotFoundException If entity does not exist.
     */
    @Throws(EntityNotFoundException::class)
    fun getById(id: UUID): E

    /**
     * Get page of entities.
     *
     * @param pageOpts Page options.
     * @return Page.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    fun getPage(pageOpts: PageOptions<E>): Page<E>
}
