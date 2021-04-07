@file:Suppress("ClassName", "SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import io.kotlintest.matchers.collections.shouldBeEmpty
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Clock
import java.time.OffsetDateTime

@SpringBootTest
@ActiveProfiles("dev")
internal class GameDatabaseRepositoryTest :
    AbstractDatabaseEntityRepositoryTest<GameEntity, GameDatabaseRepository>() {

    private val whitePlayer = User(
        pseudo = "white",
        email = "white@ivanachess.loc",
        creationDate = OffsetDateTime.now(Clock.systemUTC()),
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )
    private val blackPlayer = User(
        pseudo = "black",
        email = "black@ivanachess.loc",
        creationDate = OffsetDateTime.now(Clock.systemUTC()),
        bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
    )

    @Autowired
    private lateinit var userRepository: UserDatabaseRepository

    @BeforeEach
    override fun beforeEach() {
        userRepository.save(whitePlayer)
        userRepository.save(blackPlayer)
        super.beforeEach()
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
            gameEntity = entities[0]
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

    override fun createEntity(index: Int) = GameEntity(
        creationDate = OffsetDateTime.now(Clock.systemUTC()),
        whitePlayer = whitePlayer,
        blackPlayer = blackPlayer,
    )

    override fun updateEntity(entity: GameEntity) = entity.copy()
}
