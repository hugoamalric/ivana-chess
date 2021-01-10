@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PieceTest {
    private val deserializer = StringBoardDeserializer()

    @Nested
    inner class Color {
        @Nested
        inner class opponent {
            @Test
            fun `should return black if color is white`() {
                Piece.Color.White.opponent() shouldBe Piece.Color.Black
            }

            @Test
            fun `should return white if color is black`() {
                Piece.Color.Black.opponent() shouldBe Piece.Color.White
            }
        }
    }

    @Nested
    inner class Bishop : Common() {
        override val whiteSymbol = Piece.Bishop.WhiteSymbol
        override val blackSymbol = Piece.Bishop.BlackSymbol

        @Nested
        inner class possiblePositions {
            private val piece = Piece.Bishop(Piece.Color.White)

            @Test
            fun test01() {
                testPossiblePositions("test01", piece, "D3", "A6", "B5", "C4", "E2", "F1")
            }

            @Test
            fun test02() {
                testPossiblePositions("test02", piece, "B4", "C5", "D6", "E7", "F8", "A5", "C3", "A3")
            }

            @Test
            fun test03() {
                testPossiblePositions("test03", piece, "B4")
            }
        }

        override fun instantiate(color: Piece.Color) = Piece.Bishop(color)
    }

    @Nested
    inner class King : Common() {
        override val whiteSymbol = Piece.King.WhiteSymbol
        override val blackSymbol = Piece.King.BlackSymbol

        override fun instantiate(color: Piece.Color) = Piece.King(color)
    }

    @Nested
    inner class Knight : Common() {
        override val whiteSymbol = Piece.Knight.WhiteSymbol
        override val blackSymbol = Piece.Knight.BlackSymbol

        override fun instantiate(color: Piece.Color) = Piece.Knight(color)
    }

    @Nested
    inner class Pawn : Common() {
        override val whiteSymbol = Piece.Pawn.WhiteSymbol
        override val blackSymbol = Piece.Pawn.BlackSymbol

        override fun instantiate(color: Piece.Color) = Piece.Pawn(color)
    }

    @Nested
    inner class Queen : Common() {
        override val whiteSymbol = Piece.Queen.WhiteSymbol
        override val blackSymbol = Piece.Queen.BlackSymbol

        override fun instantiate(color: Piece.Color) = Piece.Queen(color)
    }

    @Nested
    inner class Rook : Common() {
        override val whiteSymbol = Piece.Rook.WhiteSymbol
        override val blackSymbol = Piece.Rook.BlackSymbol

        override fun instantiate(color: Piece.Color) = Piece.Rook(color)
    }

    abstract inner class Common {
        @Nested
        inner class constructor {
            @Test
            fun `should initialize white symbol`() {
                shouldInitializeSymbol(Piece.Color.White, whiteSymbol)
            }

            @Test
            fun `should initialize black symbol`() {
                shouldInitializeSymbol(Piece.Color.Black, blackSymbol)
            }

            private fun shouldInitializeSymbol(color: Piece.Color, symbol: Char) {
                val piece = instantiate(color)
                piece.symbol shouldBe symbol
            }
        }

        @Nested
        inner class toString {
            @Test
            fun `should return symbol`() {
                val piece = instantiate(Piece.Color.White)
                piece.toString() shouldBe piece.symbol.toString()
            }
        }

        protected abstract val whiteSymbol: Char
        protected abstract val blackSymbol: Char

        protected abstract fun instantiate(color: Piece.Color): Piece

        protected fun testPossiblePositions(
            name: String,
            piece: Piece,
            pieceCoordinates: String,
            vararg expectedCoordinates: String
        ) {
            val path = "/pieces/${javaClass.simpleName.toLowerCase()}/$name.txt"
            val board = deserializer.deserialize(javaClass.getResourceAsStream(path).readAllBytes())
            val position = Position.fromCoordinates(pieceCoordinates)
            val expectedPositions = expectedCoordinates.map { Position.fromCoordinates(it) }.toSet()
            piece.possiblePositions(board, position) shouldBe expectedPositions
        }
    }
}
