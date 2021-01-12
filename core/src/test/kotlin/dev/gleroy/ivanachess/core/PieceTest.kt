@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

internal class PieceTest {
    private val serializer = StringBoardSerializer()
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

    @Nested
    inner class possibleBoards {
        @Test
        fun possibleBoards01() {
            test("possible_boards_01", Piece.Color.White)
        }

        @Test
        fun possibleBoards02() {
            test("possible_boards_02", Piece.Color.Black, "D4", "D5", "B6")
        }

        @Test
        fun possibleBoards03() {
            test("possible_boards_03", Piece.Color.Black, "D4", "A5", "B6")
        }

        @Test
        fun possibleBoards04() {
            test("possible_boards_04", Piece.Color.White, "A4", "D4", "F3")
        }

        private fun test(name: String, color: Piece.Color, vararg pieceHasAlreadyMovedCoordinates: String) {
            val pieceHasAlreadyMovedPositions = pieceHasAlreadyMovedCoordinates.map { Position.fromCoordinates(it) }
            val dir = Paths.get(javaClass.getResource("/pieces/$name").toURI())
            val initialBoardPath = dir.resolve("000.txt")
            val initialBoard = deserializer.deserialize(Files.newInputStream(initialBoardPath).readAllBytes())
            val expectedBoard = Files.walk(dir)
                .filter { it.toString().endsWith(".txt") && it != initialBoardPath }
                .map { deserializer.deserialize(Files.newInputStream(it).readAllBytes()) }
                .collect(Collectors.toSet())
            val boards = initialBoard.pieces(color)
                .flatMap { positionedPiece ->
                    positionedPiece.piece.possibleBoards(
                        board = initialBoard,
                        pos = positionedPiece.pos,
                        hasAlreadyMoved = pieceHasAlreadyMovedPositions.contains(positionedPiece.pos)
                    )
                }
                .toSet()
            try {
                boards shouldBe expectedBoard
            } catch (exception: AssertionFailedError) {
                val unexpectedBoards = boards - expectedBoard
                if (unexpectedBoards.isNotEmpty()) {
                    val str = unexpectedBoards.joinToString("\n\n") { String(serializer.serialize(it)) }
                    throw AssertionFailedError("Unexpected boards:\n$str")
                }
                val missingBoards = expectedBoard - boards
                if (missingBoards.isNotEmpty()) {
                    val str = missingBoards.joinToString("\n\n") { String(serializer.serialize(it)) }
                    throw AssertionFailedError("Missing boards:\n$str")
                }
                throw exception
            }
        }
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
            fun targeting_01() {
                test("targeting_01", "D4", test01TargetingCoordinates)
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

        private fun loadBoardFile(name: String, piece: Piece): Board {
            val path = "/pieces/$name.txt"
            val boardStr = String(javaClass.getResourceAsStream(path).readAllBytes())
            return deserializer.deserialize(boardStr.replace('X', piece.symbol).toByteArray())
        }
    }
}
