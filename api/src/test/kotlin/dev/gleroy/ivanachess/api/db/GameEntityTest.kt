@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameInfo
import dev.gleroy.ivanachess.core.Game
import dev.gleroy.ivanachess.core.Move
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameEntityTest {
    @Nested
    inner class toGameInfo {
        private val gameInfo = GameInfo(
            game = Game(
                moves = listOf(Move.Simple.fromCoordinates("E1", "E2"))
            )
        )
        private val gameEntity = GameEntity(
            id = gameInfo.id,
            creationDate = gameInfo.creationDate,
            whiteToken = gameInfo.whiteToken,
            blackToken = gameInfo.blackToken
        )

        @Test
        fun `should return game info`() {
            gameEntity.toGameInfo(gameInfo.game.moves) shouldBe gameInfo
        }
    }
}
