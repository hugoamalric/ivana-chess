@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*
import kotlin.math.ceil

internal abstract class AbstractDatabaseEntityRepositoryTest<E : Entity, R : AbstractDatabaseEntityRepository<E>> {
    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    protected lateinit var repository: R

    protected lateinit var entities: List<E>

    @BeforeEach
    open fun beforeEach() {
        entities = (0 until 100).map { repository.save(createEntity(it)) }
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
    inner class count {
        @Test
        fun `should return total number of entities`() {
            repository.count() shouldBe entities.size
        }
    }

    @Nested
    inner class existsWithId {
        @Test
        fun `should return false if entity does not exist`() {
            repository.existsWithId(UUID.randomUUID()).shouldBeFalse()
        }

        @Test
        fun `should return true if entity exists`() {
            repository.existsWithId(entities[0].id).shouldBeTrue()
        }
    }

    @Nested
    inner class fetchById : fetchBy<UUID>() {
        override val wrongValue: UUID = UUID.randomUUID()

        override fun fetchBy(value: UUID) = repository.fetchById(value)

        override fun valueFromEntity(entity: E) = entity.id
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
                        sorts = listOf(EntitySort(UnsupportedSortableField)),
                    )
                )
            }
            exception shouldBe UnsupportedFieldException(
                fieldLabel = UnsupportedSortableField.label,
                supportedFields = repository.sortableColumns.keys,
            )
        }

        @Test
        fun `should return page sorted by creation date and ID`() {
            shouldReturnPage(
                sorts = listOf(
                    EntitySort(CommonSortableEntityField.CreationDate),
                    EntitySort(CommonSortableEntityField.Id),
                ),
                sortedEntities = entities.sortedWith { entity1, entity2 ->
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
            shouldReturnPageSortedById(EntitySort.Order.Descending)
        }

        @Test
        fun `should return page sorted by creation date (ascending)`() {
            shouldReturnPageSortedByCreationDate()
        }

        @Test
        fun `should return page sorted by creation date (descending)`() {
            shouldReturnPageSortedByCreationDate(EntitySort.Order.Descending)
        }

        protected fun shouldReturnPage(
            field: SortableEntityField<E>,
            sortedEntities: List<E>,
            order: EntitySort.Order = EntitySort.Order.Ascending
        ) {
            shouldReturnPage(
                sorts = listOf(EntitySort(field, order)),
                sortedEntities = if (order == EntitySort.Order.Ascending) {
                    sortedEntities
                } else {
                    sortedEntities.asReversed()
                },
            )
        }

        protected fun shouldReturnPage(sorts: List<EntitySort<E>>, sortedEntities: List<E>) {
            repository.fetchPage(PageOptions(number, size, sorts)) shouldBe Page(
                content = sortedEntities.subList((number - 1) * size, number * size),
                number = number,
                totalPages = ceil(sortedEntities.size.toDouble() / size.toDouble()).toInt(),
                totalItems = sortedEntities.size,
            )
        }

        private fun shouldReturnPageSortedByCreationDate(order: EntitySort.Order = EntitySort.Order.Ascending) {
            shouldReturnPage(
                field = CommonSortableEntityField.CreationDate,
                sortedEntities = entities.sortedBy { it.creationDate },
                order = order,
            )
        }

        private fun shouldReturnPageSortedById(order: EntitySort.Order = EntitySort.Order.Ascending) {
            shouldReturnPage(
                field = CommonSortableEntityField.Id,
                sortedEntities = entities.sortedBy { it.id.toString() },
                order = order,
            )
        }
    }

    @Nested
    inner class save {
        @Test
        fun `should create new entity`() {
            shouldSaveEntity { createEntity(entities.size) }
        }

        @Test
        fun `should update entity`() {
            shouldSaveEntity { updateEntity(entities[0]) }
        }

        private fun shouldSaveEntity(getEntity: () -> E) {
            val entity = getEntity()
            repository.save(entity)
            repository.fetchById(entity.id) shouldBe entity
        }
    }

    abstract inner class existsWith<T> {
        protected abstract val wrongValue: T

        @Test
        fun `should return false if entity does not exist`() {
            existsWith(wrongValue).shouldBeFalse()
        }

        @Test
        fun `should return true if entity exists`() {
            existsWith(valueFromEntity(entities[0])).shouldBeTrue()
        }

        @Test
        fun `should return false if entity exists but it is excluded`() {
            val entity = entities[0]
            existsWith(valueFromEntity(entity), setOf(entity.id)).shouldBeFalse()
        }

        protected abstract fun existsWith(value: T, excluding: Set<UUID> = emptySet()): Boolean

        protected abstract fun valueFromEntity(entity: E): T
    }

    abstract inner class fetchBy<T> {
        protected abstract val wrongValue: T

        @Test
        fun `should return null if entity does not exist`() {
            fetchBy(wrongValue).shouldBeNull()
        }

        @Test
        fun `should return entity if it exists`() {
            val entity = entities[0]
            fetchBy(valueFromEntity(entity)) shouldBe entity
        }

        protected abstract fun fetchBy(value: T): E?

        protected abstract fun valueFromEntity(entity: E): T
    }

    protected abstract fun createEntity(index: Int): E

    protected abstract fun updateEntity(entity: E): E

    private object UnsupportedSortableField : SortableEntityField<Nothing> {
        override val label = "unsupported"
    }
}
