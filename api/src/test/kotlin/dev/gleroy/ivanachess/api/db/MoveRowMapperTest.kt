@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.game.Piece
import dev.gleroy.ivanachess.game.Position
import io.kotlintest.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.sql.ResultSet

internal class MoveRowMapperTest {
    private val alias = "m"
    private val mapper = MoveRowMapper(alias)

    @Nested
    inner class mapRow {
        private lateinit var resultSet: ResultSet

        @BeforeEach
        fun beforeEach() {
            resultSet = mockk()
        }

        @Test
        fun `should return simple move`() {
            val move = Move.Simple(Position.fromCoordinates("E1"), Position.fromCoordinates("E2"))
            every {
                resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias))
            } returns move.from.toString()
            every {
                resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias))
            } returns move.to.toString()
            every { resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias)) } returns null

            mapper.mapRow(resultSet, 1) shouldBe move

            verify { resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }

        @Test
        fun `should return white promotion move`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("E1"),
                to = Position.fromCoordinates("E2"),
                promotion = Piece.Queen(Piece.Color.White)
            )
            every { resultSet.getInt(DatabaseConstants.Move.OrderColumnName.withAlias(alias)) } returns 1
            every {
                resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias))
            } returns move.from.toString()
            every {
                resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias))
            } returns move.to.toString()
            every {
                resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias))
            } returns PieceTypeSqlEnumValue.Queen.label

            mapper.mapRow(resultSet, 1) shouldBe move

            verify { resultSet.getInt(DatabaseConstants.Move.OrderColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }

        @Test
        fun `should return black promotion move`() {
            val move = Move.Promotion(
                from = Position.fromCoordinates("E1"),
                to = Position.fromCoordinates("E2"),
                promotion = Piece.Queen(Piece.Color.Black)
            )
            every { resultSet.getInt(DatabaseConstants.Move.OrderColumnName.withAlias(alias)) } returns 2
            every {
                resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias))
            } returns move.from.toString()
            every {
                resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias))
            } returns move.to.toString()
            every {
                resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias))
            } returns PieceTypeSqlEnumValue.Queen.label

            mapper.mapRow(resultSet, 1) shouldBe move

            verify { resultSet.getInt(DatabaseConstants.Move.OrderColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.FromColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.ToColumnName.withAlias(alias)) }
            verify { resultSet.getString(DatabaseConstants.Move.PromotionColumnName.withAlias(alias)) }
            confirmVerified(resultSet)
        }
    }
}
