package com.example.rpc.bl

import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.ConnectException

fun HttpClientConfig<*>.installStandardRetry() {
    install(HttpRequestRetry) {
        maxRetries = 5
        retryOnExceptionIf { _, error ->
            error is HttpRequestTimeoutException || error is ConnectException
        }
        delayMillis { retry ->
            retry * 1000L
        }
    }
}
