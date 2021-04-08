@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api.game

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.api.*
import dev.gleroy.ivanachess.api.user.User
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Position
import dev.gleroy.ivanachess.dto.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
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

    @Nested
    inner class create : EndpointTest() {
        private val dto = GameCreationDto(
            whitePlayer = match.entity.whitePlayer.id,
            blackPlayer = match.entity.blackPlayer.id
        )

        override val method = HttpMethod.POST
        override val path = ApiConstants.Game.Path

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return player_not_found if white player user does not exist`() = withAuthentication { jwt ->
            val exception = EntityNotFoundException("")
            whenever(userService.getById(match.entity.whitePlayer.id)).thenThrow(exception)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ErrorDto.PlayerNotFound(PieceDto.Color.White),
            ) { mapper.readValue(it) }

            verify(userService).getById(match.entity.whitePlayer.id)
        }

        @Test
        fun `should return player_not_found if black player user does not exist`() = withAuthentication { jwt ->
            val exception = EntityNotFoundException("")
            whenever(userService.getById(match.entity.whitePlayer.id))
                .thenReturn(match.entity.whitePlayer)
            whenever(userService.getById(match.entity.blackPlayer.id)).thenThrow(exception)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ErrorDto.PlayerNotFound(PieceDto.Color.Black),
            ) { mapper.readValue(it) }

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

                doRequest(
                    cookies = listOf(createAuthenticationCookie(jwt)),
                    body = dto.copy(blackPlayer = dto.whitePlayer),
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedResponseBody = ErrorDto.PlayersAreSameUser,
                ) { mapper.readValue(it) }

                verify(userService, times(2)).getById(match.entity.whitePlayer.id)
                verify(gameService).create(match.entity.whitePlayer, match.entity.whitePlayer)
            }

        @Test
        fun `should create new game`() = withAuthentication { jwt ->
            whenever(userService.getById(match.entity.whitePlayer.id))
                .thenReturn(match.entity.whitePlayer)
            whenever(userService.getById(match.entity.blackPlayer.id))
                .thenReturn(match.entity.blackPlayer)
            whenever(gameService.create(match.entity.whitePlayer, match.entity.blackPlayer))
                .thenReturn(match)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedStatus = HttpStatus.CREATED,
                expectedResponseBody = gameConverter.convertToCompleteDto(match),
            ) { mapper.readValue(it) }

            verify(userService).getById(match.entity.whitePlayer.id)
            verify(userService).getById(match.entity.blackPlayer.id)
            verify(gameService).create(match.entity.whitePlayer, match.entity.blackPlayer)
        }
    }

    @Nested
    inner class get : EndpointTest() {
        override val method = HttpMethod.GET
        override val path = "${ApiConstants.Game.Path}/${match.entity.id}"

        @Test
        fun `should return game_not_found if game does not exist`() {
            whenever(gameService.getById(match.entity.id)).thenThrow(EntityNotFoundException(""))

            shouldReturnEntityNotFoundDto()

            verify(gameService).getById(match.entity.id)
        }

        @Test
        fun `should return game`() {
            whenever(gameService.getById(match.entity.id)).thenReturn(match.entity)
            whenever(gameService.getGameById(match.entity.id)).thenReturn(match.game)

            doRequest(
                expectedResponseBody = gameConverter.convertToCompleteDto(match),
            ) { mapper.readValue(it) }

            verify(gameService).getById(match.entity.id)
            verify(gameService).getGameById(match.entity.id)
        }
    }

    @Nested
    inner class getPage : PaginatedEndpointTest() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.Game.Path

        private val pageOpts = PageOptions<GameEntity>(
            number = 1,
            size = 10,
            sorts = listOf(
                EntitySort(CommonSortableEntityField.Id, EntitySort.Order.Descending),
                EntitySort(CommonSortableEntityField.CreationDate),
            ),
        )
        private val page = Page(
            content = listOf(match.entity),
            number = pageOpts.number,
            totalPages = 1,
            totalItems = 1,
        )

        @Test
        fun `should return page`() {
            whenever(gameService.getPage(pageOpts)).thenReturn(page)

            doRequest(
                pageOpts = pageOpts,
                expectedResponseBody = pageConverter.convertToDto(page) { gameConverter.convertToSummaryDto(it) },
            ) { mapper.readValue(it) }

            verify(gameService).getPage(pageOpts)
        }
    }

    @Nested
    inner class joinMatchmakingQueue : EndpointTest() {
        override val method = HttpMethod.PUT
        override val path = "${ApiConstants.Game.Path}${ApiConstants.Game.MatchPath}"

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return no content`() = withAuthentication { jwt ->
            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                expectedStatus = HttpStatus.NO_CONTENT,
            )

            verify(matchmakingQueue).put(simpleUser)
        }
    }

    @Nested
    inner class leaveMatchmakingQueue : EndpointTest() {
        override val method = HttpMethod.DELETE
        override val path = "${ApiConstants.Game.Path}${ApiConstants.Game.MatchPath}"

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return no content`() = withAuthentication { jwt ->
            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                expectedStatus = HttpStatus.NO_CONTENT,
            )

            verify(matchmakingQueue).remove(simpleUser)
        }
    }

    @Nested
    inner class play : EndpointTest() {
        private val move = Move.Simple.fromCoordinates("A2", "A4")

        override val method = HttpMethod.PUT
        override val path = "${ApiConstants.Game.Path}/${match.entity.id}${ApiConstants.Game.PlayPath}"

        private lateinit var dto: MoveDto

        @BeforeEach
        fun beforeEach() {
            dto = moveConverter.convertToDto(move)
        }

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return validation_error if number are too low`() = withAuthentication { jwt ->
            val col = Position.Min - 1
            val row = Position.Min - 1
            shouldReturnValidationErrorDto(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = MoveDto.Simple(
                    from = PositionDto(col, row),
                    to = PositionDto(col, row)
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createTooLowParameterDto("from.col", Position.Min),
                        createTooLowParameterDto("from.row", Position.Min),
                        createTooLowParameterDto("to.col", Position.Min),
                        createTooLowParameterDto("to.row", Position.Min)
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if number are too high`() = withAuthentication { jwt ->
            val col = Position.Max + 1
            val row = Position.Max + 1
            shouldReturnValidationErrorDto(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = MoveDto.Simple(
                    from = PositionDto(col, row),
                    to = PositionDto(col, row)
                ),
                expectedResponseBody = ErrorDto.Validation(
                    errors = setOf(
                        createTooHighParameterDto("from.col", Position.Max),
                        createTooHighParameterDto("from.row", Position.Max),
                        createTooHighParameterDto("to.col", Position.Max),
                        createTooHighParameterDto("to.row", Position.Max)
                    )
                ),
            )
        }

        @Test
        fun `should return game_not_found if game does not exist`() = withAuthentication { jwt ->
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(EntityNotFoundException(""))

            shouldReturnEntityNotFoundDto(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
            )

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return forbidden if user is not game player`() = withAuthentication { jwt ->
            whenever(gameService.play(match.entity.id, simpleUser, move))
                .thenThrow(NotAllowedPlayerException(match.entity.id, simpleUser))

            shouldReturnForbiddenDto(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
            )

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_player if player is user tries to steal turn`() = withAuthentication { jwt ->
            val exception = InvalidPlayerException(match.entity.id, simpleUser)
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(exception)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedStatus = HttpStatus.PRECONDITION_FAILED,
                expectedResponseBody = ErrorDto.InvalidPlayer,
            ) { mapper.readValue(it) }

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

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedStatus = HttpStatus.PRECONDITION_FAILED,
                expectedResponseBody = ErrorDto.InvalidMove,
            ) { mapper.readValue(it) }

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return updated game`() = withAuthentication(simpleUser) { jwt ->
            val game = match.game.play(move)
            val match = match.copy(game = game)
            val gameDto = gameConverter.convertToCompleteDto(match)

            whenever(gameService.play(match.entity.id, simpleUser, move)).thenReturn(match)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = dto,
                expectedResponseBody = gameDto,
            ) { mapper.readValue(it) }

            verify(gameService).play(match.entity.id, simpleUser, move)
            verify(messagingTemplate).convertAndSend(
                "${ApiConstants.WebSocket.GamePath}${match.entity.id}",
                gameDto
            )
        }
    }
}
