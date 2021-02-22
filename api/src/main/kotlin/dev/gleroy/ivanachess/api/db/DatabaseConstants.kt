package dev.gleroy.ivanachess.api.db

/**
 * Database constants.
 */
internal object DatabaseConstants {
    /**
     * Game table constants.
     */
    object Game {
        /**
         * Table name.
         */
        const val TableName = "game"

        /**
         * ID column name.
         */
        const val IdColumnName = "id"

        /**
         * Creation date column name.
         */
        const val CreationDateColumnName = "creation_date"

        /**
         * White token column name.
         */
        const val WhiteTokenColumnName = "white_token"

        /**
         * Black token column name.
         */
        const val BlackTokenColumnName = "black_token"
    }

    /**
     * Move table constants.
     */
    object Move {
        /**
         * Table name.
         */
        const val TableName = "move"

        /**
         * Game ID column name.
         */
        const val GameIdColumnName = "game_id"

        /**
         * Order column name.
         */
        const val OrderColumnName = "order"

        /**
         * From column name.
         */
        const val FromColumnName = "from"

        /**
         * To column name.
         */
        const val ToColumnName = "to"

        /**
         * Promotion column name.
         */
        const val PromotionColumnName = "promotion"
    }

    /**
     * SQL piece type.
     */
    const val PieceType = "piece_type"
}
