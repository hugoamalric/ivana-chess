package dev.gleroy.ivanachess.core

/**
 * Item sort.
 *
 * @param field Field.
 * @param order Sort order.
 * @throws IllegalArgumentException ![field].isSortable
 */
data class ItemSort(
    val field: ItemField,
    val order: Order = Order.Ascending,
) {
    init {
        require(field.isSortable) { "field must be sortable" }
    }

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
