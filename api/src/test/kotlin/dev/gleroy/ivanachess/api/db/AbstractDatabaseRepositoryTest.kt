@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.Serializable
import kotlin.math.ceil

internal abstract class AbstractDatabaseRepositoryTest<I : Serializable, T : Item<I>, R : AbstractDatabaseRepository<I, T>> {
    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    protected lateinit var repository: R

    protected lateinit var items: List<T>

    @Nested
    inner class count {
        @Test
        fun `should return total number of items`() {
            repository.count() shouldBe items.size
        }
    }

    @Nested
    open inner class fetchPage {
        private val number = 2
        private val size = 3

        @Test
        fun `should throw exception if one of sortable fields is not supported`() {
            val exception = assertThrows<UnsupportedFieldException> {
                repository.fetchPage(
                    pageOpts = PageOptions(
                        number = number,
                        size = size,
                        sorts = listOf(ItemSort(UnsupportedSortableField)),
                    )
                )
            }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = UnsupportedSortableField.label,
                supportedFields = repository.sortableColumns.keys,
            )
        }

        protected fun shouldReturnPage(
            field: ItemField,
            sortedItems: List<T>,
            order: ItemSort.Order = ItemSort.Order.Ascending
        ) {
            shouldReturnPage(
                sorts = listOf(ItemSort(field, order)),
                sortedItems = if (order == ItemSort.Order.Ascending) {
                    sortedItems
                } else {
                    sortedItems.asReversed()
                },
            )
        }

        protected fun shouldReturnPage(sorts: List<ItemSort>, sortedItems: List<T>) {
            repository.fetchPage(PageOptions(number, size, sorts)) shouldBe Page(
                content = sortedItems.subList((number - 1) * size, number * size),
                number = number,
                totalPages = ceil(sortedItems.size.toDouble() / size.toDouble()).toInt(),
                totalItems = sortedItems.size,
            )
        }
    }

    abstract inner class existsWith<V> {
        protected abstract val wrongValue: V

        @Test
        fun `should return false if item does not exist`() {
            existsWith(wrongValue).shouldBeFalse()
        }

        @Test
        fun `should return true if item exists`() {
            existsWith(valueFromItem(items[0])).shouldBeTrue()
        }

        @Test
        fun `should return false if item exists but it is excluded`() {
            val item = items[0]
            existsWith(valueFromItem(item), setOf(item.id)).shouldBeFalse()
        }

        protected abstract fun existsWith(value: V, excluding: Set<I> = emptySet()): Boolean

        protected abstract fun valueFromItem(item: T): V
    }

    abstract inner class existsWithId {
        abstract val wrongValue: I

        @Test
        fun `should return false if item does not exist`() {
            repository.existsWithId(wrongValue).shouldBeFalse()
        }

        @Test
        fun `should return true if item exists`() {
            repository.existsWithId(items[0].id).shouldBeTrue()
        }
    }

    abstract inner class fetchBy<V> {
        protected abstract val wrongValue: V

        @Test
        fun `should return null if item does not exist`() {
            fetchBy(wrongValue).shouldBeNull()
        }

        @Test
        fun `should return item if it exists`() {
            val item = items[0]
            fetchBy(valueFromItem(item)) shouldBe item
        }

        protected abstract fun fetchBy(value: V): T?

        protected abstract fun valueFromItem(item: T): V
    }

    abstract inner class fetchById : fetchBy<I>() {
        override fun fetchBy(value: I) = repository.fetchById(value)

        override fun valueFromItem(item: T) = item.id
    }

    private object UnsupportedSortableField : ItemField {
        override val label = "unsupported"

        override val isSortable get() = true
        override val isSearchable get() = false
    }
}
