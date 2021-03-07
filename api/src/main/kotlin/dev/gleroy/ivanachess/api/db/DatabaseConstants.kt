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
         * White player column name.
         */
        const val WhitePlayerColumnName = "white_player"

        /**
         * Black player column name.
         */
        const val BlackPlayerColumnName = "black_player"

        /**
         * Turn color column name.
         */
        const val TurnColorColumnName = "turn_color"

        /**
         * State column name.
         */
        const val StateColumnName = "state"
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
         * Game column name.
         */
        const val GameColumnName = "game"

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
     * User table constants.
     */
    object User {
        /**
         * Table name.
         */
        const val TableName = "user"

        /**
         * ID column name.
         */
        const val IdColumnName = "id"

        /**
         * Pseudo column name.
         */
        const val PseudoColumnName = "pseudo"

        /**
         * Email column name.
         */
        const val EmailColumnName = "email"

        /**
         * Creation date column name.
         */
        const val CreationDateColumnName = "creation_date"

        /**
         * BCrypt password column name.
         */
        const val BCryptPasswordColumnName = "bcrypt_password"

        /**
         * Role column name.
         */
        const val RoleColumnName = "role"
    }

    /**
     * Color SQL type.
     */
    const val ColorType = "color"

    /**
     * Game state SQL type.
     */
    const val GameStateType = "game_state"

    /**
     * Piece SQL type.
     */
    const val PieceType = "piece_type"

    /**
     * Role SQL type.
     */
    const val RoleType = "role"
}
