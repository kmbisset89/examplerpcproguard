package com.example.rpc.bl

import com.example.rpc.api.ServiceConnection
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.encodedPath
import kotlinx.atomicfu.atomic
import kotlinx.rpc.RpcClient
import kotlinx.rpc.krpc.ktor.client.installKrpc
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.json.json

class RpcFactory {
    private val serviceConnection = ServiceConnection.Local
    private var currentPort by atomic(8081)
    suspend fun makeClient(path: String): RpcClient = HttpClient(CIO) {
        installStandardRetry()
        installKrpc()
    }.rpc {
        url {
            host = serviceConnection.ip
            port = currentPort
            encodedPath = path
        }

        rpcConfig {
            serialization {
                json()
            }
        }
    }

    internal fun updatePort(port: Int) {
        this.currentPort = port
    }
}
