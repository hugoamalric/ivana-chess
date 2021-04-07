@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.api.CommonSortableEntityField
import dev.gleroy.ivanachess.api.SortableEntityField
import dev.gleroy.ivanachess.api.game.GameEntity
import dev.gleroy.ivanachess.api.game.GameRepository
import dev.gleroy.ivanachess.core.Move
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
class GameDatabaseRepository(
    override val jdbcTemplate: NamedParameterJdbcTemplate
) : AbstractDatabaseEntityRepository<GameEntity>(), GameRepository {
    internal companion object {
        /**
         * Create set of table columns used in SELECT statement.
         *
         * @param alias Table alias.
         * @return Set of columns.
         */
        internal fun createSelectColumns(alias: String) = setOf(
            SelectColumn(DatabaseConstants.Common.IdColumnName, alias),
            SelectColumn(DatabaseConstants.Common.CreationDateColumnName, alias),
            SelectColumn(DatabaseConstants.Game.TurnColorColumnName, alias),
            SelectColumn(DatabaseConstants.Game.StateColumnName, alias),
            SelectColumn(DatabaseConstants.Game.WinnerColorColumnName, alias),
        )

        /**
         * Alias for game table.
         */
        private const val GameTableAlias = "g"

        /**
         * Alias for user table used to white player join.
         */
        private const val WhitePlayerTableAlias = "wp"

        /**
         * Alias for user table used to black player join.
         */
        private const val BlackPlayerTableAlias = "bp"

        /**
         * Alias for move table.
         */
        private const val MoveTableAlias = "m"

        /**
         * White player column.
         */
        private val WhitePlayerColumn = UpdateColumn(DatabaseConstants.Game.WhitePlayerColumnName)

        /**
         * Black player column.
         */
        private val BlackPlayerColumn = UpdateColumn(DatabaseConstants.Game.BlackPlayerColumnName)

        /**
         * Turn color column.
         */
        private val TurnColorColumn = UpdateColumn(
            name = DatabaseConstants.Game.TurnColorColumnName,
            type = DatabaseConstants.Type.Color,
        )

        /**
         * State column.
         */
        private val StateColumn = UpdateColumn(
            name = DatabaseConstants.Game.StateColumnName,
            type = DatabaseConstants.Type.GameState
        )

        /**
         * Winner color column.
         */
        private val WinnerColorColumn = UpdateColumn(
            name = DatabaseConstants.Game.WinnerColorColumnName,
            type = DatabaseConstants.Type.Color
        )

        /**
         * Game column from move table.
         */
        private val GameMoveColumn = UpdateColumn(DatabaseConstants.Move.GameColumnName)

        /**
         * Order column from move table.
         */
        private val OrderMoveColumn = UpdateColumn(DatabaseConstants.Move.OrderColumnName)

        /**
         * From column from move table.
         */
        private val FromMoveColumn = UpdateColumn(DatabaseConstants.Move.FromColumnName)

        /**
         * To column from move table.
         */
        private val ToMoveColumn = UpdateColumn(DatabaseConstants.Move.ToColumnName)

        /**
         * Promotion column from move table.
         */
        private val PromotionMoveColumn = UpdateColumn(
            name = DatabaseConstants.Move.PromotionColumnName,
            type = DatabaseConstants.Type.Piece
        )
    }

    override val tableName get() = DatabaseConstants.Game.TableName

    final override val tableAlias get() = GameTableAlias

    override val selectColumns
        get() = createSelectColumns(tableAlias) +
                UserDatabaseRepository.createSelectColumns(WhitePlayerTableAlias) +
                UserDatabaseRepository.createSelectColumns(BlackPlayerTableAlias)

    override val selectJoins
        get() = listOf(
            Join(
                leftColumn = Join.Column(
                    name = DatabaseConstants.Common.IdColumnName,
                    tableName = DatabaseConstants.User.TableName,
                    tableAlias = WhitePlayerTableAlias,
                ),
                rightColumn = SelectColumn(
                    name = DatabaseConstants.Game.WhitePlayerColumnName,
                    tableAlias = tableAlias,
                )
            ),
            Join(
                leftColumn = Join.Column(
                    name = DatabaseConstants.Common.IdColumnName,
                    tableName = DatabaseConstants.User.TableName,
                    tableAlias = BlackPlayerTableAlias,
                ),
                rightColumn = SelectColumn(
                    name = DatabaseConstants.Game.BlackPlayerColumnName,
                    tableAlias = tableAlias,
                )
            ),
        )

    override val sortableColumns: Map<SortableEntityField<GameEntity>, SelectColumn>
        get() = mapOf(
            CommonSortableEntityField.Id to SelectColumn(DatabaseConstants.Common.IdColumnName, tableAlias),
            CommonSortableEntityField.CreationDate to SelectColumn(
                name = DatabaseConstants.Common.CreationDateColumnName,
                tableAlias = tableAlias
            ),
        )

    override val insertColumns: Set<UpdateColumn>
        get() = setOf(WhitePlayerColumn, BlackPlayerColumn)

    override val updateColumns: Set<UpdateColumn>
        get() = setOf(
            TurnColorColumn,
            StateColumn,
            WinnerColorColumn,
        )

    override val rowMapper = GameEntityRowMapper(
        alias = tableAlias,
        whitePlayerRowMapper = UserRowMapper(WhitePlayerTableAlias),
        blackPlayerRowMapper = UserRowMapper(BlackPlayerTableAlias),
    )

    /**
     * SELECT statement for move table.
     */
    private val moveSelectStatement = buildSelectStatement(
        tableName = DatabaseConstants.Move.TableName,
        tableAlias = MoveTableAlias,
        columns = setOf(
            SelectColumn(DatabaseConstants.Move.GameColumnName, MoveTableAlias),
            SelectColumn(DatabaseConstants.Move.OrderColumnName, MoveTableAlias),
            SelectColumn(DatabaseConstants.Move.FromColumnName, MoveTableAlias),
            SelectColumn(DatabaseConstants.Move.ToColumnName, MoveTableAlias),
            SelectColumn(DatabaseConstants.Move.PromotionColumnName, MoveTableAlias),
        ),
    )

    /**
     * INSERT statement for move table.
     */
    private val moveInsertStatement = buildInsertStatement(
        tableName = DatabaseConstants.Move.TableName,
        columns = setOf(
            GameMoveColumn,
            OrderMoveColumn,
            FromMoveColumn,
            ToMoveColumn,
            PromotionMoveColumn,
        ),
    )

    override fun fetchMoves(id: UUID): List<Move> {
        val sql = """
            $moveSelectStatement
            WHERE m."${DatabaseConstants.Move.GameColumnName}" = :game_id
            ORDER BY m."${DatabaseConstants.Move.OrderColumnName}"
        """
        val params = mapOf("game_id" to id)
        logStatement(sql, params)
        return jdbcTemplate.query(sql, params, MoveRowMapper(MoveTableAlias))
    }

    @Transactional
    override fun saveMoves(id: UUID, moves: List<Move>) {
        deleteAllMoves(id)
        val sql = moveInsertStatement
        val params = moves
            .mapIndexed { i, move ->
                mapOf(
                    GameMoveColumn.name to id,
                    OrderMoveColumn.name to i + 1,
                    FromMoveColumn.name to move.from.toString(),
                    ToMoveColumn.name to move.to.toString(),
                    PromotionMoveColumn.name to if (move is Move.Promotion) {
                        PieceType.from(move.promotion).sqlValue
                    } else {
                        null
                    }
                )
            }
        logStatements(sql, params)
        jdbcTemplate.batchUpdate(sql, params.toTypedArray())
    }

    override fun insertParams(entity: GameEntity) = mapOf(
        WhitePlayerColumn.name to entity.whitePlayer.id,
        BlackPlayerColumn.name to entity.blackPlayer.id,
    )

    override fun updateParams(entity: GameEntity) = mapOf(
        TurnColorColumn.name to ColorType.from(entity.turnColor).sqlValue,
        StateColumn.name to GameStateType.from(entity.state).sqlValue,
        WinnerColorColumn.name to entity.winnerColor?.let { ColorType.from(it).sqlValue },
    )

    /**
     * Delete all moves.
     *
     * @param id Game entity ID.
     */
    private fun deleteAllMoves(id: UUID) {
        val sql = """
            DELETE FROM "${DatabaseConstants.Move.TableName}"
            WHERE "${DatabaseConstants.Move.GameColumnName}" = :id
        """
        val params = mapOf("id" to id)
        logStatement(sql, params)
        jdbcTemplate.update(sql, params)
    }
}
