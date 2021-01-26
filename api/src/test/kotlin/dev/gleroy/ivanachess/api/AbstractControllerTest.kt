package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request

internal abstract class AbstractControllerTest {
    @Autowired
    protected lateinit var mapper: ObjectMapper

    @Autowired
    protected lateinit var mvc: MockMvc

    abstract inner class WithBody {
        @Test
        fun `should return validation_error if request body is invalid`() {
            invalidRequests.forEach { req ->
                val responseBody = mvc.request(method, path) {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsBytes(req.requestDto)
                }
                    .andDo { print() }
                    .andExpect { status { isBadRequest() } }
                    .andReturn()
                    .response
                    .contentAsByteArray
                mapper.readValue<ErrorDto.Validation>(responseBody) shouldBe req.responseDto
            }
        }

        protected abstract val method: HttpMethod
        protected abstract val path: String
        protected abstract val requestDto: Any
        protected abstract val invalidRequests: List<InvalidRequest>
    }
}
