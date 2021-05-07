@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.io

import dev.gleroy.ivanachess.core.GameEntity
import dev.gleroy.ivanachess.core.User
import dev.gleroy.ivanachess.game.Game
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.io.ColorRepresentation
import dev.gleroy.ivanachess.io.GameRepresentation
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class DefaultGameConverterTest {
    private val gameEntity = GameEntity(
        whitePlayer = User(
            pseudo = "white",
            email = "white@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        ),
        blackPlayer = User(
            pseudo = "black",
            email = "black@ivanachess.loc",
            bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
        )
    )

    private val userConverter = DefaultUserConverter()
    private val converter = DefaultGameConverter(
        userConverter = userConverter,
    )

    @Nested
    inner class convertRepresentation {
        private val gameRepresentation = GameRepresentation.Summary(
            id = gameEntity.id,
            creationDate = gameEntity.creationDate,
            whitePlayer = userConverter.convertToPublicRepresentation(gameEntity.whitePlayer),
            blackPlayer = userConverter.convertToPublicRepresentation(gameEntity.blackPlayer),
            turnColor = ColorRepresentation.White,
            state = GameRepresentation.State.InGame,
            winnerColor = null,
        )

        @Test
        fun `should return in_game representation`() {
            converter.convertToRepresentation(gameEntity) shouldBe gameRepresentation
        }

        @Test
        fun `should return checkmate representation`() {
            converter.convertToRepresentation(
                gameEntity.copy(
                    state = Game.State.Checkmate,
                    winnerColor = Piece.Color.White,
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Checkmate,
                winnerColor = ColorRepresentation.White,
            )
        }

        @Test
        fun `should return stalemate representation`() {
            converter.convertToRepresentation(
                gameEntity.copy(
                    state = Game.State.Stalemate,
                )
            ) shouldBe gameRepresentation.copy(
                state = GameRepresentation.State.Stalemate,
            )
        }
    }
}
