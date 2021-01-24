package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web configuration.
 *
 * This configuration override default object mapper.
 *
 * @param mapper Object mapper.
 */
@Configuration
class WebConfiguration(
    private val mapper: ObjectMapper
) : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(MappingJackson2HttpMessageConverter(mapper))
    }
}
