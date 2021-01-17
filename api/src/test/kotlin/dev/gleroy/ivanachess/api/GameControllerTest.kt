@file:Suppress("ClassName")

package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.mockk.confirmVerified
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class GameControllerTest {
    @MockBean
    private lateinit var service: GameService

    @Autowired
    private lateinit var converter: GameInfoConverter

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var mvc: MockMvc

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
}
