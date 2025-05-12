package com.example.exampleapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.rpc.CoreServiceClient
import com.example.rpc.CoreServiceServer
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun main() = application {
    startKoin {
        modules(
            listOf(
                ExampleAppModule().module,
                CoreServiceClient().module,
                CoreServiceServer().module,
                module
            )
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "CoreService"
    ) {
        App()
    }
}
