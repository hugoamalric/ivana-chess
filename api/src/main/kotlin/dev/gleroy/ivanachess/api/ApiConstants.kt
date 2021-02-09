package dev.gleroy.ivanachess.api

/***********************
 * API endpoint paths
 ***********************/

/**
 * ASCII board endpoint path.
 */
const val BoardAsciiPath = "/board/ascii"

/**
 * Game API root path.
 */
const val GameApiPath = "/game"

/**
 * Play endpoint path.
 */
const val PlayPath = "/play"

/***********************
 * Query params
 ***********************/

/**
 * Page parameter.
 */
const val PageParam = "page"

/**
 * Size parameter.
 */
const val SizeParam = "size"

/***********************
 * Path regex
 ***********************/

/**
 * UUID regex.
 */
const val UuidRegex = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}\$"

/***********************
 * WS broker paths
 ***********************/

/**
 * Topic path for websocket broker.
 */
const val TopicPath = "/topic"

/**
 * WebSocket root path.
 */
const val WebSocketPath = "/ws"
