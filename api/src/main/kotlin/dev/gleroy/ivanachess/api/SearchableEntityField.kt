package dev.gleroy.ivanachess.api

/**
 * Searchable field.
 *
 * @param E Type of entity.
 */
interface SearchableEntityField<out E : SearchableEntity> : EntityField<E>
