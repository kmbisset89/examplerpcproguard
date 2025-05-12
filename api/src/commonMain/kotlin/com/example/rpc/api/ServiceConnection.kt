package com.example.rpc.api

sealed class ServiceConnection {

    abstract val ip: String

    data object Local : ServiceConnection() {
        override val ip: String = "127.0.0.1"
    }

    data class Remote(override val ip: String) : ServiceConnection()
}
