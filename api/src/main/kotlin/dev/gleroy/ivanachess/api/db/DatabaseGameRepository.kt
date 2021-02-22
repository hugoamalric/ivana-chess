@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.GameInfo
import dev.gleroy.ivanachess.api.GameRepository
import dev.gleroy.ivanachess.api.Page
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

    /**
     * Row mapper for game entity.
     */
    private val gameEntityRowMapper = GameEntityRowMapper()

    /**
     * Row mapper for move.
     */
    private val moveRowMapper = MoveRowMapper()

    override fun create() = GameInfo().apply {
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
                    "id" to id,
                    "creation_date" to creationDate,
                    "white_token" to whiteToken,
                    "black_token" to blackToken
                )
            )
        )
        Logger.debug("Game $id saved in database")
    }

    override fun exists(id: UUID): Boolean = jdbcTemplate.queryForObject(
        """
            SELECT EXISTS(
                SELECT *
                FROM "${DatabaseConstants.Game.TableName}"
                WHERE "${DatabaseConstants.Game.IdColumnName}" = :id
            )
        """,
        ComparableMapSqlParameterSource(mapOf("id" to id)),
        Boolean::class.java
    )!!

    override fun getAll(page: Int, size: Int): Page<GameInfo> {
        checkNumberIsStrictlyPositive(page, "page")
        checkNumberIsStrictlyPositive(size, "size")
        val gameEntities = jdbcTemplate.query(
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
            gameEntityRowMapper
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
            content = gameEntities.map { fetchMoves(it) },
            number = page,
            totalItems = totalItems,
            totalPages = ceil(totalItems.toDouble() / size.toDouble()).toInt()
        )
    }

    override fun getById(id: UUID) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "${DatabaseConstants.Game.TableName}"
            WHERE "${DatabaseConstants.Game.IdColumnName}" = :id
        """,
        mapOf("id" to id),
        gameEntityRowMapper
    )?.let { fetchMoves(it) }

    override fun getByToken(token: UUID) = jdbcTemplate.queryForNullableObject(
        """
            SELECT *
            FROM "${DatabaseConstants.Game.TableName}"
            WHERE "${DatabaseConstants.Game.WhiteTokenColumnName}" = :token
                OR "${DatabaseConstants.Game.BlackTokenColumnName}" = :token
        """,
        mapOf("token" to token),
        gameEntityRowMapper
    )?.let { fetchMoves(it) }

    @Transactional
    override fun update(gameInfo: GameInfo): GameInfo {
        val existingGameInfo = getById(gameInfo.id)
            ?: throw IllegalArgumentException("Game ${gameInfo.id} does not exist").apply { Logger.debug(message) }
        updateMoves(gameInfo, existingGameInfo)
        Logger.debug("Game ${gameInfo.id} updated")
        return gameInfo
    }

    /**
     * Check if given number is strictly positive.
     *
     * @param number Number to check.
     * @param parameterName Parameter name.
     * @throws IllegalArgumentException If number is not strictly positive.
     */
    @Throws(IllegalArgumentException::class)
    private fun checkNumberIsStrictlyPositive(number: Int, parameterName: String) {
        if (number < 1) {
            throw IllegalArgumentException("$parameterName must be strictly positive")
        }
    }

    /**
     * Fetch moves of given game entity.
     *
     * @param gameEntity Game entity.
     * @return Game.
     */
    private fun fetchMoves(gameEntity: GameEntity): GameInfo {
        val moves = jdbcTemplate.query(
            """
                SELECT *
                FROM "${DatabaseConstants.Move.TableName}"
                WHERE "${DatabaseConstants.Move.GameIdColumnName}" = :game_id
                ORDER BY "${DatabaseConstants.Move.OrderColumnName}"
            """,
            ComparableMapSqlParameterSource(mapOf("game_id" to gameEntity.id)),
            moveRowMapper
        )
        return gameEntity.toGameInfo(moves)
    }

    /**
     * Update game moves.
     *
     * @param gameInfo New game.
     * @param existingGameInfo Current game in database.
     */
    private fun updateMoves(gameInfo: GameInfo, existingGameInfo: GameInfo) {
        val moves = gameInfo.game.moves - existingGameInfo.game.moves
        if (moves.isNotEmpty()) {
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
            val moveUpdate = moves
                .mapIndexed { i, move -> move.toMoveUpdate(existingGameInfo.game.moves.size + i + 1) }
                .reduce { acc, moveUpdate ->
                    acc.copy(
                        sql = "${acc.sql}, ${moveUpdate.sql}",
                        params = acc.params + moveUpdate.params
                    )
                }
            jdbcTemplate.update(
                "$insertIntoSql ${moveUpdate.sql}",
                MapSqlParameterSource(moveUpdate.params + mapOf("game_id" to gameInfo.id))
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
            is Move.Promotion -> PieceType.fromPiece(promotion).sqlValue
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
