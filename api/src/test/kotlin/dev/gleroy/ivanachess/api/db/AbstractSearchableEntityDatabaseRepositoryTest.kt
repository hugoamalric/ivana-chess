@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import kotlin.math.ceil

internal abstract class AbstractSearchableEntityDatabaseRepositoryTest<E : SearchableEntity, R : AbstractSearchableEntityDatabaseRepository<E>> :
    AbstractEntityDatabaseRepositoryTest<E, R>() {

    @Nested
    open inner class search {
        private val number = 2
        private val size = 3

        @Test
        fun `should throw exception if fields list is empty`() {
            val exception = assertThrows<IllegalArgumentException> {
                repository.search("term", emptySet(), PageOptions(number, size))
            }
            exception shouldHaveMessage "fields must not be empty"
        }

        @Test
        fun `should throw exception if fields list contains not searchable field`() {
            val exception = assertThrows<IllegalArgumentException> {
                repository.search("term", setOf(CommonEntityField.Id), PageOptions(number, size))
            }
            exception shouldHaveMessage "fields must contains only searchable fields"
        }

        protected fun shouldReturnPageOfEntitiesWhichMatchSearch(
            term: String,
            searchableField: ItemField,
            sortableField: ItemField,
            sortedEntities: List<E>,
            order: ItemSort.Order = ItemSort.Order.Ascending,
            excluding: Set<UUID> = emptySet(),
            filtering: (E) -> Boolean,
        ) {
            shouldReturnPageOfEntitiesWhichMatchSearch(
                term = term,
                fields = setOf(searchableField),
                sorts = listOf(ItemSort(sortableField, order)),
                sortedEntities = if (order == ItemSort.Order.Ascending) {
                    sortedEntities
                } else {
                    sortedEntities.asReversed()
                },
                excluding = excluding,
                filtering = filtering,
            )
        }

        protected fun shouldReturnPageOfEntitiesWhichMatchSearch(
            term: String,
            fields: Set<ItemField>,
            sorts: List<ItemSort>,
            sortedEntities: List<E>,
            excluding: Set<UUID> = emptySet(),
            filtering: (E) -> Boolean,
        ) {
            val filteredSortedEntities = sortedEntities.filter(filtering)
            repository.search(term, fields, PageOptions(number, size, sorts), excluding) shouldBe Page(
                content = filteredSortedEntities
                    .subList((number - 1) * size, number * size),
                number = number,
                totalPages = ceil(filteredSortedEntities.size.toDouble() / size.toDouble()).toInt(),
                totalItems = filteredSortedEntities.size,
            )
        }
    }
}
