@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameRepository
import dev.gleroy.ivanachess.api.GameSummary
import dev.gleroy.ivanachess.core.Move
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Database implementation of game repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class DatabaseGameRepository(
    override val jdbcTemplate: NamedParameterJdbcTemplate
) : AbstractDatabaseRepository<GameSummary>(), GameRepository {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DatabaseGameRepository::class.java)
    }

    override val tableName = DatabaseConstants.Game.TableName

    override val idColumnName = DatabaseConstants.Game.IdColumnName

    override val creationDateColumnName = DatabaseConstants.Game.CreationDateColumnName

    /**
     * Row mapper for game summary.
     */
    override val rowMapper = GameSummaryRowMapper()

    /**
     * Row mapper for move.
     */
    private val moveRowMapper = MoveRowMapper()

    override fun getByToken(token: UUID) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "${DatabaseConstants.Game.TableName}"
            WHERE "${DatabaseConstants.Game.WhiteTokenColumnName}" = :token
                OR "${DatabaseConstants.Game.BlackTokenColumnName}" = :token
        """,
        mapOf("token" to token),
        rowMapper
    )

    override fun getMoves(id: UUID): List<Move> = jdbcTemplate.query(
        """
            SELECT *
            FROM "${DatabaseConstants.Move.TableName}"
            WHERE "${DatabaseConstants.Move.GameIdColumnName}" = :game_id
            ORDER BY "${DatabaseConstants.Move.OrderColumnName}"
        """,
        ComparableMapSqlParameterSource(mapOf("game_id" to id)),
        moveRowMapper
    )

    @Transactional
    override fun save(gameSummary: GameSummary, moves: List<Move>) = if (existsById(gameSummary.id)) {
        update(gameSummary, moves)
    } else {
        create(gameSummary, moves)
    }

    /**
     * Save new game in database.
     *
     * @param gameSummary Game summary.
     * @param moves List of moves since the begin of the game.
     * @return Saved game summary.
     */
    private fun create(gameSummary: GameSummary, moves: List<Move>): GameSummary {
        jdbcTemplate.update(
            """
                INSERT INTO "$tableName"
                (
                    "$idColumnName",
                    "$creationDateColumnName",
                    "${DatabaseConstants.Game.WhiteTokenColumnName}",
                    "${DatabaseConstants.Game.BlackTokenColumnName}"
                ) VALUES (
                    :id,
                    :creation_date,
                    :white_token,
                    :black_token
                )
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "id" to gameSummary.id,
                    "creation_date" to gameSummary.creationDate,
                    "white_token" to gameSummary.whiteToken,
                    "black_token" to gameSummary.blackToken
                )
            )
        )
        updateMoves(gameSummary, moves)
        Logger.debug("Game ${gameSummary.id} saved in database")
        return gameSummary
    }

    /**
     * Update game summary.
     *
     * @param gameSummary Game summary.
     * @param moves List of moves since the begin of the game.
     * @return Game summary.
     */
    private fun update(gameSummary: GameSummary, moves: List<Move>): GameSummary {
        jdbcTemplate.update(
            """
                UPDATE "$tableName"
                SET "${DatabaseConstants.Game.TurnColorColumnName}" = :turn_color::${DatabaseConstants.ColorType},
                    "${DatabaseConstants.Game.StateColumnName}" = :state::${DatabaseConstants.GameStateType}
                WHERE "$idColumnName" = :id
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "turn_color" to ColorType.from(gameSummary.turnColor).sqlValue,
                    "state" to GameStateType.from(gameSummary.state).sqlValue,
                    "id" to gameSummary.id
                )
            )
        )
        updateMoves(gameSummary, moves)
        Logger.debug("Game ${gameSummary.id} updated")
        return gameSummary
    }

    /**
     * Update game moves.
     *
     * @param gameSummary New game.
     * @param moves List of moves since the begin of the game.
     */
    private fun updateMoves(gameSummary: GameSummary, moves: List<Move>) {
        val previousMoves = getMoves(gameSummary.id)
        val newMoves = moves - previousMoves
        if (newMoves.isNotEmpty()) {
            val insertIntoSql = """
                INSERT INTO "${DatabaseConstants.Move.TableName}"
                (
                    "${DatabaseConstants.Move.GameIdColumnName}",
                    "${DatabaseConstants.Move.OrderColumnName}",
                    "${DatabaseConstants.Move.FromColumnName}",
                    "${DatabaseConstants.Move.ToColumnName}",
                    "${DatabaseConstants.Move.PromotionColumnName}"
                )
                VALUES
            """
            val moveUpdate = newMoves
                .mapIndexed { i, move -> move.toMoveUpdate(previousMoves.size + i + 1) }
                .reduce { acc, moveUpdate ->
                    acc.copy(
                        sql = "${acc.sql}, ${moveUpdate.sql}",
                        params = acc.params + moveUpdate.params
                    )
                }
            jdbcTemplate.update(
                "$insertIntoSql ${moveUpdate.sql}",
                MapSqlParameterSource(moveUpdate.params + mapOf("game_id" to gameSummary.id))
            )
        }
    }

    /**
     * Move SQL update.
     *
     * @param sql SQL statement.
     * @param params Map which associates parameter name to its value.
     */
    private data class MoveUpdate(
        val sql: String,
        val params: Map<String, *>
    )

    /**
     * Convert move to move SQL update.
     *
     * @param order Move order.
     * @return Move SQL update.
     */
    private fun Move.toMoveUpdate(order: Int): MoveUpdate {
        val sql = """
            (:game_id, :order_$order, :from_$order, :to_$order, :promotion_$order::${DatabaseConstants.PieceType})
        """
        val promotion = when (this) {
            is Move.Simple -> null
            is Move.Promotion -> PieceType.from(promotion).sqlValue
        }
        return MoveUpdate(
            sql = sql,
            params = mapOf(
                "order_$order" to order,
                "from_$order" to from.toString(),
                "to_$order" to to.toString(),
                "promotion_$order" to promotion
            )
        )
    }
}
