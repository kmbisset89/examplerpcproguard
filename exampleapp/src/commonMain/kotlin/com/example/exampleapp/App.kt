package com.example.exampleapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.exampleapp.ui.MainViewInteractions
import com.example.exampleapp.ui.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        val viewModel = koinViewModel<MainViewModel>()

        Scaffold(snackbarHost = {
            SnackbarHost(viewModel.snackbarHostState)
        }) { paddingValues ->
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                if (viewModel.isLoading) {
                    LoadingUi()
                } else {
                    Column {
                        Button({ viewModel.onInteraction(MainViewInteractions.SendTestMessage) }) {
                            Text("Send Test Message")
                        }

                        HorizontalDivider()
                        Text("Test Messages Flow:", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = viewModel.testMessages,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingUi(
    message: String? = null,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    sizeRatio: Float = .25f
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(Modifier.fillMaxSize(sizeRatio))
            Text(message ?: "Loading...", style = textStyle)
        }
    }
}
