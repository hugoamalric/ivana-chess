@file:Suppress("ClassName")

package dev.gleroy.ivanachess.dto

import dev.gleroy.ivanachess.core.Game
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameDtoTest {
    @Nested
    inner class State {
        @Nested
        inner class from {
            @Test
            fun `should return in_game`() {
                GameDto.State.from(Game.State.InGame) shouldBe GameDto.State.InGame
            }

            @Test
            fun `should return checkmate`() {
                GameDto.State.from(Game.State.Checkmate) shouldBe GameDto.State.Checkmate
            }

            @Test
            fun `should return stalemate`() {
                GameDto.State.from(Game.State.Stalemate) shouldBe GameDto.State.Stalemate
            }
        }
    }
}
