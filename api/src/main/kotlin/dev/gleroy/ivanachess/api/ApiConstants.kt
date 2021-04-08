package dev.gleroy.ivanachess.api

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
         * Min of page parameter.
         */
        const val MinPage = 1

        /**
         * Min of page size parameter.
         */
        const val MinPageSize = 1
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
         * ASCII board endpoint path.
         */
        const val BoardAsciiPath = "/board/ascii"

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
         * By query parameter name.
         */
        const val By = "by"

        /**
         * Exclude query parameter name.
         */
        const val Exclude = "exclude"

        /**
         * Field parameter name.
         */
        const val Field = "field"

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

        /**
         * Game path.
         */
        const val GamePath = "/topic/game-"

        /**
         * Match path.
         */
        const val MatchPath = "/topic/game-match"
    }

    /**
     * Search endpoint path.
     */
    const val SearchPath = "/search"

    /**
     * Exists endpoint path.
     */
    const val ExistsPath = "/exists"

    /**
     * UUID regex.
     */
    const val UuidRegex = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$"
}
