@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Entity
import dev.gleroy.ivanachess.api.Page
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*

abstract class AbstractDatabaseRepositoryTest<E : Entity, R : AbstractDatabaseRepository<E>> {
    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    protected lateinit var repository: R

    protected lateinit var entities: List<E>
    protected lateinit var entity: E

    @BeforeEach
    fun beforeEach() {
        entities = (0 until 100)
            .map { create(it).atUtc() }
            .reversed()
        entity = entities.first()
    }

    @Suppress("SqlWithoutWhere", "SqlResolve")
    @AfterEach
    fun afterEach() {
        jdbcTemplate.update(
            "DELETE FROM \"${repository.tableName}\"",
            ComparableMapSqlParameterSource()
        )
    }

    @Nested
    inner class existsById {
        @Test
        fun `should return false`() {
            repository.existsById(UUID.randomUUID()).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.existsById(entity.id).shouldBeTrue()
        }
    }

    @Nested
    inner class getAll {
        @Test
        fun `should throw exception if page is 0`() {
            val exception = assertThrows<IllegalArgumentException> { repository.getAll(0, 1) }
            exception shouldHaveMessage "page must be strictly positive"
        }

        @Test
        fun `should throw exception if size is 0`() {
            val exception = assertThrows<IllegalArgumentException> { repository.getAll(1, 0) }
            exception shouldHaveMessage "size must be strictly positive"
        }

        @Test
        fun `should return first page`() {
            val page = 1
            val size = 3
            repository.getAll(page, size) shouldBe Page(
                content = entities.subList(entities.size - size, entities.size).reversed(),
                number = page,
                totalItems = entities.size,
                totalPages = 34
            )
        }

        @Test
        fun `should return last page`() {
            val page = 34
            val size = 3
            repository.getAll(page, size) shouldBe Page(
                content = entities.subList(0, 1),
                number = page,
                totalItems = entities.size,
                totalPages = 34
            )
        }
    }

    @Nested
    inner class getById {
        @Test
        fun `should return null`() {
            repository.getById(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return game`() {
            repository.getById(entity.id) shouldBe entity
        }
    }

    protected abstract fun create(index: Int): E

    protected abstract fun E.atUtc(): E
}
