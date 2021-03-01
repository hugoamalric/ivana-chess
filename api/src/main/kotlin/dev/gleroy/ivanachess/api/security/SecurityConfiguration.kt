package dev.gleroy.ivanachess.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import dev.gleroy.ivanachess.api.ApiConstants
import dev.gleroy.ivanachess.api.Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * Security configuration.
 *
 * @param service Authentication service.
 * @param mapper Object mapper.
 * @param props Properties.
 */
@Configuration
class SecurityConfiguration(
    private val service: AuthenticationService,
    private val mapper: ObjectMapper,
    private val props: Properties
) : WebSecurityConfigurerAdapter() {

    /**
     * Instantiate BCrypt password encoder.
     */
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .exceptionHandling()
            .authenticationEntryPoint(DefaultAuthenticationEntryPoint(mapper))
            .accessDeniedHandler(DefaultAccessDeniedHandler(mapper))
            .and()
            .addFilter(JwtAuthenticationFilter(service, authenticationManager(), props))
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "${ApiConstants.User.Path}/${ApiConstants.User.SignUpPath}").anonymous()
            .antMatchers(HttpMethod.DELETE, ApiConstants.Authentication.Path).authenticated()
            .antMatchers(HttpMethod.POST, ApiConstants.Game.Path).authenticated()
            .antMatchers(HttpMethod.PUT, "${ApiConstants.Game.Path}/*/${ApiConstants.Game.PlayPath}").authenticated()
            .anyRequest().permitAll()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(service).passwordEncoder(passwordEncoder())
    }
}
