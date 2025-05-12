package com.example.exampleapp

import com.example.rpc.api.LoggerWrapper
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@ComponentScan("com.example.exampleapp")
@Module
class ExampleAppModule

val module = module {
    single { LoggerWrapper { println(it) } }
}
