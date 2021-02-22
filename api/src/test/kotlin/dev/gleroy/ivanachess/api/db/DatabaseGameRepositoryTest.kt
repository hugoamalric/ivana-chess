@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameInfo
import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.ZoneOffset
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DatabaseGameRepositoryTest {
    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    @Autowired
    private lateinit var repository: DatabaseGameRepository

    @Suppress("SqlWithoutWhere")
    @AfterEach
    fun afterEach() {
        jdbcTemplate.update(
            "DELETE FROM \"${DatabaseConstants.Game.TableName}\"",
            ComparableMapSqlParameterSource()
        )
    }

    @Nested
    inner class create {
        @Test
        fun `should save game`() {
            val gameInfo = repository.create().atUtc()
            repository.getById(gameInfo.id) shouldBe gameInfo
        }
    }

    @Nested
    inner class exists {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create().atUtc()
        }

        @Test
        fun `should return false`() {
            repository.exists(UUID.randomUUID()).shouldBeFalse()
        }

        @Test
        fun `should return true`() {
            repository.exists(gameInfo.id).shouldBeTrue()
        }
    }

    @Nested
    inner class getAll {
        private lateinit var gameInfos: List<GameInfo>

        @BeforeEach
        fun beforeEach() {
            gameInfos = (0 until 100)
                .map { repository.create().atUtc() }
                .reversed()
        }

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
                content = gameInfos.subList(gameInfos.size - size, gameInfos.size).reversed(),
                number = page,
                totalItems = gameInfos.size,
                totalPages = 34
            )
        }

        @Test
        fun `should return last page`() {
            val page = 34
            val size = 3
            repository.getAll(page, size) shouldBe Page(
                content = gameInfos.subList(0, 1),
                number = page,
                totalItems = gameInfos.size,
                totalPages = 34
            )
        }
    }

    @Nested
    inner class getById {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create().atUtc()
        }

        @Test
        fun `should return null`() {
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
            gameInfo = repository.create().atUtc()
        }

        @Test
        fun `should return null`() {
            repository.getByToken(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return game with white token`() {
            repository.getByToken(gameInfo.whiteToken) shouldBe gameInfo
        }

        @Test
        fun `should return game with black token`() {
            repository.getByToken(gameInfo.blackToken) shouldBe gameInfo
        }
    }

    @Nested
    inner class update {
        private lateinit var gameInfo: GameInfo

        @BeforeEach
        fun beforeEach() {
            gameInfo = repository.create().atUtc()
        }

        @Test
        fun `should throw exception if game does not exist`() {
            val gameInfo = GameInfo()
            val exception = assertThrows<IllegalArgumentException> { repository.update(gameInfo) }
            exception shouldHaveMessage "Game ${gameInfo.id} does not exist"
        }

        @Test
        fun `should update game by adding simple move`() {
            val gameInfo = gameInfo.copy(
                game = gameInfo.game.play(Move.Simple.fromCoordinates("A2", "A4"))
            )
            repository.update(gameInfo) shouldBe gameInfo
            repository.getById(gameInfo.id) shouldBe gameInfo
        }

        @Test
        fun `should update game by adding promotion move`() {
            val gameInfo = gameInfo.copy(
                game = gameInfo.game
                    .play(Move.Simple.fromCoordinates("E2", "E4"))
                    .play(Move.Simple.fromCoordinates("E7", "E5"))
                    .play(Move.Simple.fromCoordinates("F2", "F4"))
                    .play(Move.Simple.fromCoordinates("H7", "H6"))
                    .play(Move.Simple.fromCoordinates("F4", "E5"))
                    .play(Move.Simple.fromCoordinates("F7", "F6"))
                    .play(Move.Simple.fromCoordinates("E5", "F6"))
                    .play(Move.Simple.fromCoordinates("G7", "G5"))
                    .play(Move.Simple.fromCoordinates("F6", "F7"))
                    .play(Move.Simple.fromCoordinates("E8", "E7"))
                    .play(
                        Move.Promotion(
                            from = Position.fromCoordinates("F7"),
                            to = Position.fromCoordinates("G8"),
                            promotion = Piece.Queen(Piece.Color.White)
                        )
                    )
            )
            repository.update(gameInfo) shouldBe gameInfo
            repository.getById(gameInfo.id) shouldBe gameInfo
        }
    }

    private fun GameInfo.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
