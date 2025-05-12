package com.example.rpc.api

open class LoggerWrapper(
    val infoLogger: (String) -> Unit,
    val warningLogger: (String) -> Unit,
    val errorLogger: (String) -> Unit
) {

    constructor(logger: (String) -> Unit) : this(logger, logger, logger)

    fun info(message: String) = infoLogger(message)

    fun warning(message: String) = warningLogger(message)

    fun error(message: String) = errorLogger(message)
}
