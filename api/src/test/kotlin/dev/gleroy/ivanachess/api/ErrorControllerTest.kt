package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.put
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
internal class ErrorControllerTest : AbstractControllerTest() {
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
    fun `should return invalid_request_body if request body is missing`() {
        val responseBody = mvc.put("$GameApiPath/${UUID.randomUUID()}$PlayPath")
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        val responseDto = mapper.readValue<ErrorDto.InvalidRequestBody>(responseBody)
        responseDto.shouldBeInstanceOf<ErrorDto.InvalidRequestBody>()
    }

    @Test
    fun `should return invalid_content_type if content type is invalid`() {
        val responseBody = mvc.put("$GameApiPath/${UUID.randomUUID()}$PlayPath") {
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
    fun `should return invalid_request_body if request body is not json`() {
        val responseBody = mvc.put("$GameApiPath/${UUID.randomUUID()}$PlayPath") {
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
        val responseBody = mvc.get(GameApiPath) {
            param(PageParam, "a")
        }
            .andDo { print() }
            .andExpect { status { isBadRequest() } }
            .andReturn()
            .response
            .contentAsByteArray
        mapper.readValue<ErrorDto.InvalidParameter>(responseBody) shouldBe ErrorDto.InvalidParameter(
            parameter = PageParam,
            reason = "must be int"
        )
    }
}
