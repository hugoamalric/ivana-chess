package dev.gleroy.ivanachess.core

import java.util.*

/**
 * Repository.
 *
 * @param E Type of entity.
 */
interface EntityRepository<E : Entity> {
    /**
     * Fetch total number of entities.
     *
     * @return Total number of entities.
     */
    fun count(): Int

    /**
     * Check if entity with an ID exists.
     *
     * @param id Entity ID.
     * @return True if entity with id exists, false otherwise.
     */
    fun existsWithId(id: UUID): Boolean

    /**
     * Fetch entity by its ID.
     *
     * @param id Entity ID.
     * @return Entity or null if no entity with this ID.
     */
    fun fetchById(id: UUID): E?

    /**
     * Fetch page of entities.
     *
     * @param pageOpts Page options.
     * @return Page.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    fun fetchPage(pageOpts: PageOptions<E>): Page<E>

    /**
     * Save entity.
     *
     * @param entity Entity.
     * @return Saved entity.
     */
    fun save(entity: E): E
}
