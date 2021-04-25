@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.CommonEntityField
import dev.gleroy.ivanachess.core.Entity
import dev.gleroy.ivanachess.core.ItemSort
import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

internal abstract class AbstractEntityDatabaseRepositoryTest<E : Entity, R : AbstractEntityDatabaseRepository<E>> :
    AbstractDatabaseRepositoryTest<UUID, E, R>() {

    @BeforeEach
    open fun beforeEach() {
        items = (0 until 100).map { repository.save(createEntity(it)) }
    }

    @Suppress("SqlWithoutWhere", "SqlResolve")
    @AfterEach
    open fun afterEach() {
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.Game.TableName}\"",
            emptyMap<String, Any>()
        )
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.User.TableName}\"",
            emptyMap<String, Any>()
        )
    }

    @Nested
    inner class existsWithId : AbstractDatabaseRepositoryTest<UUID, E, R>.existsWithId() {
        override val wrongValue: UUID = UUID.randomUUID()
    }

    @Nested
    inner class fetchById : AbstractDatabaseRepositoryTest<UUID, E, R>.fetchById() {
        override val wrongValue: UUID = UUID.randomUUID()
    }

    @Nested
    open inner class fetchPage : AbstractDatabaseRepositoryTest<UUID, E, R>.fetchPage() {
        @Test
        fun `should return page sorted by creation date and ID`() {
            shouldReturnPage(
                sorts = listOf(
                    ItemSort(CommonEntityField.CreationDate),
                    ItemSort(CommonEntityField.Id),
                ),
                sortedItems = items.sortedWith { entity1, entity2 ->
                    val result = entity1.creationDate.compareTo(entity2.creationDate)
                    if (result == 0) {
                        entity1.id.toString().compareTo(entity2.id.toString())
                    } else {
                        result
                    }
                }
            )
        }

        @Test
        fun `should return page sorted by ID (ascending)`() {
            shouldReturnPageSortedById()
        }

        @Test
        fun `should return page sorted by ID (descending)`() {
            shouldReturnPageSortedById(ItemSort.Order.Descending)
        }

        @Test
        fun `should return page sorted by creation date (ascending)`() {
            shouldReturnPageSortedByCreationDate()
        }

        @Test
        fun `should return page sorted by creation date (descending)`() {
            shouldReturnPageSortedByCreationDate(ItemSort.Order.Descending)
        }

        private fun shouldReturnPageSortedByCreationDate(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                sortedField = CommonEntityField.CreationDate,
                sortedItems = items.sortedBy { it.creationDate },
                order = order,
            )
        }

        private fun shouldReturnPageSortedById(order: ItemSort.Order = ItemSort.Order.Ascending) {
            shouldReturnPage(
                sortedField = CommonEntityField.Id,
                sortedItems = items.sortedBy { it.id.toString() },
                order = order,
            )
        }
    }

    @Nested
    inner class save {
        @Test
        fun `should create new entity`() {
            shouldSaveEntity { createEntity(items.size) }
        }

        @Test
        fun `should update entity`() {
            shouldSaveEntity { updateEntity(items[0]) }
        }

        private fun shouldSaveEntity(getEntity: () -> E) {
            val entity = getEntity()
            repository.save(entity)
            repository.fetchById(entity.id) shouldBe entity
        }
    }

    protected abstract fun createEntity(index: Int): E

    protected abstract fun updateEntity(entity: E): E
}
