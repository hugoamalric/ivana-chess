package dev.gleroy.ivanachess.api

/***********************
 * API endpoint paths
 ***********************/

/**
 * API constants.
 */
object ApiConstants {
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
         * Play endpoint path.
         */
        const val PlayPath = "/play"
    }

    /**
     * Query parameters constants.
     */
    object QueryParams {
        /**
         * Page parameter.
         */
        const val Page = "page"

        /**
         * Page size parameter.
         */
        const val PageSize = "size"
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
         * Log-in endpoint path.
         */
        const val LogInPath = "/login"

        /**
         * Log-out endpoint path.
         */
        const val LogOutPath = "/login"

        /**
         * Sign-up endpoint path.
         */
        const val SignUpPath = "/signup"
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
         * Topic path.
         */
        const val TopicPath = "/topic"
    }

    /**
     * UUID regex.
     */
    const val UuidRegex = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$"
}
