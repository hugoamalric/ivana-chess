package dev.gleroy.ivanachess.core

import java.io.Serializable

/**
 * Repository.
 *
 * @param I Type of items ID.
 * @param T Type of items.
 */
interface Repository<I : Serializable, T : Item<I>> {
    /**
     * Fetch total number of items.
     *
     * @return Total number of items.
     */
    fun count(): Int

    /**
     * Check if item with an ID exists.
     *
     * @param id Item ID.
     * @return True if item with id exists, false otherwise.
     */
    fun existsWithId(id: I): Boolean

    /**
     * Fetch item by its ID.
     *
     * @param id Item ID.
     * @return Item or null if no item with this ID.
     */
    fun fetchById(id: I): T?

    /**
     * Fetch page of items.
     *
     * @param pageOpts Page options.
     * @return Page.
     * @throws UnsupportedFieldException If one of sortable fields is not supported.
     */
    @Throws(UnsupportedFieldException::class)
    fun fetchPage(pageOpts: PageOptions): Page<T>
}
