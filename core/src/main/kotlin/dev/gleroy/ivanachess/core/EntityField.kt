package dev.gleroy.ivanachess.core

/**
 * Entity field.
 *
 * @param E Type of entity.
 */
interface EntityField<out E : Entity> {
    /**
     * Label.
     */
    val label: String
}
