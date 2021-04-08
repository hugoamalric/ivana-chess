@file:Suppress("ClassName")

package dev.gleroy.ivanachess.game

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class MoveTest {
    @Nested
    inner class fromCoordinates {
        @Test
        fun `should return move`() {
            val from = Position.fromCoordinates("E1")
            val to = Position.fromCoordinates("C1")
            Move.Simple.fromCoordinates(from.toString(), to.toString()) shouldBe Move.Simple(from, to)
        }
    }
}
