@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.Page
import dev.gleroy.ivanachess.api.game.GameRepository
import dev.gleroy.ivanachess.api.game.GameSummary
import dev.gleroy.ivanachess.core.Move
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.math.ceil

/**
 * Database implementation of game repository.
 *
 * @param jdbcTemplate JDBC template.
 */
@Repository
class DatabaseGameRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : GameRepository {
    private companion object {
        /**
         * Logger.
         */
        private val Logger = LoggerFactory.getLogger(DatabaseGameRepository::class.java)
    }

    override fun existsById(id: UUID) = existsBy(DatabaseConstants.Game.IdColumnName, id)

    override fun getAll(page: Int, size: Int): Page<GameSummary> {
        require(page > 0) { "page must be strictly positive" }
        require(size > 0) { "size must be strictly positive" }
        val gameSummaries = jdbcTemplate.query(
            """
                SELECT *
                FROM "${DatabaseConstants.Game.TableName}"
                ORDER BY "${DatabaseConstants.Game.CreationDateColumnName}"
                OFFSET :offset
                LIMIT :limit
            """,
            ComparableMapSqlParameterSource(
                mapOf(
                    "offset" to (page - 1) * size,
                    "limit" to size
                )
            ),
            GameSummaryRowMapper()
        )
        val totalItems = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM "${DatabaseConstants.Game.TableName}"
            """,
            ComparableMapSqlParameterSource(),
            Int::class.java
        )!!
        return Page(
            content = gameSummaries,
            number = page,
            totalItems = totalItems,
            totalPages = ceil(totalItems.toDouble() / size.toDouble()).toInt()
        )
    }

    override fun getById(id: UUID) = getBy(DatabaseConstants.Game.IdColumnName, id)

    override fun getByToken(token: UUID) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "${DatabaseConstants.Game.TableName}"
            WHERE "${DatabaseConstants.Game.WhiteTokenColumnName}" = :token
                OR "${DatabaseConstants.Game.BlackTokenColumnName}" = :token
        """,
        mapOf("token" to token),
        GameSummaryRowMapper()
    )

    override fun getMoves(id: UUID): List<Move> = jdbcTemplate.query(
        """
            SELECT *
            FROM "${DatabaseConstants.Move.TableName}"
            WHERE "${DatabaseConstants.Move.GameIdColumnName}" = :game_id
            ORDER BY "${DatabaseConstants.Move.OrderColumnName}"
        """,
        ComparableMapSqlParameterSource(mapOf("game_id" to id)),
        MoveRowMapper()
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
                INSERT INTO "${DatabaseConstants.Game.TableName}"
                (
                    "${DatabaseConstants.Game.IdColumnName}",
                    "${DatabaseConstants.Game.CreationDateColumnName}",
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
     * Check if game exists by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return True if game exists, false otherwise.
     */
    private fun existsBy(columnName: String, columnValue: Any): Boolean = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT *
                FROM "${DatabaseConstants.Game.TableName}"
                WHERE "$columnName" = :value
            )
        """,
        ComparableMapSqlParameterSource(mapOf("value" to columnValue)),
        Boolean::class.java
    )!!

    /**
     * Get game by specific column value.
     *
     * @param columnName Column name.
     * @param columnValue Column value.
     * @return Game or null if it does not exist.
     */
    protected fun getBy(columnName: String, columnValue: Any) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "${DatabaseConstants.Game.TableName}"
            WHERE "$columnName" = :value
        """,
        mapOf("value" to columnValue),
        GameSummaryRowMapper()
    )

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
                UPDATE "${DatabaseConstants.Game.TableName}"
                SET "${DatabaseConstants.Game.TurnColorColumnName}" = :turn_color::${DatabaseConstants.ColorType},
                    "${DatabaseConstants.Game.StateColumnName}" = :state::${DatabaseConstants.GameStateType}
                WHERE "${DatabaseConstants.Game.IdColumnName}" = :id
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
