package com.example.exampleapp.bl

import com.example.rpc.api.LoggerWrapper
import org.koin.core.annotation.Single

@Single([LoggerWrapper::class])
class CoreServiceLogger :
    LoggerWrapper(
        infoLogger = {
            println("INFO: $it")
        },
        warningLogger = {
            println("WARNING: $it")
        },
        errorLogger = {
            println("ERROR: $it")
        }

    )
