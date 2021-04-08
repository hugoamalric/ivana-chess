package dev.gleroy.ivanachess.core

/**
 * Page option.
 *
 * @param number Page number.
 * @param size Page size.
 * @param sorts List of sorts.
 * @throws IllegalArgumentException [number] <= 0 || [size] <= 0 || [sorts].isEmpty()
 */
data class PageOptions<out E : Entity>(
    val number: Int,
    val size: Int,
    val sorts: List<EntitySort<E>> = listOf(
        EntitySort(CommonSortableEntityField.CreationDate),
        EntitySort(CommonSortableEntityField.Id),
    ),
) {
    init {
        require(number > 0) { "number must be strictly positive" }
        require(size > 0) { "size must be strictly positive" }
        require(sorts.isNotEmpty()) { "sorts must not be empty" }
    }
}
