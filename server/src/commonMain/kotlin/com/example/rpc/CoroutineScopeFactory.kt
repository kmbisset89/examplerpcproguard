package com.example.rpc

import com.example.rpc.api.LoggerWrapper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
internal class CoroutineScopeFactory(
    @Provided
    private val loggerWrapper: LoggerWrapper?
) {

    fun createScope(): CoroutineScope = CoroutineScope(
        Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            loggerWrapper?.error("Error in WindowsNetworkStatusMonitor: ${throwable.message ?: "Unknown error"}")
        }
    )
}
