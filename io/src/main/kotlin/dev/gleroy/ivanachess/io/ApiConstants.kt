package dev.gleroy.ivanachess.io

import dev.gleroy.ivanachess.game.Position

/**
 * API constants.
 */
object ApiConstants {
    /**
     * Authentication API constants.
     */
    object Authentication {
        /**
         * API path.
         */
        const val Path = "/auth"
    }

    /**
     * Constraints API constants.
     */
    object Constraints {
        /**
         * Max of position index.
         */
        const val MaxPositionIndex = Position.Max

        /**
         * Max of user pseudo length.
         */
        const val MaxPseudoLength = 50

        /**
         * Min of page parameter.
         */
        const val MinPage = 1

        /**
         * Min of page size parameter.
         */
        const val MinPageSize = 1

        /**
         * Min of user password length.
         */
        const val MinPasswordLength = 3

        /**
         * Min of position index.
         */
        const val MinPositionIndex = Position.Min

        /**
         * Min of user pseudo length.
         */
        const val MinPseudoLength = 3

        /**
         * User pseudo regex.
         */
        const val PseudoRegex = "^[A-z0-9_-]+$"
    }

    /**
     * Game API constants.
     */
    object Game {
        /**
         * API path.
         */
        const val Path = "/game"

        /**
         * Match endpoint path.
         */
        const val MatchPath = "/match"

        /**
         * Play endpoint path.
         */
        const val PlayPath = "/play"
    }

    /**
     * User API constants.
     */
    object User {
        /**
         * API path.
         */
        const val Path = "/user"

        /**
         * Sign-up endpoint path.
         */
        const val SignUpPath = "/signup"
    }

    /**
     * Query parameters constants.
     */
    object QueryParams {
        /**
         * Exclude query parameter name.
         */
        const val Exclude = "exclude"

        /**
         * Field parameter name.
         */
        const val Field = "field"

        /**
         * Filter query parameter name.
         */
        const val Filter = "filter"

        /**
         * Page query parameter name.
         */
        const val Page = "page"

        /**
         * Page size query parameter name.
         */
        const val PageSize = "size"

        /**
         * Q query parameter name.
         */
        const val Q = "q"

        /**
         * Sort query parameter name.
         */
        const val Sort = "sort"

        /**
         * Value query parameter name.
         */
        const val Value = "value"
    }

    /**
     * Web socket constants.
     */
    object WebSocket {
        /**
         * Endpoint.
         */
        const val Path = "/ws"
    }

    /**
     * Search endpoint path.
     */
    const val SearchPath = "/search"

    /**
     * UUID regex.
     */
    const val UuidRegex = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$"
}
