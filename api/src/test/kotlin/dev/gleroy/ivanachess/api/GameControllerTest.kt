@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import com.nhaarman.mockitokotlin2.reset
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.gleroy.ivanachess.core.InvalidMoveException
import dev.gleroy.ivanachess.core.Move
import dev.gleroy.ivanachess.core.Piece
import dev.gleroy.ivanachess.core.Position
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class GameControllerTest : AbstractControllerTest() {
    @MockBean
    private lateinit var service: GameService

    @Autowired
    private lateinit var converter: GameInfoConverter

    @Nested
    inner class create {
        private val gameInfo = GameInfo()
        private val gameDto = converter.convert(gameInfo)

        @Test
        fun `should create new game`() {
            whenever(service.create()).thenReturn(gameInfo)

            mvc.post(GameApiPath)
                .andDo { print() }
                .andExpect {
                    status { isCreated() }
                    content { bytes(mapper.writeValueAsBytes(gameDto)) }
                }

            verify(service).create()
        }
    }

    @Nested
    inner class play : AbstractControllerTest.WithBody() {
        private val gameInfo = GameInfo()
        private val move = Move.fromCoordinates("A2", "A4")
        private val responseDto = converter.convert(gameInfo)

        override val method = HttpMethod.PUT
        override val path = "$GameApiPath/${gameInfo.whiteToken}$PlayPath"
        override val requestDto = move.toDto()
        override val invalidRequests = listOf(
            InvalidRequest(
                requestDto = MoveDto(
                    from = PositionDto(Position.Min - 1, Position.Min - 1),
                    to = PositionDto(Position.Min - 1, Position.Min - 1)
                ),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        ErrorDto.InvalidParameter(
                            parameter = "from.col",
                            reason = "must be greater than or equal to ${Position.Min}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "from.row",
                            reason = "must be greater than or equal to ${Position.Min}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "to.col",
                            reason = "must be greater than or equal to ${Position.Min}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "to.row",
                            reason = "must be greater than or equal to ${Position.Min}"
                        )
                    )
                )
            ),
            InvalidRequest(
                requestDto = MoveDto(
                    from = PositionDto(Position.Max + 1, Position.Max + 1),
                    to = PositionDto(Position.Max + 1, Position.Max + 1)
                ),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        ErrorDto.InvalidParameter(
                            parameter = "from.col",
                            reason = "must be less than or equal to ${Position.Max}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "from.row",
                            reason = "must be less than or equal to ${Position.Max}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "to.col",
                            reason = "must be less than or equal to ${Position.Max}"
                        ),
                        ErrorDto.InvalidParameter(
                            parameter = "to.row",
                            reason = "must be less than or equal to ${Position.Max}"
                        )
                    )
                )
            ),
            InvalidRequest(
                requestDto = NullableMoveDto(),
                responseDto = ErrorDto.Validation(
                    errors = setOf(
                        ErrorDto.InvalidParameter(
                            parameter = "from",
                            reason = "must not be null"
                        )
                    )
                )
            )
        )

        @Test
        fun `should return game_not_found if game does not exist`() {
            val token = gameInfo.whiteToken
            whenever(service.play(token, move)).thenThrow(PlayException.GameNotFound(token))

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isNotFound() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.GameNotFound>(responseBody).shouldBeInstanceOf<ErrorDto.GameNotFound>()

            verify(service).play(token, move)
        }

        @Test
        fun `should return invalid_move if move is invalid`() {
            val exception = PlayException.InvalidMove(
                id = gameInfo.id,
                token = gameInfo.whiteToken,
                color = Piece.Color.White,
                move = move,
                cause = InvalidMoveException("Invalid move")
            )
            whenever(service.play(exception.token, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidMove>(responseBody) shouldBe ErrorDto.InvalidMove(exception.cause.message)

            verify(service).play(exception.token, move)
        }

        @Test
        fun `should return invalid_player if player is invalid`() {
            val exception = PlayException.InvalidPlayer(
                id = gameInfo.id,
                token = gameInfo.whiteToken,
                color = Piece.Color.White
            )
            whenever(service.play(exception.token, move)).thenThrow(exception)

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isPreconditionFailed() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<ErrorDto.InvalidPlayer>(responseBody).shouldBeInstanceOf<ErrorDto.InvalidPlayer>()

            verify(service).play(exception.token, move)
        }

        @Test
        fun `should return move piece`() {
            whenever(service.play(gameInfo.whiteToken, move)).thenReturn(gameInfo)

            val responseBody = mvc.request(method, path) {
                contentType = MediaType.APPLICATION_JSON
                content = mapper.writeValueAsBytes(requestDto)
            }
                .andDo { print() }
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsByteArray
            mapper.readValue<GameDto>(responseBody) shouldBe responseDto

            verify(service).play(gameInfo.whiteToken, move)
        }
    }

    private data class NullableMoveDto(
        val from: PositionDto? = null,
        val to: PositionDto? = null
    )

    private fun Move.toDto() = MoveDto(
        from = from.toDto(),
        to = to.toDto()
    )

    private fun Position.toDto() = PositionDto(
        col = col,
        row = row
    )
}
