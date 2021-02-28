package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import dev.gleroy.ivanachess.dto.ErrorDto
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class ErrorControllerTest : AbstractControllerTest() {
    @Test
    fun `should return method_not_allowed if method is not allowed`() {
        val responseBody = mvc.put("${ApiConstants.Game.Path}/${UUID.randomUUID()}")
            .andDo { print() }
            .andExpect { status { isMethodNotAllowed() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.MethodNotAllowed>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.MethodNotAllowed>()
    }

    @Test
    fun `should return not_found if endpoint does not exist`() {
        val responseBody = mvc.put("/test")
            .andDo { print() }
            .andExpect { status { isNotFound() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.NotFound>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.NotFound>()
    }

    @Test
    fun `should return invalid_request_body if request body is missing`() = withAuthentication(simpleUser) { jwt ->
        val responseBody = mvc.put("${ApiConstants.Game.Path}/${UUID.randomUUID()}${ApiConstants.Game.PlayPath}") {
            authenticationHeader(jwt)
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.InvalidRequestBody>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.InvalidRequestBody>()
    }

    @Test
    fun `should return invalid_content_type if content type is invalid`() = withAuthentication(simpleUser) { jwt ->
        val responseBody = mvc.put("${ApiConstants.Game.Path}/${UUID.randomUUID()}${ApiConstants.Game.PlayPath}") {
            authenticationHeader(jwt)
            content = "{}"
        }
            .andDo { print() }
            .andExpect { status { isUnsupportedMediaType() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.InvalidContentType>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.InvalidContentType>()
    }

    @Test
    fun `should return invalid_request_body if request body is not json`() = withAuthentication(simpleUser) { jwt ->
        val responseBody = mvc.put("${ApiConstants.Game.Path}/${UUID.randomUUID()}${ApiConstants.Game.PlayPath}") {
            authenticationHeader(jwt)
            contentType = MediaType.APPLICATION_JSON
            content = "test".toByteArray()
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.InvalidRequestBody>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.InvalidRequestBody>()
    }

    @Test
    fun `should return invalid_parameter if type is invalid`() {
        val responseBody = mvc.get(ApiConstants.Game.Path) {
            param(ApiConstants.QueryParams.Page, "a")
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        mapper.readValue<ErrorDto.InvalidParameter>(responseBody) shouldBe ErrorDto.InvalidParameter(
            parameter = ApiConstants.QueryParams.Page,
            reason = "must be int"
        )
    }

    @Test
    fun `should return invalid_parameter if parameter is missing`() {
        val responseBody = mvc.post("${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}") {
            contentType = MediaType.APPLICATION_JSON
            content = "{}"
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe ErrorDto.Validation(
            errors = setOf(
                ErrorDto.InvalidParameter(
                    parameter = "pseudo",
                    reason = "must not be null"
                )
            )
        )
    }
}
