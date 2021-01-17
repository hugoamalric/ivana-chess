package dev.gleroy.ivanachess.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.net.InetAddress
import java.net.URI

/**
 * Properties.
 *
 * @param server Server properties.
 * @param api API properties.
 * @param webappUrl URL to webapp.
 */
@ConfigurationProperties(prefix = "ivana-chess")
@ConstructorBinding
data class Properties(
    val server: Server = Server(),
    val api: Api = Api(),
    val webappUrl: URI = URI("http://localhost:3000")
) {
    /**
     * API properties.
     *
     * @param docEndpointEnabled True if doc endpoint is enabled, false otherwise.
     */
    data class Api(
        val docEndpointEnabled: Boolean = false
    )

    /**
     * Server properties.
     *
     * @param bindAddress Bind address.
     * @param port Port.
     */
    data class Server(
        val bindAddress: InetAddress = InetAddress.getByName("0.0.0.0"),
        val port: Int = 8080
    )
}
