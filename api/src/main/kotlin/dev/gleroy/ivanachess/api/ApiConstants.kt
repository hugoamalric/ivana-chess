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
         * Play endpoint path.
         */
        const val PlayPath = "/play"

        /**
         * ASCII board endpoint path.
         */
        const val BoardAsciiPath = "/board/ascii"
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
         * Sign up endpoint path.
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
