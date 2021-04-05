@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Entity
import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.Repository
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*

internal abstract class AbstractDatabaseRepositoryTest<E : Entity, R : Repository<E>> {
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
    inner class fetchPage {
        private val number = 2
        private val size = 3

        private lateinit var page: Page<E>

        @BeforeEach
        fun beforeEach() {
            page = Page(
                content = entities.subList((number - 1) * size, number * size),
                number = number,
                totalItems = entities.size,
                totalPages = 34
            )
        }

        @Test
        fun `should throw exception if number is negative`() {
            shouldThrowExceptionIfParameterIsInvalid("number", -1)
        }

        @Test
        fun `should throw exception if number is zero`() {
            shouldThrowExceptionIfParameterIsInvalid("number", 0)
        }

        @Test
        fun `should throw exception if size is negative`() {
            shouldThrowExceptionIfParameterIsInvalid("size", 1, -1)
        }

        @Test
        fun `should throw exception if size is zero`() {
            shouldThrowExceptionIfParameterIsInvalid("size", 1, 0)
        }

        @Test
        fun `should return page`() {
            repository.fetchPage(number, size) shouldBe page
        }

        private fun shouldThrowExceptionIfParameterIsInvalid(parameter: String, number: Int = 1, size: Int = 1) {
            val exception = assertThrows<IllegalArgumentException> { repository.fetchPage(number, size) }
            exception shouldHaveMessage "$parameter must be strictly positive"
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
}
