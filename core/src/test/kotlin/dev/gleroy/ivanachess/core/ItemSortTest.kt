@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ItemSortTest {
    @Nested
    inner class constructor {
        @Test
        fun `should throw exception if field is not sortable`() {
            val exception = assertThrows<IllegalArgumentException> { ItemSort(NotSortableField) }
            exception shouldHaveMessage "field must be sortable"
        }
    }

    private object NotSortableField : ItemField {
        override val label get() = "notSortable"
        override val isSortable get() = false
        override val isFilterable get() = false
        override val isSearchable get() = false
    }
}
