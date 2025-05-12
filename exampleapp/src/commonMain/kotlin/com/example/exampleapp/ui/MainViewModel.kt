package com.example.exampleapp.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpc.BasicService
import com.example.rpc.ServiceConfiguration
import com.example.rpc.api.LoggerWrapper
import com.example.rpc.api.ServiceConnection
import com.example.rpc.bl.ClientFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import org.koin.core.component.KoinComponent

@Factory
class MainViewModel(
    private val loggerWrapper: LoggerWrapper
) : ViewModel(),
    KoinComponent {

    var isLoading by mutableStateOf(true)
        private set

    internal var testMessages by mutableStateOf("")
        private set

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val snackbarHostState = SnackbarHostState()

    private val clientFacade by lazy {
        getKoin().get<ClientFacade>()
    }

    private val basicService = BasicService(
        ServiceConfiguration(true, ServiceConnection.Local, serviceScope, port = 8081)
    )
    init {
        viewModelScope.launch(Dispatchers.IO) {
            basicService.start()
        }

        viewModelScope.launch(Dispatchers.IO) {
            clientFacade.testFlow().collect {
                withContext(Dispatchers.Main) {
                    testMessages = it
                }
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.Main) {
                isLoading = false
            }
        }
    }

    internal fun onInteraction(interactions: MainViewInteractions) {
        when (interactions) {
            MainViewInteractions.SendTestMessage -> {
                viewModelScope.launch {
                    val result = clientFacade.check()
                    snackbarHostState.showSnackbar(result)
                }
            }
        }
    }
}
