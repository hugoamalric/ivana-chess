@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultGameServiceTest {
    private lateinit var repository: GameRepository
    private lateinit var service: DefaultGameService

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        service = DefaultGameService(repository)
    }

    @Nested
    inner class create {
        private val gameInfo = GameInfo()

        @Test
        fun `should create new game`() {
            every { repository.create() } returns gameInfo
            service.create() shouldBe gameInfo
            verify { repository.create() }
            confirmVerified(repository)
        }
    }
}
