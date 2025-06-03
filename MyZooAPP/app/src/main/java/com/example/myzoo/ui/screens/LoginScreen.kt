package com.example.myzoo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.myzoo.loadLastLogin
import com.example.myzoo.saveLastLogin
import com.example.myzoo.data.remote.ApiModule
import java.time.Duration
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by viewModel.loginState.collectAsState()

    // Проверка автологина при каждом появлении экрана
    LaunchedEffect(true) {
        val (savedToken, lastLogin, loggedOut) = loadLastLogin(context)
        val now = Instant.now()
        val twoMonths = Duration.ofDays(60)
        if (savedToken != null && savedToken.isNotBlank() && lastLogin != null && Duration.between(lastLogin, now) < twoMonths && loggedOut == "false") {
            ApiModule.setToken(savedToken)
            viewModel.loadProfile()
            onLoginSuccess(savedToken)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetLoginState()
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success && viewModel.token != null) {
            onLoginSuccess(viewModel.token!!)
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Вход в систему", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Логин") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.login(context, username, password) },
                    enabled = loginState !is LoginState.Loading
                ) {
                    Text("Войти")
                }
                if (loginState is LoginState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text((loginState as LoginState.Error).message, color = MaterialTheme.colorScheme.error)
                }
                if (loginState is LoginState.Loading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
} 
 
 
 
 
 
 
 
 
 
 