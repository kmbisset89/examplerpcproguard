package com.example.rpc.bl

import com.example.rpc.api.ExampleRpc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import kotlinx.rpc.withService
import org.koin.core.annotation.Single

@Single
class ClientFacade {

    val rpcFactory = RpcFactory()

    suspend fun check(): String = withContext(Dispatchers.IO) {
        val client = rpcFactory.makeClient("/example")

        client.withService<ExampleRpc>().check()
    }

    suspend fun testFlow(): Flow<String> = withContext(Dispatchers.IO) {
        val client = rpcFactory.makeClient("/example")

        client.withService<ExampleRpc>().testFlow()
    }
}
