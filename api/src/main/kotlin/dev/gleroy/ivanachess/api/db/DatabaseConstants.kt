package dev.gleroy.ivanachess.api.db

/**
 * Database constants.
 */
internal object DatabaseConstants {
    /**
     * Common table constants.
     */
    object Common {
        /**
         * ID column name.
         */
        const val IdColumnName = "id"

        /**
         * Creation date column name.
         */
        const val CreationDateColumnName = "creation_date"
    }

    /**
     * Game table constants.
     */
    object Game {
        /**
         * Table name.
         */
        const val TableName = "game"

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

        /**
         * Winner color column name.
         */
        const val WinnerColorColumnName = "winner_color"
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
     * Type constants.
     */
    object Type {
        /**
         * Color type.
         */
        const val Color = "color"

        /**
         * Game state type.
         */
        const val GameState = "game_state"

        /**
         * Piece type.
         */
        const val Piece = "piece_type"

        /**
         * Role type.
         */
        const val Role = "role"
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
         * Pseudo column name.
         */
        const val PseudoColumnName = "pseudo"

        /**
         * Email column name.
         */
        const val EmailColumnName = "email"

        /**
         * BCrypt password column name.
         */
        const val BCryptPasswordColumnName = "bcrypt_password"

        /**
         * Role column name.
         */
        const val RoleColumnName = "role"
    }
}
