@file:Suppress("ClassName")

package dev.gleroy.ivanachess.core

import io.kotlintest.matchers.throwable.shouldHaveMessage
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class MoveTest {
    private val board = Board.Initial
    private val allPositions = Position.all()
    private val whiteKingPos = Piece.King(Piece.Color.White).initialPos
    private val whiteCastlingKingTargetPositions = setOf(
        Position.fromCoordinates("C1"),
        Position.fromCoordinates("G1")
    )
    private val blackKingPos = Piece.King(Piece.Color.Black).initialPos
    private val blackCastlingKingTargetPositions = setOf(
        Position.fromCoordinates("C8"),
        Position.fromCoordinates("G8")
    )
    private val simpleMoves = allPositions
        .flatMap { kingPos ->
            allPositions
                .filterNot { it == kingPos }
                .map { kingPos to it }
        }
        .filterNot { it.first == whiteKingPos && whiteCastlingKingTargetPositions.contains(it.second) }
        .filterNot { it.first == blackKingPos && blackCastlingKingTargetPositions.contains(it.second) }
        .map { Move.Simple(it.first, it.second) }

    @Nested
    inner class fromCoordinates {
        @Test
        fun `should return move`() {
            val from = Position.fromCoordinates("E1")
            val to = Position.fromCoordinates("C1")
            Move.Simple.fromCoordinates(from.toString(), to.toString()) shouldBe Move.Simple(from, to)
        }
    }

    @Nested
    inner class Simple {
        @Nested
        inner class execute {
            @Test
            fun `should throw exception if no piece at start position`() {
                val move = Move.Simple.fromCoordinates("A3", "A4")
                val exception = assertThrows<IllegalStateException> { move.execute(board) }
                exception shouldHaveMessage "No piece at position ${move.from}"
            }

            @Test
            fun `should return new board with pawn moved from B2 to B3`() {
                val move = Move.Simple.fromCoordinates("B2", "B3")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                movePiece(pieceByPosition, move.from, move.to)
                move.execute(board) shouldBe Board(pieceByPosition)
            }

            @Test
            fun `should execute white left castling`() {
                val move = Move.Simple.fromCoordinates("E1", "C1")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                movePiece(pieceByPosition, move.from, move.to)
                movePiece(pieceByPosition, Position.fromCoordinates("A1"), Position.fromCoordinates("D1"))
                move.execute(board) shouldBe Board(pieceByPosition)
            }

            @Test
            fun `should execute black left castling`() {
                val move = Move.Simple.fromCoordinates("E8", "C8")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                movePiece(pieceByPosition, move.from, move.to)
                movePiece(pieceByPosition, Position.fromCoordinates("A8"), Position.fromCoordinates("D8"))
                move.execute(board) shouldBe Board(pieceByPosition)
            }

            @Test
            fun `should execute white right castling`() {
                val move = Move.Simple.fromCoordinates("E1", "G1")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                movePiece(pieceByPosition, move.from, move.to)
                movePiece(pieceByPosition, Position.fromCoordinates("H1"), Position.fromCoordinates("F1"))
                move.execute(board) shouldBe Board(pieceByPosition)
            }

            @Test
            fun `should execute black right castling`() {
                val move = Move.Simple.fromCoordinates("E8", "G8")
                val pieceByPosition = board.pieceByPosition.toMutableMap()
                movePiece(pieceByPosition, move.from, move.to)
                movePiece(pieceByPosition, Position.fromCoordinates("H8"), Position.fromCoordinates("F8"))
                move.execute(board) shouldBe Board(pieceByPosition)
            }
        }
    }

    private fun movePiece(pieceByPosition: MutableMap<Position, Piece>, from: Position, to: Position) {
        val piece = pieceByPosition[from]!!
        pieceByPosition.remove(from)
        pieceByPosition[to] = piece
    }
}
