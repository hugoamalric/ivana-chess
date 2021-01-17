@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class InMemoryGameRepositoryTest {
    private val repository = InMemoryGameRepository()

    @Nested
    inner class create {
        @Test
        fun `should create new game`() {
            val gameInfo = repository.create()
            gameInfo.game shouldBe Game()
            repository.gameInfos shouldContain gameInfo
        }
    }
}
