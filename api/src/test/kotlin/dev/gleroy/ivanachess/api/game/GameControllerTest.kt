@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.api.broker.MatchmakingQueue
import dev.gleroy.ivanachess.api.io.GameConverter
import dev.gleroy.ivanachess.api.io.MoveConverter
import dev.gleroy.ivanachess.api.io.PageConverter
import dev.gleroy.ivanachess.api.security.Jwt
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.api.user.UserService
import dev.gleroy.ivanachess.core.AsciiBoardSerializer
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.*
import io.kotlintest.matchers.numerics.shouldBeZero
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request
import java.time.Clock
import java.time.OffsetDateTime

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class GameControllerTest : AbstractControllerTest() {
    private val match = Match(
        entity = GameEntity(
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

    @MockBean
    private lateinit var matchmakingQueue: MatchmakingQueue

    @MockBean
    private lateinit var messagingTemplate: SimpMessagingTemplate

    @Autowired
    private lateinit var moveConverter: MoveConverter

    @Autowired
    private lateinit var pageConverter: PageConverter

    @Autowired
    private lateinit var gameConverter: GameConverter

    @Autowired
    private lateinit var asciiBoardSerializer: AsciiBoardSerializer

    @Nested
    inner class asciiBoard {
        @Test
        fun `should return game_not_found if game does not exist`() {
            whenever(gameService.getGameById(match.entity.id)).thenThrow(EntityNotFoundException(""))

            val responseBody = mvc.get(
                "${ApiConstants.Game.Path}/${match.entity.id}${ApiConstants.Game.BoardAsciiPath}"
            )
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.EntityNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.EntityNotFound>()

            verify(gameService).getGameById(match.entity.id)
        }

        @Test
        fun `should return ascii board representation`() {
            whenever(gameService.getGameById(match.entity.id)).thenReturn(match.game)

            val responseBody = mvc.get(
                "${ApiConstants.Game.Path}/${match.entity.id}${ApiConstants.Game.BoardAsciiPath}"
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
            responseBody shouldBe asciiBoardSerializer.serialize(match.game.board)

            verify(gameService).getGameById(match.entity.id)
        }
    }

    @Nested
    inner class create {
        private val gameCreationDto = GameCreationDto(
            whitePlayer = match.entity.whitePlayer.id,
            blackPlayer = match.entity.blackPlayer.id
        )

        private lateinit var gameDto: GameDto

        @BeforeEach
        fun beforeEach() {
            gameDto = gameConverter.convertToCompleteDto(match)
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
            val exception = EntityNotFoundException("")
            whenever(userService.getById(match.entity.whitePlayer.id)).thenThrow(exception)

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
                playerColor = PieceDto.Color.White,
            )

            verify(userService).getById(match.entity.whitePlayer.id)
        }

        @Test
        fun `should return player_not_found if black player user does not exist`() = withAuthentication { jwt ->
            val exception = EntityNotFoundException("")
            whenever(userService.getById(match.entity.whitePlayer.id))
                .thenReturn(match.entity.whitePlayer)
            whenever(userService.getById(match.entity.blackPlayer.id)).thenThrow(exception)

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
                playerColor = PieceDto.Color.Black,
            )

            verify(userService).getById(match.entity.whitePlayer.id)
            verify(userService).getById(match.entity.blackPlayer.id)
        }

        @Test
        fun `should return players_are_same_user if white and black players are the same user`() =
            withAuthentication { jwt ->
                whenever(userService.getById(match.entity.whitePlayer.id))
                    .thenReturn(match.entity.whitePlayer)
                whenever(gameService.create(match.entity.whitePlayer, match.entity.whitePlayer))
                    .thenThrow(PlayersAreSameUserException())

                val responseBody = mvc.post(ApiConstants.Game.Path) {
                    authenticationHeader(jwt)
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsBytes(gameCreationDto.copy(blackPlayer = gameCreationDto.whitePlayer))
                }
                    .andDo { print() }
                    .andExpect { status { isBadRequest() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                mapper.readValue<ErrorDto.PlayersAreSameUser>(responseBody)
                    .shouldBeInstanceOf<ErrorDto.PlayersAreSameUser>()

                verify(userService, times(2)).getById(match.entity.whitePlayer.id)
                verify(gameService).create(match.entity.whitePlayer, match.entity.whitePlayer)
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
            whenever(userService.getById(match.entity.whitePlayer.id))
                .thenReturn(match.entity.whitePlayer)
            whenever(userService.getById(match.entity.blackPlayer.id))
                .thenReturn(match.entity.blackPlayer)
            whenever(gameService.create(match.entity.whitePlayer, match.entity.blackPlayer))
                .thenReturn(match)

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

            verify(userService).getById(match.entity.whitePlayer.id)
            verify(userService).getById(match.entity.blackPlayer.id)
            verify(gameService).create(match.entity.whitePlayer, match.entity.blackPlayer)
        }
    }

    @Nested
    inner class get {
        private lateinit var gameDto: GameDto

        @BeforeEach
        fun beforeEach() {
            gameDto = gameConverter.convertToCompleteDto(match)
        }

        @Test
        fun `should return game_not_found if game does not exist`() {
            whenever(gameService.getById(match.entity.id)).thenThrow(EntityNotFoundException(""))

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.EntityNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.EntityNotFound>()

            verify(gameService).getById(match.entity.id)
        }

        @Test
        fun `should return game`() {
            whenever(gameService.getById(match.entity.id)).thenReturn(match.entity)
            whenever(gameService.getGameById(match.entity.id)).thenReturn(match.game)

            val responseBody = mvc.get("${ApiConstants.Game.Path}/${gameDto.id}")
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<GameDto.Complete>(responseBody) shouldBe gameDto

            verify(gameService).getById(match.entity.id)
            verify(gameService).getGameById(match.entity.id)
        }
    }

    @Nested
    inner class getAll : Paginated() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.Game.Path

        private val pageNb = 2
        private val size = 4
        private val page = Page(
            content = listOf(match.entity),
            number = pageNb,
            totalItems = 5,
            totalPages = 6
        )
        private val responseDto = pageConverter.convertToDto(page) { gameConverter.convertToSummaryDto(it) }

        @Test
        fun `should return page`() {
            whenever(gameService.getPage(PageOptions(pageNb, size))).thenReturn(page)

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

            verify(gameService).getPage(PageOptions(pageNb, size))
        }
    }

    @Nested
    inner class joinMatchmakingQueue {
        private val method = HttpMethod.PUT
        private val path = "${ApiConstants.Game.Path}${ApiConstants.Game.MatchPath}"

        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.request(method, path)
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should return no content with header auth`() {
            shouldReturnNoContent { authenticationHeader(it) }
        }

        @Test
        fun `should return no content with cookie auth`() {
            shouldReturnNoContent { authenticationCookie(it) }
        }

        private fun shouldReturnNoContent(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) =
            withAuthentication(simpleUser) { jwt ->
                val responseBody = mvc.request(method, path) { auth(jwt) }
                    .andDo { print() }
                    .andExpect { status { isNoContent() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                responseBody.size.shouldBeZero()

                verify(matchmakingQueue).put(simpleUser)
            }
    }

    @Nested
    inner class leaveMatchmakingQueue {
        private val method = HttpMethod.DELETE
        private val path = "${ApiConstants.Game.Path}${ApiConstants.Game.MatchPath}"

        @Test
        fun `should return unauthorized`() {
            val responseBody = mvc.request(method, path)
                .andDo { print() }
                .andExpect { status { isUnauthorized() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.Unauthorized>(responseBody).shouldBeInstanceOf<ErrorDto.Unauthorized>()
        }

        @Test
        fun `should return no content with header auth`() {
            shouldReturnNoContent { authenticationHeader(it) }
        }

        @Test
        fun `should return no content with cookie auth`() {
            shouldReturnNoContent { authenticationCookie(it) }
        }

        private fun shouldReturnNoContent(auth: MockHttpServletRequestDsl.(Jwt) -> Unit) =
            withAuthentication(simpleUser) { jwt ->
                val responseBody = mvc.request(method, path) { auth(jwt) }
                    .andDo { print() }
                    .andExpect { status { isNoContent() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                responseBody.size.shouldBeZero()

                verify(matchmakingQueue).remove(simpleUser)
            }
    }

    @Nested
    inner class play {
        private val move = Move.Simple.fromCoordinates("A2", "A4")

        private val method = HttpMethod.PUT
        private val path = "${ApiConstants.Game.Path}/${match.entity.id}${ApiConstants.Game.PlayPath}"

        private lateinit var moveDto: MoveDto

        @BeforeEach
        fun beforeEach() {
            moveDto = moveConverter.convertToDto(move)
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
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(EntityNotFoundException(""))

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
            mapper.readValue<ErrorDto.EntityNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.EntityNotFound>()

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return forbidden if user is not game player`() = withAuthentication { jwt ->
            whenever(gameService.play(match.entity.id, simpleUser, move))
                .thenThrow(NotAllowedPlayerException(match.entity.id, simpleUser))

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

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_player if player is user tries to steal turn`() = withAuthentication { jwt ->
            val exception = InvalidPlayerException(match.entity.id, simpleUser)
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(exception)

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

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_move if move is invalid`() = withAuthentication { jwt ->
            val exception = InvalidMoveException(
                id = match.entity.id,
                player = match.entity.whitePlayer,
                move = move
            )
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(exception)

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

            verify(gameService).play(match.entity.id, simpleUser, move)
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
                val game = match.game.play(move)
                val match = match.copy(game = game)
                val gameDto = gameConverter.convertToCompleteDto(match)
                whenever(gameService.play(match.entity.id, simpleUser, move)).thenReturn(match)

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

                verify(gameService).play(match.entity.id, simpleUser, move)
                verify(messagingTemplate).convertAndSend(
                    "${ApiConstants.WebSocket.GamePath}${match.entity.id}",
                    gameDto
                )
            }
    }
}
