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
    private lateinit var repository: InMemoryGameRepository

    @BeforeEach
    fun beforeEach() {
        repository = InMemoryGameRepository()
    }

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
    inner class getAll {
        @BeforeEach
        fun beforeEach() {
            repository.gameInfos.addAll((0 until 10).map { GameInfo() })
        }

        @Test
        fun `should throw exception if page is negative`() {
            shouldThrowExceptionIfArgIsNegativeOrEqualToZero("page") { repository.getAll(-1, 0) }
        }

        @Test
        fun `should throw exception if size is negative`() {
            shouldThrowExceptionIfArgIsNegativeOrEqualToZero("size") { repository.getAll(0, -1) }
        }

        @Test
        fun `should return empty page if page is too high`() {
            val page = repository.gameInfos.size + 1
            repository.getAll(page, 1) shouldBe Page(
                totalItems = repository.gameInfos.size,
                totalPages = 10
            )
        }

        @Test
        fun `should return all games`() {
            val page = 1
            repository.getAll(page, 100) shouldBe Page(
                content = repository.gameInfos,
                number = page,
                totalItems = repository.gameInfos.size,
                totalPages = 1
            )
        }

        @Test
        fun `should return 2 games`() {
            val page = 2
            repository.getAll(page, 2) shouldBe Page(
                content = repository.gameInfos.subList(2, 4),
                number = page,
                totalItems = repository.gameInfos.size,
                totalPages = 5
            )
        }

        private fun shouldThrowExceptionIfArgIsNegativeOrEqualToZero(argName: String, getAll: () -> Page<GameInfo>) {
            val exception = assertThrows<IllegalArgumentException> { getAll() }
            exception shouldHaveMessage "$argName must be strictly positive"
        }
    }

    @Nested
    inner class getById {
        private val gameInfo = GameInfo()

        @BeforeEach
        fun beforeEach() {
            repository.gameInfos.add(gameInfo)
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
        private val gameInfo = GameInfo()

        @BeforeEach
        fun beforeEach() {
            repository.gameInfos.add(gameInfo)
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
            gameInfo = GameInfo().let { gameInfo ->
                repository.gameInfos.add(gameInfo)
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
