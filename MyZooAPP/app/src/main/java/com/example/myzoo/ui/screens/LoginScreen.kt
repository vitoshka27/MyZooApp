package com.example.myzoo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.myzoo.loadLastLogin
import com.example.myzoo.saveLastLogin
import com.example.myzoo.data.remote.ApiModule
import java.time.Duration
import java.time.Instant
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onLoginSuccess: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()
    val screenHeight = configuration.screenHeightDp.toFloat()
    val aspectRatio = screenHeight / screenWidth

    // Индивидуальные параметры для каждого фона
    data class LayoutParams(
        val backgroundRes: Int,
        val topPadding: Dp,
        val loginFieldSpacing: Dp,
        val passwordFieldSpacing: Dp,
        val buttonSpacing: Dp,
        val fieldHeight: Dp,
        val buttonHeight: Dp,
        val fontSizeField: TextUnit,
        val fontSizeButton: TextUnit
    )
    val layoutParams = when {
        aspectRatio >= 2.2f -> LayoutParams(
            backgroundRes = com.example.myzoo.R.drawable.background_9x21,
            topPadding = 170.dp,
            loginFieldSpacing = 10.dp,
            passwordFieldSpacing = 14.dp,
            buttonSpacing = 0.dp,
            fieldHeight = 58.dp,
            buttonHeight = 52.dp,
            fontSizeField = 24.sp,
            fontSizeButton = 26.sp
        )
        aspectRatio >= 2.0f -> LayoutParams(
            backgroundRes = com.example.myzoo.R.drawable.background_9x18,
            topPadding = 140.dp,
            loginFieldSpacing = 12.dp,
            passwordFieldSpacing = 14.dp,
            buttonSpacing = 0.dp,
            fieldHeight = 56.dp,
            buttonHeight = 46.dp,
            fontSizeField = 22.sp,
            fontSizeButton = 24.sp
        )
        aspectRatio >= 1.7f -> LayoutParams(
            backgroundRes = com.example.myzoo.R.drawable.background_9x16,
            topPadding = 120.dp,
            loginFieldSpacing = 14.dp,
            passwordFieldSpacing = 16.dp,
            buttonSpacing = 0.dp,
            fieldHeight = 52.dp,
            buttonHeight = 44.dp,
            fontSizeField = 16.sp,
            fontSizeButton = 24.sp
        )
        else -> LayoutParams(
            backgroundRes = com.example.myzoo.R.drawable.background_2x3,
            topPadding = 100.dp,
            loginFieldSpacing = 6.dp,
            passwordFieldSpacing = 8.dp,
            buttonSpacing = 0.dp,
            fieldHeight = 52.dp,
            buttonHeight = 40.dp,
            fontSizeField = 14.sp,
            fontSizeButton = 24.sp
        )
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = layoutParams.backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(top = layoutParams.topPadding)
                    .fillMaxWidth(0.85f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Логин", fontSize = layoutParams.fontSizeField, color = Color(0xFF7A868C), fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFebede1),
                        focusedContainerColor = Color(0xFFebede1),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedTextColor = Color(0xFF7A868C),
                        focusedTextColor = Color(0xFF7A868C)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = layoutParams.fontSizeField,
                        color = Color(0xFF7A868C),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(layoutParams.fieldHeight)
                )
                Spacer(modifier = Modifier.height(layoutParams.loginFieldSpacing))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Пароль", fontSize = layoutParams.fontSizeField, color = Color(0xFF7A868C), fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFebede1),
                        focusedContainerColor = Color(0xFFebede1),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedTextColor = Color(0xFF7A868C),
                        focusedTextColor = Color(0xFF7A868C)
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = layoutParams.fontSizeField,
                        color = Color(0xFF7A868C),
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(layoutParams.fieldHeight)
                )
                Spacer(modifier = Modifier.height(layoutParams.passwordFieldSpacing))
                Button(
                    onClick = { viewModel.login(context, username, password) },
                    enabled = loginState !is LoginState.Loading,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFf6ac2c)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(layoutParams.buttonHeight)
                ) {
                    Text("Войти", fontSize = layoutParams.fontSizeButton, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(layoutParams.buttonSpacing))
                if (loginState is LoginState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        (loginState as LoginState.Error).message,
                        color = Color(0xFFFF3B30),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (loginState is LoginState.Loading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(name = "9:21", widthDp = 360, heightDp = 840, showBackground = true)
@Composable
fun LoginScreenPreview_9x21() {
    LoginScreen()
}

@Preview(name = "9:18", widthDp = 360, heightDp = 720, showBackground = true)
@Composable
fun LoginScreenPreview_9x18() {
    LoginScreen()
}

@Preview(name = "9:16", widthDp = 360, heightDp = 640, showBackground = true)
@Composable
fun LoginScreenPreview_9x16() {
    LoginScreen()
}

@Preview(name = "2:3", widthDp = 360, heightDp = 540, showBackground = true)
@Composable
fun LoginScreenPreview_2x3() {
    LoginScreen()
} 
 
 
 
 
 
 
 
 
 
 