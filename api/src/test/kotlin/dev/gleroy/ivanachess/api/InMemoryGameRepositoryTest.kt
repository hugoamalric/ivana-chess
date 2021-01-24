@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

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

    @Nested
    inner class getById {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create()
        }

        @Test
        fun `should return null if game does not exist`() {
            repository.getById(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return game`() {
            repository.getById(gameInfo.id) shouldBe gameInfo
        }
    }

    @Nested
    inner class getByToken {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create()
        }

        @Test
        fun `should return null if game does not exist`() {
            repository.getByToken(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return game if token is white token`() {
            repository.getByToken(gameInfo.whiteToken) shouldBe gameInfo
        }

        @Test
        fun `should return game if token is black token`() {
            repository.getByToken(gameInfo.blackToken) shouldBe gameInfo
        }
    }

    @Nested
    inner class update {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create().let { gameInfo ->
                gameInfo.copy(
                    game = gameInfo.game.play(Move.fromCoordinates("E2", "E4"))
                )
            }
        }

        @Test
        fun `should throw exception if game does not exist`() {
            val id = UUID.randomUUID()
            val exception = assertThrows<IllegalArgumentException> { repository.update(gameInfo.copy(id = id)) }
            exception shouldHaveMessage "Game $id does not exist"
        }

        @Test
        fun `should update game information`() {
            repository.update(gameInfo) shouldBe gameInfo
            repository.gameInfos shouldContainExactly setOf(gameInfo)
        }
    }
}
