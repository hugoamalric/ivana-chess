package dev.gleroy.ivanachess.api

import java.util.*

/**
 * Searchable entity repository.
 *
 * @param E Type of entity.
 */
interface SearchableEntityRepository<E : SearchableEntity> : EntityRepository<E> {
    /**
     * Fetch page of entities which match search term.
     *
     * If one of fields matches the search, the entity is returned.
     *
     * @param term Search term.
     * @param fields Set of fields in which search.
     * @param pageOpts Page options.
     * @param excluding Set of user IDs excluded from the search.
     * @return Page.
     * @throws IllegalArgumentException [fields].isEmpty()
     * @throws UnsupportedFieldExceptionV2 If one of searchable/sortable fields is not supported.
     */
    @Throws(exceptionClasses = [IllegalArgumentException::class, UnsupportedFieldExceptionV2::class])
    fun search(
        term: String,
        fields: Set<SearchableEntityField<E>>,
        pageOpts: PageOptions<E>,
        excluding: Set<UUID> = emptySet(),
    ): Page<E>
}
