package com.example.sicemultiplataform

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sicemultiplataform.ui.theme.screens.HomeScreen
import com.example.sicemultiplataform.ui.theme.screens.LoginScreen
import com.example.sicemultiplataform.ui.theme.screens.SNViewModel
import com.example.sicemultiplataform.ui.theme.screens.snViewModelFactory

@Composable
fun App() {
    MaterialTheme {
        val snViewModel: SNViewModel = viewModel(factory = snViewModelFactory())
        val uiState = snViewModel.uiState

        Scaffold { padding ->
            if (uiState.isLogged) {
                HomeScreen(
                    padding  = padding,
                    onLogout = { snViewModel.logout() }
                )
            } else {
                LoginScreen(
                    uiState = uiState,
                    onLogin = { m, p -> snViewModel.login(m, p) }
                )
            }
        }
    }
}