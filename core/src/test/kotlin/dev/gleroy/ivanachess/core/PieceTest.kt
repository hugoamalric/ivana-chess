@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.match
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

        @Nested
        inner class initialPos {
            @Test
            fun `should be E1 if color is white`() {
                Piece.King(Piece.Color.White).initialPos shouldBe Position.fromCoordinates("E1")
            }

            @Test
            fun `should be E8 if color is black`() {
                Piece.King(Piece.Color.Black).initialPos shouldBe Position.fromCoordinates("E8")
            }
        }

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
            test(
                name = "possible_boards_02",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G7", "G6"),
                    Move.fromCoordinates("H5", "E5"),
                )
            )
        }

        @Test
        fun possibleBoards03_1() {
            test(
                name = "possible_boards_03",
                color = Piece.Color.White,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G8", "H6"),
                    Move.fromCoordinates("H5", "E5"),
                    Move.fromCoordinates("F8", "E7"),
                    Move.fromCoordinates("E5", "G7"),
                    Move.fromCoordinates("H8", "F8"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("F7", "F6"),
                    Move.fromCoordinates("D2", "D3"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("C4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("G1", "G3"),
                    Move.fromCoordinates("C7", "C6"),
                    Move.fromCoordinates("D5", "D6"),
                    Move.fromCoordinates("F6", "F5"),
                    Move.fromCoordinates("G1", "H3"),
                    Move.fromCoordinates("F5", "F4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("F8", "F4"),
                    Move.fromCoordinates("B1", "B3"),
                    Move.fromCoordinates("E7", "G5"),
                    Move.fromCoordinates("D6", "D7"),
                    Move.fromCoordinates("E8", "D8")
                )
            )
        }

        @Test
        fun possibleBoards03_2() {
            test(
                name = "possible_boards_03",
                color = Piece.Color.White,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G8", "H6"),
                    Move.fromCoordinates("H5", "E5"),
                    Move.fromCoordinates("F8", "E7"),
                    Move.fromCoordinates("E5", "G7"),
                    Move.fromCoordinates("H8", "F8"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("F7", "F6"),
                    Move.fromCoordinates("D2", "D3"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("C4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("G1", "G3"),
                    Move.fromCoordinates("C7", "C6"),
                    Move.fromCoordinates("D5", "D6"),
                    Move.fromCoordinates("F6", "F5"),
                    Move.fromCoordinates("G1", "H3"),
                    Move.fromCoordinates("F5", "F4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("F8", "F4"),
                    Move.fromCoordinates("B1", "B3"),
                    Move.fromCoordinates("E7", "G5"),
                    Move.fromCoordinates("D6", "D7"),
                    Move.fromCoordinates("E8", "D8"),
                    Move.fromCoordinates("F3", "F4"),
                    Move.fromCoordinates("E1", "F1"),
                    Move.fromCoordinates("F4", "F3"),
                    Move.fromCoordinates("F1", "E1")
                ),
                "E1C1.txt", "E1G1.txt"
            )
        }

        @Test
        fun possibleBoards03_3() {
            test(
                name = "possible_boards_03",
                color = Piece.Color.White,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G8", "H6"),
                    Move.fromCoordinates("H5", "E5"),
                    Move.fromCoordinates("F8", "E7"),
                    Move.fromCoordinates("E5", "G7"),
                    Move.fromCoordinates("H8", "F8"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("F7", "F6"),
                    Move.fromCoordinates("D2", "D3"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("C4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("G1", "G3"),
                    Move.fromCoordinates("C7", "C6"),
                    Move.fromCoordinates("D5", "D6"),
                    Move.fromCoordinates("F6", "F5"),
                    Move.fromCoordinates("G1", "H3"),
                    Move.fromCoordinates("F5", "F4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("F8", "F4"),
                    Move.fromCoordinates("B1", "B3"),
                    Move.fromCoordinates("E7", "G5"),
                    Move.fromCoordinates("D6", "D7"),
                    Move.fromCoordinates("E8", "D8"),
                    Move.fromCoordinates("F3", "F4"),
                    Move.fromCoordinates("H1", "G1"),
                    Move.fromCoordinates("F4", "F3"),
                    Move.fromCoordinates("G1", "H1")
                ),
                "E1G1.txt"
            )
        }

        @Test
        fun possibleBoards03_4() {
            test(
                name = "possible_boards_03",
                color = Piece.Color.White,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G8", "H6"),
                    Move.fromCoordinates("H5", "E5"),
                    Move.fromCoordinates("F8", "E7"),
                    Move.fromCoordinates("E5", "G7"),
                    Move.fromCoordinates("H8", "F8"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("F7", "F6"),
                    Move.fromCoordinates("D2", "D3"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("C4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("G1", "G3"),
                    Move.fromCoordinates("C7", "C6"),
                    Move.fromCoordinates("D5", "D6"),
                    Move.fromCoordinates("F6", "F5"),
                    Move.fromCoordinates("G1", "H3"),
                    Move.fromCoordinates("F5", "F4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("F8", "F4"),
                    Move.fromCoordinates("B1", "B3"),
                    Move.fromCoordinates("E7", "G5"),
                    Move.fromCoordinates("D6", "D7"),
                    Move.fromCoordinates("E8", "D8"),
                    Move.fromCoordinates("F3", "F4"),
                    Move.fromCoordinates("A1", "B1"),
                    Move.fromCoordinates("F4", "F3"),
                    Move.fromCoordinates("B1", "A1")
                ),
                "E1C1.txt"
            )
        }

        @Test
        fun possibleBoards04() {
            test(
                name = "possible_boards_04",
                color = Piece.Color.White,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("E7", "E5"),
                    Move.fromCoordinates("D1", "H5"),
                    Move.fromCoordinates("G8", "H6"),
                    Move.fromCoordinates("H5", "E5"),
                    Move.fromCoordinates("F8", "E7"),
                    Move.fromCoordinates("E5", "G7"),
                    Move.fromCoordinates("H8", "F8"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("F7", "F6"),
                    Move.fromCoordinates("D2", "D3"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("C4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("G1", "G3"),
                    Move.fromCoordinates("C7", "C6"),
                    Move.fromCoordinates("D5", "D6"),
                    Move.fromCoordinates("F6", "F5"),
                    Move.fromCoordinates("G1", "H3"),
                    Move.fromCoordinates("F5", "F4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("F8", "F4"),
                    Move.fromCoordinates("B1", "B3"),
                    Move.fromCoordinates("E7", "G5"),
                    Move.fromCoordinates("D6", "D7"),
                    Move.fromCoordinates("E8", "D8"),
                    Move.fromCoordinates("F4", "F3"),
                    Move.fromCoordinates("D3", "D4"),
                )
            )
        }

        @Test
        fun possibleBoards05_1() {
            test(
                name = "possible_boards_05",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("B1", "C3"),
                    Move.fromCoordinates("G8", "F6"),
                    Move.fromCoordinates("G1", "F3"),
                    Move.fromCoordinates("E7", "E6"),
                    Move.fromCoordinates("F3", "G5"),
                    Move.fromCoordinates("F8", "B4"),
                    Move.fromCoordinates("A2", "A3"),
                    Move.fromCoordinates("D8", "D6"),
                    Move.fromCoordinates("A3", "B4"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("B4", "B5"),
                    Move.fromCoordinates("B8", "A6"),
                    Move.fromCoordinates("B5", "A6"),
                    Move.fromCoordinates("B7", "A6"),
                    Move.fromCoordinates("C3", "B5"),
                    Move.fromCoordinates("A6", "B5"),
                    Move.fromCoordinates("G5", "D6"),
                    Move.fromCoordinates("C7", "D6"),
                    Move.fromCoordinates("C1", "G5"),
                    Move.fromCoordinates("E6", "E5"),
                    Move.fromCoordinates("E2", "E3"),
                    Move.fromCoordinates("E5", "D4"),
                    Move.fromCoordinates("G2", "G3"),
                    Move.fromCoordinates("D7", "H3"),
                    Move.fromCoordinates("F1", "H3"),
                    Move.fromCoordinates("F6", "D7"),
                    Move.fromCoordinates("B2", "B4"),
                )
            )
        }

        @Test
        fun possibleBoards05_2() {
            test(
                name = "possible_boards_05",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("B1", "C3"),
                    Move.fromCoordinates("G8", "F6"),
                    Move.fromCoordinates("G1", "F3"),
                    Move.fromCoordinates("E7", "E6"),
                    Move.fromCoordinates("F3", "G5"),
                    Move.fromCoordinates("F8", "B4"),
                    Move.fromCoordinates("A2", "A3"),
                    Move.fromCoordinates("D8", "D6"),
                    Move.fromCoordinates("A3", "B4"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("B4", "B5"),
                    Move.fromCoordinates("B8", "A6"),
                    Move.fromCoordinates("B5", "A6"),
                    Move.fromCoordinates("B7", "A6"),
                    Move.fromCoordinates("C3", "B5"),
                    Move.fromCoordinates("A6", "B5"),
                    Move.fromCoordinates("G5", "D6"),
                    Move.fromCoordinates("C7", "D6"),
                    Move.fromCoordinates("C1", "G5"),
                    Move.fromCoordinates("E6", "E5"),
                    Move.fromCoordinates("E2", "E3"),
                    Move.fromCoordinates("E5", "D4"),
                    Move.fromCoordinates("G2", "G3"),
                    Move.fromCoordinates("D7", "H3"),
                    Move.fromCoordinates("F1", "H3"),
                    Move.fromCoordinates("F6", "D7"),
                    Move.fromCoordinates("B2", "B4"),
                    Move.fromCoordinates("E8", "F8"),
                    Move.fromCoordinates("G5", "H4"),
                    Move.fromCoordinates("F8", "E8"),
                    Move.fromCoordinates("H4", "G5"),
                ),
                "E8C8.txt", "E8G8.txt"
            )
        }

        @Test
        fun possibleBoards05_3() {
            test(
                name = "possible_boards_05",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("B1", "C3"),
                    Move.fromCoordinates("G8", "F6"),
                    Move.fromCoordinates("G1", "F3"),
                    Move.fromCoordinates("E7", "E6"),
                    Move.fromCoordinates("F3", "G5"),
                    Move.fromCoordinates("F8", "B4"),
                    Move.fromCoordinates("A2", "A3"),
                    Move.fromCoordinates("D8", "D6"),
                    Move.fromCoordinates("A3", "B4"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("B4", "B5"),
                    Move.fromCoordinates("B8", "A6"),
                    Move.fromCoordinates("B5", "A6"),
                    Move.fromCoordinates("B7", "A6"),
                    Move.fromCoordinates("C3", "B5"),
                    Move.fromCoordinates("A6", "B5"),
                    Move.fromCoordinates("G5", "D6"),
                    Move.fromCoordinates("C7", "D6"),
                    Move.fromCoordinates("C1", "G5"),
                    Move.fromCoordinates("E6", "E5"),
                    Move.fromCoordinates("E2", "E3"),
                    Move.fromCoordinates("E5", "D4"),
                    Move.fromCoordinates("G2", "G3"),
                    Move.fromCoordinates("D7", "H3"),
                    Move.fromCoordinates("F1", "H3"),
                    Move.fromCoordinates("F6", "D7"),
                    Move.fromCoordinates("B2", "B4"),
                    Move.fromCoordinates("H8", "G8"),
                    Move.fromCoordinates("G5", "H4"),
                    Move.fromCoordinates("G8", "H8"),
                    Move.fromCoordinates("H4", "G5"),
                ),
                "E8G8.txt"
            )
        }

        @Test
        fun possibleBoards05_4() {
            test(
                name = "possible_boards_05",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("B1", "C3"),
                    Move.fromCoordinates("G8", "F6"),
                    Move.fromCoordinates("G1", "F3"),
                    Move.fromCoordinates("E7", "E6"),
                    Move.fromCoordinates("F3", "G5"),
                    Move.fromCoordinates("F8", "B4"),
                    Move.fromCoordinates("A2", "A3"),
                    Move.fromCoordinates("D8", "D6"),
                    Move.fromCoordinates("A3", "B4"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("B4", "B5"),
                    Move.fromCoordinates("B8", "A6"),
                    Move.fromCoordinates("B5", "A6"),
                    Move.fromCoordinates("B7", "A6"),
                    Move.fromCoordinates("C3", "B5"),
                    Move.fromCoordinates("A6", "B5"),
                    Move.fromCoordinates("G5", "D6"),
                    Move.fromCoordinates("C7", "D6"),
                    Move.fromCoordinates("C1", "G5"),
                    Move.fromCoordinates("E6", "E5"),
                    Move.fromCoordinates("E2", "E3"),
                    Move.fromCoordinates("E5", "D4"),
                    Move.fromCoordinates("G2", "G3"),
                    Move.fromCoordinates("D7", "H3"),
                    Move.fromCoordinates("F1", "H3"),
                    Move.fromCoordinates("F6", "D7"),
                    Move.fromCoordinates("B2", "B4"),
                    Move.fromCoordinates("A8", "B8"),
                    Move.fromCoordinates("G5", "H4"),
                    Move.fromCoordinates("B8", "A8"),
                    Move.fromCoordinates("H4", "G5"),
                ),
                "E8C8.txt"
            )
        }

        @Test
        fun possibleBoards06() {
            test(
                name = "possible_boards_06",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("B1", "C3"),
                    Move.fromCoordinates("G8", "F6"),
                    Move.fromCoordinates("G1", "F3"),
                    Move.fromCoordinates("E7", "E6"),
                    Move.fromCoordinates("F3", "G5"),
                    Move.fromCoordinates("F8", "B4"),
                    Move.fromCoordinates("A2", "A3"),
                    Move.fromCoordinates("D8", "D6"),
                    Move.fromCoordinates("A3", "B4"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("B4", "B5"),
                    Move.fromCoordinates("B8", "A6"),
                    Move.fromCoordinates("B5", "A6"),
                    Move.fromCoordinates("B7", "A6"),
                    Move.fromCoordinates("C3", "B5"),
                    Move.fromCoordinates("A6", "B5"),
                    Move.fromCoordinates("G5", "D6"),
                    Move.fromCoordinates("C7", "D6"),
                    Move.fromCoordinates("C1", "G5"),
                    Move.fromCoordinates("E6", "E5"),
                    Move.fromCoordinates("E2", "E3"),
                    Move.fromCoordinates("E5", "D4"),
                    Move.fromCoordinates("G2", "G3"),
                    Move.fromCoordinates("D7", "H3"),
                    Move.fromCoordinates("F1", "H3"),
                    Move.fromCoordinates("F6", "D7"),
                    Move.fromCoordinates("B2", "B4"),
                    Move.fromCoordinates("A8", "B8"),
                    Move.fromCoordinates("G5", "H4"),
                    Move.fromCoordinates("B8", "A8"),
                    Move.fromCoordinates("H4", "G5"),
                    Move.fromCoordinates("D7", "H5"),
                    Move.fromCoordinates("E3", "D4"),
                )
            )
        }

        @Test
        fun possibleBoards07() {
            test(
                name = "possible_boards_07",
                color = Piece.Color.Black,
                moves = listOf(
                    Move.fromCoordinates("E2", "E4"),
                    Move.fromCoordinates("D7", "D5"),
                    Move.fromCoordinates("E4", "D5"),
                    Move.fromCoordinates("D8", "D5"),
                    Move.fromCoordinates("C2", "C4"),
                    Move.fromCoordinates("D5", "C4"),
                    Move.fromCoordinates("F1", "C4"),
                    Move.fromCoordinates("B7", "B5"),
                    Move.fromCoordinates("B8", "C6"),
                    Move.fromCoordinates("B5", "C6"),
                    Move.fromCoordinates("C8", "D7"),
                    Move.fromCoordinates("C6", "A8"),
                    Move.fromCoordinates("C6", "A8"),
                    Move.fromCoordinates("C7", "C5"),
                    Move.fromCoordinates("D2", "D4"),
                    Move.fromCoordinates("C5", "C4"),
                    Move.fromCoordinates("C1", "F4"),
                    Move.fromCoordinates("C4", "C3"),
                    Move.fromCoordinates("D1", "G4"),
                    Move.fromCoordinates("C3", "C2"),
                    Move.fromCoordinates("A2", "A4"),
                    Move.fromCoordinates("D7", "G4"),
                    Move.fromCoordinates("B2", "B4"),
                )
            )
        }

        private fun test(
            name: String,
            color: Piece.Color,
            moves: List<Move> = emptyList(),
            vararg filenameExclusions: String
        ) {
            val fileNameRegex = Regex("^([A-H][1-8])([A-H][1-8])(_[QRKB])?\\.txt$")
            val dir = Paths.get(javaClass.getResource("/pieces/$name").toURI())
            val initialBoardPath = dir.resolve("initial.txt")
            val initialBoard = deserializer.deserialize(Files.newInputStream(initialBoardPath).readAllBytes())
            val expectedPossibleMoves = Files.walk(dir)
                .filter { path ->
                    path.fileName.toString().let { fileName ->
                        fileName.endsWith(".txt") &&
                                !filenameExclusions.contains(fileName) &&
                                path != initialBoardPath
                    }
                }
                .map { path ->
                    val fileName = path.fileName.toString()
                    val matcher = fileNameRegex.matchEntire(fileName)
                        ?: throw IllegalStateException("$fileName does not match ${fileNameRegex.pattern}")
                    val from = Position.fromCoordinates(matcher.groups[1]!!.value)
                    val to = Position.fromCoordinates(matcher.groups[2]!!.value)
                    val board = try {
                        deserializer.deserialize(Files.newInputStream(path).readAllBytes())
                    } catch (exception: IllegalArgumentException) {
                        throw IllegalArgumentException("Unable to load $path: ${exception.message}")
                    }
                    PossibleMove(Move(from, to), board)
                }
                .collect(Collectors.toSet())
            val possibleMoves = initialBoard.pieces(color)
                .flatMap { it.piece.possibleMoves(initialBoard, it.pos, moves) }
                .toSet()
            try {
                possibleMoves shouldBe expectedPossibleMoves
            } catch (exception: AssertionFailedError) {
                val initialBoardStr = String(serializer.serialize(initialBoard))
                val unexpectedBoards = possibleMoves - expectedPossibleMoves
                if (unexpectedBoards.isNotEmpty()) {
                    val str = unexpectedBoards.joinToString("\n") { String(serializer.serialize(it.resultingBoard)) }
                    throw AssertionFailedError(
                        "Initial board:\n$initialBoardStr\n${unexpectedBoards.size} unexpected boards:\n$str"
                    )
                }
                val missingBoards = expectedPossibleMoves - possibleMoves
                if (missingBoards.isNotEmpty()) {
                    val str = missingBoards.joinToString("\n") { String(serializer.serialize(it.resultingBoard)) }
                    throw AssertionFailedError(
                        "Initial board:\n$initialBoardStr\n${missingBoards.size} missing boards:\n$str"
                    )
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
