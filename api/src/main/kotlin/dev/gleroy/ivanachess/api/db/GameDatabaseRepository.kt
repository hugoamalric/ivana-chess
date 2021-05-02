@file:Suppress("SqlResolve")

package dev.gleroy.ivanachess.api.db

import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.game.Move
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
) : AbstractEntityDatabaseRepository<GameEntity>(), GameRepository {
    internal companion object {
        /**
         * Create set of table columns used in SELECT statement.
         *
         * @param alias Table alias.
         * @return Set of columns.
         */
        internal fun createSelectColumns(alias: String) = setOf(
            TableColumn.Select(DatabaseConstants.Common.IdColumnName, alias),
            TableColumn.Select(DatabaseConstants.Common.CreationDateColumnName, alias),
            TableColumn.Select(DatabaseConstants.Game.TurnColorColumnName, alias, DatabaseConstants.Type.Color.label),
            TableColumn.Select(DatabaseConstants.Game.StateColumnName, alias, DatabaseConstants.Type.GameState.label),
            TableColumn.Select(DatabaseConstants.Game.WinnerColorColumnName, alias, DatabaseConstants.Type.Color.label),
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
        private val WhitePlayerColumn = TableColumn.Update(DatabaseConstants.Game.WhitePlayerColumnName)

        /**
         * Black player column.
         */
        private val BlackPlayerColumn = TableColumn.Update(DatabaseConstants.Game.BlackPlayerColumnName)

        /**
         * Turn color column.
         */
        private val TurnColorColumn = TableColumn.Update(
            name = DatabaseConstants.Game.TurnColorColumnName,
            type = DatabaseConstants.Type.Color.label,
        )

        /**
         * State column.
         */
        private val StateColumn = TableColumn.Update(
            name = DatabaseConstants.Game.StateColumnName,
            type = DatabaseConstants.Type.GameState.label
        )

        /**
         * Winner color column.
         */
        private val WinnerColorColumn = TableColumn.Update(
            name = DatabaseConstants.Game.WinnerColorColumnName,
            type = DatabaseConstants.Type.Color.label
        )

        /**
         * Game column from move table.
         */
        private val GameMoveColumn = TableColumn.Update(DatabaseConstants.Move.GameColumnName)

        /**
         * Order column from move table.
         */
        private val OrderMoveColumn = TableColumn.Update(DatabaseConstants.Move.OrderColumnName)

        /**
         * From column from move table.
         */
        private val FromMoveColumn = TableColumn.Update(DatabaseConstants.Move.FromColumnName)

        /**
         * To column from move table.
         */
        private val ToMoveColumn = TableColumn.Update(DatabaseConstants.Move.ToColumnName)

        /**
         * Promotion column from move table.
         */
        private val PromotionMoveColumn = TableColumn.Update(
            name = DatabaseConstants.Move.PromotionColumnName,
            type = DatabaseConstants.Type.Piece.label
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
                rightColumn = TableColumn.Select(
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
                rightColumn = TableColumn.Select(
                    name = DatabaseConstants.Game.BlackPlayerColumnName,
                    tableAlias = tableAlias,
                )
            ),
        )

    override val sortableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            CommonEntityField.Id to TableColumn.Select(DatabaseConstants.Common.IdColumnName, tableAlias),
            CommonEntityField.CreationDate to TableColumn.Select(
                name = DatabaseConstants.Common.CreationDateColumnName,
                tableAlias = tableAlias
            ),
        )

    override val filterableColumns: Map<ItemField, TableColumn.Select>
        get() = mapOf(
            GameField.State to TableColumn.Select(
                name = DatabaseConstants.Game.StateColumnName,
                tableAlias = tableAlias,
                type = DatabaseConstants.Type.GameState.label
            ),
        )

    override val insertColumns: Set<TableColumn.Update>
        get() = setOf(WhitePlayerColumn, BlackPlayerColumn)

    override val columnsToUpdate: Set<TableColumn.Update>
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
            TableColumn.Select(DatabaseConstants.Move.GameColumnName, MoveTableAlias),
            TableColumn.Select(DatabaseConstants.Move.OrderColumnName, MoveTableAlias),
            TableColumn.Select(DatabaseConstants.Move.FromColumnName, MoveTableAlias),
            TableColumn.Select(DatabaseConstants.Move.ToColumnName, MoveTableAlias),
            TableColumn.Select(DatabaseConstants.Move.PromotionColumnName, MoveTableAlias),
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
                        PieceTypeSqlEnumValue.from(move.promotion).label
                    } else {
                        null
                    }
                )
            }
        logStatements(sql, params)
        jdbcTemplate.batchUpdate(sql, params.toTypedArray())
    }

    override fun insertParameters(item: GameEntity) = mapOf(
        WhitePlayerColumn.name to item.whitePlayer.id,
        BlackPlayerColumn.name to item.blackPlayer.id,
    )

    override fun updateParameters(entity: GameEntity) = mapOf(
        TurnColorColumn.name to ColorSqlEnumValue.from(entity.turnColor).label,
        StateColumn.name to GameStateSqlEnumValue.from(entity.state).label,
        WinnerColorColumn.name to entity.winnerColor?.let { ColorSqlEnumValue.from(it).label },
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
