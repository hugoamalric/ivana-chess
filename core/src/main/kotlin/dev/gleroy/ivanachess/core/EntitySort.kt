package dev.gleroy.ivanachess.core

/**
 * Entity sort.
 *
 * @param E Type of entity.
 * @param field Field.
 * @param order Sort order.
 */
data class EntitySort<out E : Entity>(
    val field: SortableEntityField<E>,
    val order: Order = Order.Ascending,
) {
    /**
     * Sort order.
     */
    enum class Order {
        /**
         * Ascending order.
         */
        Ascending,

        /**
         * Descending order.
         */
        Descending
    }
}
