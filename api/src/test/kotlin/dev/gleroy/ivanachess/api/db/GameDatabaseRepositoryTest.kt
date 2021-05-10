@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.CommonEntityField
import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.core.GameField
import dev.gleroy.ivanachess.core.ItemFilter
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("dev")
internal class GameDatabaseRepositoryTest :
    AbstractEntityDatabaseRepositoryTest<GameEntity, GameDatabaseRepository>() {

    @BeforeEach
    override fun beforeEach() {
        super.beforeEach()
        items = games
        repository = gameRepository
    }

    @Nested
    open inner class fetchPage : AbstractEntityDatabaseRepositoryTest<GameEntity, GameDatabaseRepository>.fetchPage() {
        @Test
        fun `should return page filtered by state`() {
            shouldReturnPage(
                sortedField = CommonEntityField.Id,
                sortedItems = items
                    .filter { it.state == Game.State.InGame }
                    .sortedBy { it.id.toString() },
                filters = setOf(ItemFilter(GameField.State, "in_game"))
            )
        }
    }

    @Nested
    inner class saveMoves {
        private val game = Game(
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
                ),
                Move.Simple.fromCoordinates("H6", "H5"),
                Move.Simple.fromCoordinates("F1", "C4"),
                Move.Simple.fromCoordinates("D7", "D6"),
            )
        )

        private lateinit var gameEntity: GameEntity

        @BeforeEach
        fun beforeEach() {
            gameEntity = items[0]
            repository.saveMoves(gameEntity.id, game.moves)
        }

        @Test
        fun `should delete all moves if moves list is empty`() {
            repository.saveMoves(gameEntity.id, emptyList())
            repository.fetchMoves(gameEntity.id).shouldBeEmpty()
        }

        @Test
        fun `should update moves`() {
            val game = game.play(Move.Simple.fromCoordinates("G8", "F7"))
            repository.saveMoves(gameEntity.id, game.moves)
            repository.fetchMoves(gameEntity.id) shouldBe game.moves
        }
    }

    override fun updateEntity(entity: GameEntity) = entity.copy(
        turnColor = Piece.Color.Black,
        state = Game.State.Checkmate,
        winnerColor = Piece.Color.White,
    )
}
