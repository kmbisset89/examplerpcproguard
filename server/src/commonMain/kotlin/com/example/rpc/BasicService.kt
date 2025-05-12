package com.example.rpc

import com.example.rpc.api.ExampleRpc
import com.example.rpc.api.LoggerWrapper
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.cancel
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

class BasicService(
    configuration: ServiceConfiguration,
    private val logger: LoggerWrapper? = null
) : KoinComponent {

    private var currentConfiguration = configuration
    fun start() {
        logger?.infoLogger("Starting BasicService")
        currentConfiguration.scope.embeddedServer(Netty, port = currentConfiguration.port, host = currentConfiguration.host.ip, module = {
            install(Krpc)

            install(CORS) {
                allowMethod(HttpMethod.Options)
                allowMethod(HttpMethod.Put)
                allowMethod(HttpMethod.Delete)
                allowMethod(HttpMethod.Patch)
                allowHeader(HttpHeaders.Authorization)
                allowHeader(HttpHeaders.AccessControlAllowOrigin)
                allowHeader(HttpHeaders.Upgrade)
                allowNonSimpleContentTypes = true
                allowCredentials = true
                allowSameOrigin = true
            }

            val koin = getKoin()
            routing {
                get("/health") {
                    call.respondText(status = HttpStatusCode.OK) { "Server is Running" }
                }

                rpc("/example") {
                    rpcConfig {
                        serialization {
                            json()
                        }
                    }

                    registerService<ExampleRpc> { ctx ->
                        koin.get { parametersOf(ctx) }
                    }
                }
            }
        }).start(wait = currentConfiguration.wait)
    }

    fun stop() {
        logger?.infoLogger("Stopping BasicService")
        currentConfiguration.scope.cancel()
    }

    fun updateConfiguration(newConfiguration: ServiceConfiguration) {
        logger?.infoLogger("Updating BasicService configuration")
        currentConfiguration = newConfiguration
    }
}
