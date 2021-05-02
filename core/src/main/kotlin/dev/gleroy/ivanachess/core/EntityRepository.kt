package dev.gleroy.ivanachess.core

import java.util.*

/**
 * Repository.
 *
 * @param E Type of entity.
 */
interface EntityRepository<E : Entity> : Repository<UUID, E> {
    /**
     * Delete entity.
     *
     * @param id Entity ID.
     * @return True if entity is deleted, false otherwise.
     */
    fun delete(id: UUID): Boolean

    /**
     * Save entity.
     *
     * @param entity Entity.
     * @return Saved entity.
     */
    fun save(entity: E): E
}
