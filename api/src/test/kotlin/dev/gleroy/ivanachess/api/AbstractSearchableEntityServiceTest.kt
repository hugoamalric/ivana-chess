@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.util.*

internal abstract class AbstractSearchableEntityServiceTest<E : SearchableEntity, R : SearchableEntityRepository<E>, S : AbstractSearchableEntityService<E>> :
    AbstractEntityServiceTest<E, R, S>() {

    abstract inner class search {
        protected abstract val fields: Set<SearchableEntityField<E>>

        private val term = "term"
        private val pageOpts = PageOptions<E>(1, 10)
        private val excluding = setOf(UUID.randomUUID())
        private val page = Page(
            content = listOf(createEntity()),
            number = 1,
            totalPages = 1,
            totalItems = 1,
        )

        @Test
        fun `should return page`() {
            every { repository.search(term, fields, pageOpts, excluding) } returns page
            service.search(term, fields, pageOpts, excluding) shouldBe page
            verify { repository.search(term, fields, pageOpts, excluding) }
        }
    }
}
