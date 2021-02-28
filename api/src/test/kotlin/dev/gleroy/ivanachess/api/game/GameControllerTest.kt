@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.api.game.*
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.dto.*
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import org.springframework.web.socket.sockjs.client.SockJsClient
import org.springframework.web.socket.sockjs.client.WebSocketTransport
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class GameControllerTest : AbstractControllerTest() {
    private val gameAndSummary = GameAndSummary()

    @MockBean
    private lateinit var service: GameService

    @Autowired
    private lateinit var pageConverter: PageConverter

    @Autowired
    private lateinit var gameConverter: GameConverter

    @Autowired
    private lateinit var asciiBoardSerializer: AsciiBoardSerializer

    @LocalServerPort
    private var serverPort: Int = 0

    private lateinit var wsClient: WebSocketStompClient
    private lateinit var wsSession: StompSession

    @BeforeEach
    fun beforeEach() {
        wsClient = WebSocketStompClient(SockJsClient(listOf(WebSocketTransport(StandardWebSocketClient())))).apply {
            messageConverter = MappingJackson2MessageConverter().apply { objectMapper = mapper }
        }
        wsSession = wsClient.connect(
            "ws://localhost:$serverPort${ApiConstants.WebSocket.Path}",
            object : StompSessionHandlerAdapter() {}
        ).get(1, TimeUnit.SECONDS)
    }

    @Nested
    inner class asciiBoard {
        @Test
        fun `should return game_not_found if game does not exist`() {
            whenever(service.getGameById(gameAndSummary.summary.id))
                .thenThrow(GameIdNotFoundException(gameAndSummary.summary.id))

            val responseBody = mvc.get(
                "${ApiConstants.Game.Path}/${gameAndSummary.summary.id}${ApiConstants.Game.BoardAsciiPath}"
            )
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(service).getGameById(gameAndSummary.summary.id)
        }

        @Test
        fun `should return ascii board representation`() {
            whenever(service.getGameById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.game)

            val responseBody = mvc.get(
                "${ApiConstants.Game.Path}/${gameAndSummary.summary.id}${ApiConstants.Game.BoardAsciiPath}"
            )
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    header {
                        string(HttpHeaders.CONTENT_TYPE, "text/plain;charset=UTF-8")
                    }
                }
                .andReturn()
                .response
                .contentAsByteArray
            responseBody shouldBe asciiBoardSerializer.serialize(gameAndSummary.game.board)

            verify(service).getGameById(gameAndSummary.summary.id)
        }
    }

    @Nested
    inner class create {
        private lateinit var gameDto: GameDto

        @BeforeEach
        fun beforeEach() {
            gameDto = gameConverter.convert(gameAndSummary)
        }

        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.post(ApiConstants.Game.Path)
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should create new game (with header auth)`() {
            shouldCreateNewGame { authenticationHeader(it) }
        }

        @Test
        fun `should create new game (with cookie auth)`() {
            shouldCreateNewGame { authenticationCookie(it) }
        }

        private fun shouldCreateNewGame(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) =
            withAuthentication(simpleUser) { jwt ->
                whenever(service.create()).thenReturn(gameAndSummary)

                val responseBody = mvc.post(ApiConstants.Game.Path) {
                    auth(jwt)
                }
                    .andDo { print() }
                    .andExpect { status { isCreated() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

                verify(service).create()
            }
    }

    @Nested
    inner class get {
        private lateinit var gameDto: GameDto

        @BeforeEach
        fun beforeEach() {
            gameDto = gameConverter.convert(gameAndSummary)
        }

        @Test
        fun `should return game_not_found if game does not exist`() {
            whenever(service.getSummaryById(gameAndSummary.summary.id))
                .thenThrow(GameIdNotFoundException(gameAndSummary.summary.id))

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(service).getSummaryById(gameAndSummary.summary.id)
        }

        @Test
        fun `should return game`() {
            whenever(service.getSummaryById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.summary)
            whenever(service.getGameById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.game)

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

            verify(service).getSummaryById(gameAndSummary.summary.id)
            verify(service).getGameById(gameAndSummary.summary.id)
        }
    }

    @Nested
    inner class getAll : Paginated() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.Game.Path

        private val pageNb = 2
        private val size = 4
        private val page = Page(
            content = listOf(gameAndSummary.summary),
            number = pageNb,
            totalItems = 5,
            totalPages = 6
        )
        private val responseDto = pageConverter.convert(page) { gameConverter.convert(it) }

        @Test
        fun `should return page`() {
            whenever(service.getAllSummaries(pageNb, size)).thenReturn(page)

            val responseBody = mvc.request(method, path) {
                param(ApiConstants.QueryParams.Page, "$pageNb")
                param(ApiConstants.QueryParams.PageSize, "$size")
            }
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<PageDto<GameDto.Summary>>(responseBody) shouldBe responseDto

            verify(service).getAllSummaries(pageNb, size)
        }
    }

    @Nested
    inner class play : WithBody(simpleUser) {
        private val move = Move.Simple.fromCoordinates("A2", "A4")

        override val method = HttpMethod.PUT
        override val path =
            "${ApiConstants.Game.Path}/${gameAndSummary.summary.whiteToken}${ApiConstants.Game.PlayPath}"
        override val requestDto = MoveDto.from(move)
        override val invalidRequests = listOf(
            InvalidRequest(
                requestDto = MoveDto.Simple(
                    from = PositionDto(Position.Min - 1, Position.Min - 1),
                    to = PositionDto(Position.Min - 1, Position.Min - 1)
                ),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        tooLowNumberInvalidParameter("from.col", Position.Min),
                        tooLowNumberInvalidParameter("from.row", Position.Min),
                        tooLowNumberInvalidParameter("to.col", Position.Min),
                        tooLowNumberInvalidParameter("to.row", Position.Min)
                    )
                )
            ),
            InvalidRequest(
                requestDto = MoveDto.Simple(
                    from = PositionDto(Position.Max + 1, Position.Max + 1),
                    to = PositionDto(Position.Max + 1, Position.Max + 1)
                ),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        tooHighNumberInvalidParameter("from.col", Position.Max),
                        tooHighNumberInvalidParameter("from.row", Position.Max),
                        tooHighNumberInvalidParameter("to.col", Position.Max),
                        tooHighNumberInvalidParameter("to.row", Position.Max)
                    )
                )
            )
        )

        private lateinit var blockingQueue: ArrayBlockingQueue<GameDto>

        @BeforeEach
        fun beforeEach() {
            blockingQueue = ArrayBlockingQueue(1)
        }

        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should return game_not_found if game does not exist`() = withAuthentication(simpleUser) { jwt ->
            val token = gameAndSummary.summary.whiteToken
            whenever(service.getSummaryByToken(token)).thenThrow(GameTokenNotFoundException(token))

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(service).getSummaryByToken(token)
        }

        @Test
        fun `should return invalid_move if move is invalid`() = withAuthentication(simpleUser) { jwt ->
            val token = gameAndSummary.summary.whiteToken
            val exception = PlayException.InvalidMove(
                id = gameAndSummary.summary.id,
                token = token,
                color = Piece.Color.White,
                move = move,
                cause = InvalidMoveException("Invalid move")
            )
            whenever(service.getSummaryByToken(token)).thenReturn(gameAndSummary.summary)
            whenever(service.play(gameAndSummary.summary, exception.token, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidMove>(responseBody) shouldBe ErrorDto.InvalidMove(exception.cause.message)

            verify(service).getSummaryByToken(token)
            verify(service).play(gameAndSummary.summary, exception.token, move)
        }

        @Test
        fun `should return invalid_player if player is invalid`() = withAuthentication(simpleUser) { jwt ->
            val token = gameAndSummary.summary.whiteToken
            val exception = PlayException.InvalidPlayer(
                id = gameAndSummary.summary.id,
                token = token,
                color = Piece.Color.White
            )
            whenever(service.getSummaryByToken(token)).thenReturn(gameAndSummary.summary)
            whenever(service.play(gameAndSummary.summary, exception.token, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidPlayer>(responseBody).shouldBeInstanceOf<ErrorDto.InvalidPlayer>()

            verify(service).getSummaryByToken(token)
            verify(service).play(gameAndSummary.summary, exception.token, move)
        }

        @Test
        fun `should return updated game (with header auth)`() {
            shouldReturnUpdatedGame { authenticationHeader(it) }
        }

        @Test
        fun `should return updated game (with cookie auth)`() {
            shouldReturnUpdatedGame { authenticationCookie(it) }
        }

        private fun shouldReturnUpdatedGame(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) =
            withAuthentication(simpleUser) { jwt ->
                val token = gameAndSummary.summary.whiteToken
                val game = gameAndSummary.game.play(move)
                val gameAndSummary = gameAndSummary.copy(game = game)
                val gameDto = gameConverter.convert(gameAndSummary)
                whenever(service.getSummaryByToken(token)).thenReturn(gameAndSummary.summary)
                whenever(service.play(gameAndSummary.summary, token, move)).thenReturn(gameAndSummary)

                wsSession.subscribe(
                    "${ApiConstants.WebSocket.TopicPath}${ApiConstants.Game.Path}/${gameAndSummary.summary.id}",
                    object : StompFrameHandler {
                        override fun getPayloadType(headers: StompHeaders) = GameDto.Complete::class.java

                        override fun handleFrame(headers: StompHeaders, payload: Any?) {
                            blockingQueue.add(payload as GameDto.Complete)
                        }
                    }
                )

                val responseBody = mvc.request(method, path) {
                    auth(jwt)
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsBytes(requestDto)
                }
                    .andDo { print() }
                    .andExpect { status { isOk() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

                blockingQueue.poll(1, TimeUnit.SECONDS) shouldBe gameDto

                verify(service).getSummaryByToken(token)
                verify(service).play(gameAndSummary.summary, token, move)
            }
    }
}
