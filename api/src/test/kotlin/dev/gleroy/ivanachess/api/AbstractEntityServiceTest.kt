@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.*
import java.util.*

internal abstract class AbstractEntityServiceTest<E : Entity, R : EntityRepository<E>, S : AbstractEntityService<E>> {
    protected lateinit var repository: R
    protected lateinit var service: S

    @BeforeEach
    fun beforeEach() {
        repository = mockRepository()
        service = createService()
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(repository)
    }

    @Nested
    inner class existsWithId : existsWith<UUID>() {
        override val value: UUID = UUID.randomUUID()

        override fun existsWithFromRepository(value: UUID, excluding: Set<UUID>) = repository.existsWithId(value)

        override fun existsWith(value: UUID, excluding: Set<UUID>) = service.existsWithId(value)
    }

    @Nested
    inner class getById : getBy<UUID>() {
        override fun buildErrorMessage(value: UUID) = "Entity $value does not exist"

        override fun fetchBy(value: UUID) = repository.fetchById(value)

        override fun getBy(value: UUID) = service.getById(value)

        override fun valueFromEntity(entity: E) = entity.id
    }

    @Nested
    inner class getPage {
        private val pageOpts = PageOptions<E>(1, 10)
        private val page = Page(
            content = listOf(createEntity()),
            number = 1,
            totalPages = 1,
            totalItems = 1,
        )

        @Test
        fun `should return page`() {
            every { repository.fetchPage(pageOpts) } returns page
            service.getPage(pageOpts) shouldBe page
            verify { repository.fetchPage(pageOpts) }
        }
    }

    abstract inner class existsWith<T> {
        private val excluding = setOf(UUID.randomUUID())

        protected abstract val value: T

        @Test
        fun `should return false if entity does not exist`() {
            shouldReturnIfEntityExists()
        }

        @Test
        fun `should return true if entity exists`() {
            shouldReturnIfEntityExists(true)
        }

        private fun shouldReturnIfEntityExists(exists: Boolean = false) {
            every { existsWithFromRepository(value, excluding) } returns exists
            existsWith(value, excluding) shouldBe exists
            verify { existsWithFromRepository(value, excluding) }
        }

        protected abstract fun existsWithFromRepository(value: T, excluding: Set<UUID>): Boolean

        protected abstract fun existsWith(value: T, excluding: Set<UUID>): Boolean
    }

    abstract inner class getBy<T> {
        private val entity = createEntity()

        @Suppress("LeakingThis")
        private val value = valueFromEntity(entity)

        @Test
        fun `should throw exception if entity does not exist`() {
            every { fetchBy(value) } returns null
            val exception = assertThrows<EntityNotFoundException> { getBy(value) }
            exception shouldHaveMessage buildErrorMessage(value)
            verify { fetchBy(value) }
        }

        @Test
        fun `should return entity if it exists`() {
            every { fetchBy(value) } returns entity
            getBy(value) shouldBe entity
            verify { fetchBy(value) }
        }

        protected abstract fun buildErrorMessage(value: T): String

        protected abstract fun fetchBy(value: T): E?

        protected abstract fun getBy(value: T): E

        protected abstract fun valueFromEntity(entity: E): T
    }

    protected abstract fun createEntity(): E

    protected abstract fun createService(): S

    protected abstract fun mockRepository(): R
}
