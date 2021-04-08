package dev.gleroy.ivanachess.core

/**
 * Searchable field.
 *
 * @param E Type of entity.
 */
interface SearchableEntityField<out E : SearchableEntity> : EntityField<E>
