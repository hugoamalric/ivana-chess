@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.core.*
import dev.gleroy.ivanachess.game.Move
import dev.gleroy.ivanachess.io.*
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
        private val gameCreation = GameCreation(
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
                body = gameCreation,
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ErrorRepresentation.PlayerNotFound(ColorRepresentation.White),
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
                body = gameCreation,
                expectedStatus = HttpStatus.BAD_REQUEST,
                expectedResponseBody = ErrorRepresentation.PlayerNotFound(ColorRepresentation.Black),
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
                    body = gameCreation.copy(blackPlayer = gameCreation.whitePlayer),
                    expectedStatus = HttpStatus.BAD_REQUEST,
                    expectedResponseBody = ErrorRepresentation.PlayersAreSameUser,
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
                body = gameCreation,
                expectedStatus = HttpStatus.CREATED,
                expectedResponseBody = matchConverter.convertToRepresentation(match),
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

            shouldReturnEntityNotFoundErrorRepresentation()

            verify(gameService).getById(match.entity.id)
        }

        @Test
        fun `should return game`() {
            whenever(gameService.getById(match.entity.id)).thenReturn(match.entity)
            whenever(gameService.getGameById(match.entity.id)).thenReturn(match.game)

            doRequest(
                expectedResponseBody = matchConverter.convertToRepresentation(match),
            ) { mapper.readValue(it) }

            verify(gameService).getById(match.entity.id)
            verify(gameService).getGameById(match.entity.id)
        }
    }

    @Nested
    inner class getPage : PaginatedEndpointTest() {
        override val method = HttpMethod.GET
        override val path = ApiConstants.Game.Path

        private val pageOpts = PageOptions(
            number = 1,
            size = 10,
            sorts = listOf(
                ItemSort(CommonEntityField.Id, ItemSort.Order.Descending),
                ItemSort(CommonEntityField.CreationDate),
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
                expectedResponseBody = pageConverter.convertToRepresentation(page) { gameEntity ->
                    gameConverter.convertToRepresentation(gameEntity)
                },
            ) { mapper.readValue(it) }

            verify(gameService).getPage(pageOpts)
        }
    }

    @Nested
    inner class joinMatchmaking : EndpointTest() {
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

            verify(matchmaking).put(simpleUser)
        }
    }

    @Nested
    inner class leaveMatchmaking : EndpointTest() {
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

            verify(matchmaking).remove(simpleUser)
        }
    }

    @Nested
    inner class play : EndpointTest() {
        private val move = Move.Simple.fromCoordinates("A2", "A4")

        override val method = HttpMethod.PUT
        override val path = "${ApiConstants.Game.Path}/${match.entity.id}${ApiConstants.Game.PlayPath}"

        private lateinit var representation: MoveRepresentation

        @BeforeEach
        fun beforeEach() {
            representation = moveConverter.convertToRepresentation(move)
        }

        @Test
        fun `should return unauthorized`() {
            shouldReturnUnauthorized()
        }

        @Test
        fun `should return validation_error if number are too low`() = withAuthentication { jwt ->
            val col = ApiConstants.Constraints.MinPositionIndex - 1
            val row = ApiConstants.Constraints.MinPositionIndex - 1
            shouldReturnValidationErrorRepresentation(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = MoveRepresentation.Simple(
                    from = PositionRepresentation(col, row),
                    to = PositionRepresentation(col, row)
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createTooLowParameterErrorRepresentation("from.col", ApiConstants.Constraints.MinPositionIndex),
                        createTooLowParameterErrorRepresentation("from.row", ApiConstants.Constraints.MinPositionIndex),
                        createTooLowParameterErrorRepresentation("to.col", ApiConstants.Constraints.MinPositionIndex),
                        createTooLowParameterErrorRepresentation("to.row", ApiConstants.Constraints.MinPositionIndex)
                    )
                ),
            )
        }

        @Test
        fun `should return validation_error if number are too high`() = withAuthentication { jwt ->
            val col = ApiConstants.Constraints.MaxPositionIndex + 1
            val row = ApiConstants.Constraints.MaxPositionIndex + 1
            shouldReturnValidationErrorRepresentation(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = MoveRepresentation.Simple(
                    from = PositionRepresentation(col, row),
                    to = PositionRepresentation(col, row)
                ),
                expectedResponseBody = ErrorRepresentation.Validation(
                    errors = setOf(
                        createTooHighParameterErrorRepresentation(
                            parameter = "from.col",
                            max = ApiConstants.Constraints.MaxPositionIndex
                        ),
                        createTooHighParameterErrorRepresentation(
                            parameter = "from.row",
                            max = ApiConstants.Constraints.MaxPositionIndex
                        ),
                        createTooHighParameterErrorRepresentation("to.col", ApiConstants.Constraints.MaxPositionIndex),
                        createTooHighParameterErrorRepresentation("to.row", ApiConstants.Constraints.MaxPositionIndex)
                    )
                ),
            )
        }

        @Test
        fun `should return game_not_found if game does not exist`() = withAuthentication { jwt ->
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(EntityNotFoundException(""))

            shouldReturnEntityNotFoundErrorRepresentation(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = representation,
            )

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return forbidden if user is not game player`() = withAuthentication { jwt ->
            whenever(gameService.play(match.entity.id, simpleUser, move))
                .thenThrow(NotAllowedPlayerException(match.entity.id, simpleUser))

            shouldReturnForbiddenErrorRepresentation(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = representation,
            )

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return invalid_player if player is user tries to steal turn`() = withAuthentication { jwt ->
            val exception = InvalidPlayerException(match.entity.id, simpleUser)
            whenever(gameService.play(match.entity.id, simpleUser, move)).thenThrow(exception)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = representation,
                expectedStatus = HttpStatus.PRECONDITION_FAILED,
                expectedResponseBody = ErrorRepresentation.InvalidPlayer,
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
                body = representation,
                expectedStatus = HttpStatus.PRECONDITION_FAILED,
                expectedResponseBody = ErrorRepresentation.InvalidMove,
            ) { mapper.readValue(it) }

            verify(gameService).play(match.entity.id, simpleUser, move)
        }

        @Test
        fun `should return updated game`() = withAuthentication(simpleUser) { jwt ->
            val game = match.game.play(move)
            val match = match.copy(game = game)
            val gameRepresentation = matchConverter.convertToRepresentation(match)

            whenever(gameService.play(match.entity.id, simpleUser, move)).thenReturn(match)

            doRequest(
                cookies = listOf(createAuthenticationCookie(jwt)),
                body = representation,
                expectedResponseBody = gameRepresentation,
            ) { mapper.readValue(it) }

            verify(gameService).play(match.entity.id, simpleUser, move)
            verify(webSocketSender).sendGame(gameRepresentation)
        }
    }
}
