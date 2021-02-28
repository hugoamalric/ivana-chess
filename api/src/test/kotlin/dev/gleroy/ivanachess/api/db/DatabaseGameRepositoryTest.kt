@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.game.GameSummary
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZoneOffset
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class DatabaseGameRepositoryTest : AbstractDatabaseRepositoryTest<GameSummary, DatabaseGameRepository>() {
    @Nested
    inner class getByToken {
        @Test
        fun `should return null`() {
            repository.getByToken(UUID.randomUUID()).shouldBeNull()
        }

        @Test
        fun `should return game with white token`() {
            repository.getByToken(entity.whiteToken) shouldBe entity
        }

        @Test
        fun `should return game with black token`() {
            repository.getByToken(entity.blackToken) shouldBe entity
        }
    }

    @Nested
    inner class getMoves {
        @Test
        fun `should return moves`() {
            val moves = listOf(
                Move.Simple.fromCoordinates("E2", "E4"),
                Move.Simple.fromCoordinates("E7", "E5"),
                Move.Simple.fromCoordinates("F2", "F4"),
                Move.Simple.fromCoordinates("H7", "H6"),
                Move.Simple.fromCoordinates("F4", "E5"),
                Move.Simple.fromCoordinates("F7", "F6"),
                Move.Simple.fromCoordinates("E5", "F6"),
                Move.Simple.fromCoordinates("G7", "G5"),
                Move.Simple.fromCoordinates("F6", "F7"),
                Move.Simple.fromCoordinates("E8", "E7"),
                Move.Promotion(
                    from = Position.fromCoordinates("F7"),
                    to = Position.fromCoordinates("G8"),
                    promotion = Piece.Queen(Piece.Color.White)
                )
            )
            repository.save(entity, moves)
            repository.getMoves(entity.id) shouldBe moves
        }
    }

    @Nested
    inner class save {
        @Test
        fun `should create new game`() {
            val gameSummary = repository.save().atUtc()
            repository.getById(gameSummary.id) shouldBe gameSummary
        }

        @Test
        fun `should update game`() {
            val game = Game(
                moves = listOf(
                    Move.Simple.fromCoordinates("E2", "E4"),
                    Move.Simple.fromCoordinates("E7", "E5"),
                    Move.Simple.fromCoordinates("F2", "F4"),
                    Move.Simple.fromCoordinates("H7", "H6"),
                    Move.Simple.fromCoordinates("F4", "E5"),
                    Move.Simple.fromCoordinates("F7", "F6"),
                    Move.Simple.fromCoordinates("E5", "F6"),
                    Move.Simple.fromCoordinates("G7", "G5"),
                    Move.Simple.fromCoordinates("F6", "F7"),
                    Move.Simple.fromCoordinates("E8", "E7"),
                    Move.Promotion(
                        from = Position.fromCoordinates("F7"),
                        to = Position.fromCoordinates("G8"),
                        promotion = Piece.Queen(Piece.Color.White)
                    )
                )
            )
            val gameSummary = entity.copy(
                turnColor = game.turnColor,
                state = game.state
            )
            repository.save(gameSummary, game.moves)
            repository.getById(gameSummary.id) shouldBe gameSummary
        }
    }

    override fun create(index: Int) = repository.save()

    override fun GameSummary.atUtc() = copy(creationDate = creationDate.withOffsetSameInstant(ZoneOffset.UTC))
}
