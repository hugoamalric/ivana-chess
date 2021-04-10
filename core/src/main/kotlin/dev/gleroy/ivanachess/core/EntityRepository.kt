package dev.gleroy.ivanachess.core

import java.util.*

/**
 * Repository.
 *
 * @param E Type of entity.
 */
interface EntityRepository<E : Entity> : Repository<UUID, E> {
    /**
     * Save entity.
     *
     * @param entity Entity.
     * @return Saved entity.
     */
    fun save(entity: E): E
}
