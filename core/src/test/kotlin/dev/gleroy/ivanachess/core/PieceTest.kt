@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError

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
        override val test01TargetingCoordinates = setOf(
            "C5", "B6", "A7",
            "E5", "F6", "G7",
            "E3", "F2",
            "C3", "B2"
        )

        override fun instantiate(color: Piece.Color) = Piece.Bishop(color)
    }

    @Nested
    inner class King : Common() {
        override val whiteSymbol = Piece.King.WhiteSymbol
        override val blackSymbol = Piece.King.BlackSymbol
        override val test01TargetingCoordinates = setOf(
            "C5",
            "D5",
            "E5",
            "E4",
            "E3",
            "D3",
            "C3",
            "C4"
        )

        override fun instantiate(color: Piece.Color) = Piece.King(color)
    }

    @Nested
    inner class Knight : Common() {
        override val whiteSymbol = Piece.Knight.WhiteSymbol
        override val blackSymbol = Piece.Knight.BlackSymbol
        override val test01TargetingCoordinates = setOf(
            "C6", "E6",
            "F5", "F3",
            "E2", "C2",
            "B3", "B5"
        )

        override fun instantiate(color: Piece.Color) = Piece.Knight(color)
    }

    @Nested
    inner class Pawn : Common() {
        override val whiteSymbol = Piece.Pawn.WhiteSymbol
        override val blackSymbol = Piece.Pawn.BlackSymbol
        override val test01TargetingCoordinates = setOf("C5", "E5")

        override fun instantiate(color: Piece.Color) = Piece.Pawn(color)
    }

    @Nested
    inner class Queen : Common() {
        override val whiteSymbol = Piece.Queen.WhiteSymbol
        override val blackSymbol = Piece.Queen.BlackSymbol
        override val test01TargetingCoordinates = setOf(
            "C5", "B6", "A7",
            "D5", "D6", "D7",
            "E5", "F6", "G7",
            "E4", "F4", "G4", "H4",
            "E3", "F2",
            "D3", "D2",
            "C3", "B2",
            "C4", "B4", "A4"
        )

        override fun instantiate(color: Piece.Color) = Piece.Queen(color)
    }

    @Nested
    inner class Rook : Common() {
        override val whiteSymbol = Piece.Rook.WhiteSymbol
        override val blackSymbol = Piece.Rook.BlackSymbol
        override val test01TargetingCoordinates = setOf(
            "D5", "D6", "D7",
            "E4", "F4", "G4", "H4",
            "D3", "D2",
            "C4", "B4", "A4"
        )

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
        inner class isTargeting {
            @Test
            fun movements01() {
                test("movements01", "D4", test01TargetingCoordinates)
            }

            private fun test(name: String, pieceCoordinates: String, targetingCoordinates: Set<String>) {
                val piece = instantiate(Piece.Color.White)
                val board = loadBoardFile(name, piece)
                val pos = Position.fromCoordinates(pieceCoordinates)
                val targetingPositions = targetingCoordinates.map { Position.fromCoordinates(it) }
                Position.all().forEach { targetingPos ->
                    try {
                        piece.isTargeting(board, pos, targetingPos) shouldBe targetingPositions.contains(targetingPos)
                    } catch (exception: AssertionFailedError) {
                        throw AssertionFailedError("${exception.message} (targetingPos: $targetingPos)")
                    }
                }
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
        protected abstract val test01TargetingCoordinates: Set<String>

        protected abstract fun instantiate(color: Piece.Color): Piece

        protected fun testPossibleBoards(
            name: String,
            piece: Piece,
            pieceCoordinates: String,
            vararg expectedCoordinates: String
        ) {
            val board = loadBoardFile(name, piece)
            val pos = Position.fromCoordinates(pieceCoordinates)
            val expectedBoards = expectedCoordinates
                .map { board.movePiece(pos, Position.fromCoordinates(it)) }
                .toSet()
            piece.possibleBoards(board, pos) shouldBe expectedBoards
        }

        private fun loadBoardFile(name: String, piece: Piece): Board {
            val path = "/pieces/$name.txt"
            val boardStr = String(javaClass.getResourceAsStream(path).readAllBytes())
            return deserializer.deserialize(boardStr.replace('X', piece.symbol).toByteArray())
        }
    }
}
