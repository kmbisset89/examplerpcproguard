package com.example.rpc

import com.example.rpc.api.ExampleRpc
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single([ExampleRpc::class])
class ExampleRpcImpl(
    @InjectedParam
    override val coroutineContext: CoroutineContext
) : ExampleRpc {

    private val _testFlow = MutableSharedFlow<String>()

    init {
        MainScope().launch {
            while (true) {
                _testFlow.emit("Hello + ${System.currentTimeMillis()}")
                kotlinx.coroutines.delay(1000)
            }
        }
    }

    override suspend fun check(): String = "OK"

    override fun testFlow(): Flow<String> = _testFlow
}
