package dev.gleroy.ivanachess.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * Web configuration.
 *
 * This configuration override default object mapper.
 *
 * @param mapper Object mapper.
 * @param props Properties.
 */
@Configuration
class WebConfiguration(
    private val mapper: ObjectMapper,
    private val props: Properties
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(*props.server.allowedOrigins.split(',').toTypedArray())
            .allowedMethods(*HttpMethod.values().map { it.name }.toTypedArray())
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        converters.add(MappingJackson2HttpMessageConverter(mapper))
    }
}
