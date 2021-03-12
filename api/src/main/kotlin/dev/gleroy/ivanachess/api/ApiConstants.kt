package dev.gleroy.ivanachess.api

/***********************
 * API endpoint paths
 ***********************/

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
         * Page parameter.
         */
        const val Page = "page"

        /**
         * Page size parameter.
         */
        const val PageSize = "size"

        /**
         * Q parameter.
         */
        const val Q = "q"

        /**
         * Max size parameter.
         */
        const val MaxSize = "maxSize"

        /**
         * By parameter.
         */
        const val By = "by"

        /**
         * Value parameter.
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
         * Topic path.
         */
        const val TopicPath = "/topic"
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
