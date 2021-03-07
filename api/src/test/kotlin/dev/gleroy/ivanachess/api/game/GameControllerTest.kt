@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.api.game.*
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserIdNotFoundException
import dev.gleroy.ivanachess.api.user.UserService
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
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class GameControllerTest : AbstractControllerTest() {
    private val gameAndSummary = GameAndSummary(
        summary = GameSummary(
            whitePlayer = User(
                pseudo = "white",
                email = "white@ivanachess.loc",
                creationDate = OffsetDateTime.now(Clock.systemUTC()),
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            ),
            blackPlayer = User(
                pseudo = "black",
                email = "black@ivanachess.loc",
                creationDate = OffsetDateTime.now(Clock.systemUTC()),
                bcryptPassword = "\$2y\$12\$0jk/kpEJfuuVJShpgeZhYuTYAVj5sau2W2qtFTMMIwPctmLWVXHSS"
            )
        )
    )

    @MockBean
    private lateinit var gameService: GameService

    @MockBean
    private lateinit var userService: UserService

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
            whenever(gameService.getGameById(gameAndSummary.summary.id))
                .thenThrow(GameNotFoundException(gameAndSummary.summary.id))

            val responseBody = mvc.get(
                "${ApiConstants.Game.Path}/${gameAndSummary.summary.id}${ApiConstants.Game.BoardAsciiPath}"
            )
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(gameService).getGameById(gameAndSummary.summary.id)
        }

        @Test
        fun `should return ascii board representation`() {
            whenever(gameService.getGameById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.game)

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

            verify(gameService).getGameById(gameAndSummary.summary.id)
        }
    }

    @Nested
    inner class create {
        private val gameCreationDto = GameCreationDto(
            whitePlayer = gameAndSummary.summary.whitePlayer.id,
            blackPlayer = gameAndSummary.summary.blackPlayer.id
        )

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
        fun `should return player_not_found if white player user does not exist`() = withAuthentication { jwt ->
            val exception = UserIdNotFoundException(gameAndSummary.summary.whitePlayer.id)
            whenever(userService.getById(gameAndSummary.summary.whitePlayer.id)).thenThrow(exception)

            val responseBody = mvc.post(ApiConstants.Game.Path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(gameCreationDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.PlayerNotFound>(responseBody) shouldBe ErrorDto.PlayerNotFound(
                id = gameAndSummary.summary.whitePlayer.id
            )

            verify(userService).getById(gameAndSummary.summary.whitePlayer.id)
        }

        @Test
        fun `should return player_not_found if black player user does not exist`() = withAuthentication { jwt ->
            val exception = UserIdNotFoundException(gameAndSummary.summary.blackPlayer.id)
            whenever(userService.getById(gameAndSummary.summary.whitePlayer.id))
                .thenReturn(gameAndSummary.summary.whitePlayer)
            whenever(userService.getById(gameAndSummary.summary.blackPlayer.id)).thenThrow(exception)

            val responseBody = mvc.post(ApiConstants.Game.Path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(gameCreationDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.PlayerNotFound>(responseBody) shouldBe ErrorDto.PlayerNotFound(
                id = gameAndSummary.summary.blackPlayer.id
            )

            verify(userService).getById(gameAndSummary.summary.whitePlayer.id)
            verify(userService).getById(gameAndSummary.summary.blackPlayer.id)
        }

        @Test
        fun `should create new game (with header auth)`() {
            shouldCreateNewGame { authenticationHeader(it) }
        }

        @Test
        fun `should create new game (with cookie auth)`() {
            shouldCreateNewGame { authenticationCookie(it) }
        }

        private fun shouldCreateNewGame(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) = withAuthentication { jwt ->
            whenever(userService.getById(gameAndSummary.summary.whitePlayer.id))
                .thenReturn(gameAndSummary.summary.whitePlayer)
            whenever(userService.getById(gameAndSummary.summary.blackPlayer.id))
                .thenReturn(gameAndSummary.summary.blackPlayer)
            whenever(gameService.create(gameAndSummary.summary.whitePlayer, gameAndSummary.summary.blackPlayer))
                .thenReturn(gameAndSummary)

            val responseBody = mvc.post(ApiConstants.Game.Path) {
                auth(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(gameCreationDto)
            }
                .andDo { print() }
                .andExpect { status { isCreated() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

            verify(userService).getById(gameAndSummary.summary.whitePlayer.id)
            verify(userService).getById(gameAndSummary.summary.blackPlayer.id)
            verify(gameService).create(gameAndSummary.summary.whitePlayer, gameAndSummary.summary.blackPlayer)
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
            whenever(gameService.getSummaryById(gameAndSummary.summary.id))
                .thenThrow(GameNotFoundException(gameAndSummary.summary.id))

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(gameService).getSummaryById(gameAndSummary.summary.id)
        }

        @Test
        fun `should return game`() {
            whenever(gameService.getSummaryById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.summary)
            whenever(gameService.getGameById(gameAndSummary.summary.id)).thenReturn(gameAndSummary.game)

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

            verify(gameService).getSummaryById(gameAndSummary.summary.id)
            verify(gameService).getGameById(gameAndSummary.summary.id)
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
            whenever(gameService.getAllSummaries(pageNb, size)).thenReturn(page)

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

            verify(gameService).getAllSummaries(pageNb, size)
        }
    }

    @Nested
    inner class play {
        private val move = Move.Simple.fromCoordinates("A2", "A4")
        private val moveDto = MoveDto.from(move)

        private val method = HttpMethod.PUT
        private val path = "${ApiConstants.Game.Path}/${gameAndSummary.summary.id}${ApiConstants.Game.PlayPath}"

        private lateinit var blockingQueue: ArrayBlockingQueue<GameDto>

        @BeforeEach
        fun beforeEach() {
            blockingQueue = ArrayBlockingQueue(1)
        }

        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should return validation_error if number are too low`() = withAuthentication { jwt ->
            val moveDto = MoveDto.Simple(
                from = PositionDto(Position.Min - 1, Position.Min - 1),
                to = PositionDto(Position.Min - 1, Position.Min - 1)
            )

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    tooLowNumberInvalidParameter("from.col", Position.Min),
                    tooLowNumberInvalidParameter("from.row", Position.Min),
                    tooLowNumberInvalidParameter("to.col", Position.Min),
                    tooLowNumberInvalidParameter("to.row", Position.Min)
                )
            )
        }

        @Test
        fun `should return validation_error if number are too high`() = withAuthentication { jwt ->
            val moveDto = MoveDto.Simple(
                from = PositionDto(Position.Max + 1, Position.Max + 1),
                to = PositionDto(Position.Max + 1, Position.Max + 1)
            )

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isBadRequest() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
                errors = setOf(
                    tooHighNumberInvalidParameter("from.col", Position.Max),
                    tooHighNumberInvalidParameter("from.row", Position.Max),
                    tooHighNumberInvalidParameter("to.col", Position.Max),
                    tooHighNumberInvalidParameter("to.row", Position.Max)
                )
            )
        }

        @Test
        fun `should return game_not_found if game does not exist`() = withAuthentication { jwt ->
            val exception = GameNotFoundException(gameAndSummary.summary.id)
            whenever(gameService.play(gameAndSummary.summary.id, simpleUser, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(gameService).play(gameAndSummary.summary.id, simpleUser, move)
        }

        @Test
        fun `should return forbidden if user is not game player`() = withAuthentication { jwt ->
            whenever(gameService.play(gameAndSummary.summary.id, simpleUser, move))
                .thenThrow(NotAllowedPlayerException(gameAndSummary.summary.id, simpleUser))

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isForbidden() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Forbidden>(responseBody).shouldBeInstanceOf<ErrorDto.Forbidden>()

            verify(gameService).play(gameAndSummary.summary.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_player if player is user tries to steal turn`() = withAuthentication { jwt ->
            val exception = InvalidPlayerException(gameAndSummary.summary.id, simpleUser)
            whenever(gameService.play(gameAndSummary.summary.id, simpleUser, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidPlayer>(responseBody).shouldBeInstanceOf<ErrorDto.InvalidPlayer>()

            verify(gameService).play(gameAndSummary.summary.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_move if move is invalid`() = withAuthentication { jwt ->
            val exception = InvalidMoveException(
                id = gameAndSummary.summary.id,
                player = gameAndSummary.summary.whitePlayer,
                move = move
            )
            whenever(gameService.play(gameAndSummary.summary.id, simpleUser, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                authenticationHeader(jwt)
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(moveDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidMove>(responseBody).shouldBeInstanceOf<ErrorDto.InvalidMove>()

            verify(gameService).play(gameAndSummary.summary.id, simpleUser, move)
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
                val game = gameAndSummary.game.play(move)
                val gameAndSummary = gameAndSummary.copy(game = game)
                val gameDto = gameConverter.convert(gameAndSummary)
                whenever(gameService.play(gameAndSummary.summary.id, simpleUser, move)).thenReturn(gameAndSummary)

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
                    content = mapper.writeValueAsBytes(moveDto)
                }
                    .andDo { print() }
                    .andExpect { status { isOk() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

                blockingQueue.poll(1, TimeUnit.SECONDS) shouldBe gameDto

                verify(gameService).play(gameAndSummary.summary.id, simpleUser, move)
            }
    }
}
