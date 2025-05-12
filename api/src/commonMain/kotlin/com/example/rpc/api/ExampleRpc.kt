package com.example.rpc.api

import kotlinx.coroutines.flow.Flow
import kotlinx.rpc.RemoteService
import kotlinx.rpc.annotations.Rpc

@Rpc
interface ExampleRpc : RemoteService {

    suspend fun check(): String

    fun testFlow(): Flow<String>
}
