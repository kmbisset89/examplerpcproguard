package com.example.rpc

import com.example.rpc.api.ServiceConnection
import kotlinx.coroutines.CoroutineScope

class ServiceConfiguration(
    val wait: Boolean,
    val host: ServiceConnection,
    val scope: CoroutineScope,
    val port: Int = 8081
)
