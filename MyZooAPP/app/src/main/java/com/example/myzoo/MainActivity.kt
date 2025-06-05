package com.example.myzoo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.myzoo.ui.theme.MyZooTheme
import com.example.myzoo.ui.screens.*
import com.example.myzoo.data.remote.ApiModule
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.MedicalServices
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.Duration
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.saveable.rememberSaveable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import android.net.Uri
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import com.example.myzoo.ui.screens.StaffListScreen
import com.example.myzoo.ui.screens.SuppliesListScreen
import com.example.myzoo.ui.screens.SplashScreen

data class MenuItem(val route: String, val icon: ImageVector)

public val Context.authDataStore by androidx.datastore.preferences.preferencesDataStore(name = "auth_prefs")
private val LAST_LOGIN_KEY = stringPreferencesKey("last_login")
private val TOKEN_KEY = stringPreferencesKey("token")
private val LOGGED_OUT_KEY = stringPreferencesKey("user_logged_out")

val LOG_TAG = "ZooAuth"

fun saveLastLogin(context: Context, token: String) {
    runBlocking {
        context.authDataStore.edit { prefs ->
            prefs[LAST_LOGIN_KEY] = Instant.now().toString()
            prefs[TOKEN_KEY] = token
            prefs[LOGGED_OUT_KEY] = "false"
        }
    }
}

fun loadLastLogin(context: Context): Triple<String?, Instant?, String?> {
    return runBlocking {
        val prefs = context.authDataStore.data.first()
        val token = prefs[TOKEN_KEY]
        val lastLogin = prefs[LAST_LOGIN_KEY]?.let { runCatching { Instant.parse(it) }.getOrNull() }
        val loggedOut = prefs[LOGGED_OUT_KEY] ?: "true"
        Log.d(LOG_TAG, "loadLastLogin: token=$token, lastLogin=$lastLogin, loggedOut=$loggedOut")
        Triple(token, lastLogin, loggedOut)
    }
}

@Composable
fun PlaceholderScreen(label: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Экран '$label' в разработке", style = MaterialTheme.typography.titleLarge)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyZooTheme {
                val context = LocalContext.current
                val loginViewModel: LoginViewModel = viewModel()
                val profile by loginViewModel.profile.collectAsState()
                val authState by remember { loginViewModel::authState }
                val accentGradient = Brush.horizontalGradient(listOf(TropicGreen, TropicTurquoise))

                // Проверка автологина при запуске
                LaunchedEffect(Unit) {
                    loginViewModel.checkAutoLogin(context)
                }

                when (authState) {
                    is AuthState.Checking -> SplashScreen()
                    is AuthState.NeedLogin -> LoginScreen(onLoginSuccess = { token ->
                        loginViewModel.onLoginSuccess(token, context)
                        loginViewModel.loadProfile()
                    })
                    is AuthState.LoggedIn -> {
                        if (profile == null) {
                            SplashScreen()
                        } else {
                            val navController = rememberNavController()
                            var lastAnimalList by remember { mutableStateOf(listOf<com.example.myzoo.data.remote.AnimalMenuItem>()) }
                            val menuItems = remember(profile) {
                                when (profile?.category_id) {
                                    5 -> listOf(
                                        MenuItem("animals", Icons.Filled.Pets),
                                        MenuItem("staff", Icons.Filled.Group),
                                        MenuItem("supplies", Icons.Filled.LocalShipping),
                                        MenuItem("production", Icons.Filled.Factory),
                                        MenuItem("exchange", Icons.Filled.SwapHoriz),
                                        MenuItem("profile", Icons.Filled.AccountCircle)
                                    )
                                    1 -> listOf(
                                        MenuItem("animals", Icons.Filled.Pets),
                                        MenuItem("treatment", Icons.Filled.Healing),
                                        MenuItem("medical", Icons.Filled.MedicalServices),
                                        MenuItem("profile", Icons.Filled.AccountCircle)
                                    )
                                    3 -> listOf(
                                        MenuItem("animals", Icons.Filled.Pets),
                                        MenuItem("feeding", Icons.Filled.Restaurant),
                                        MenuItem("profile", Icons.Filled.AccountCircle)
                                    )
                                    else -> listOf(
                                        MenuItem("animals", Icons.Filled.Pets),
                                        MenuItem("profile", Icons.Filled.AccountCircle)
                                    )
                                }
                            }
                            var selectedMenu by remember { mutableStateOf(menuItems.first().route) }
                            Scaffold(
                                bottomBar = {
                                    val darkBlue = Color(0xFF232946)
                                    val inactiveIcon = Color.White
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .height(72.dp)
                                            .background(darkBlue)
                                    ) {
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .height(2.dp)
                                                .background(Color.White.copy(alpha = 0.10f))
                                                .align(Alignment.TopCenter)
                                        )
                                        Row(
                                            Modifier.fillMaxSize(),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            menuItems.forEach { item ->
                                                val isSelected = selectedMenu == item.route
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clip(RoundedCornerShape(50))
                                                        .then(
                                                            if (isSelected)
                                                                Modifier.background(accentGradient, RoundedCornerShape(32))
                                                            else
                                                                Modifier.background(Color.Transparent, RoundedCornerShape(32))
                                                        )
                                                        .clickable {
                                                            selectedMenu = item.route
                                                            navController.navigate(item.route) {
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        }
                                                        .padding(vertical = 16.dp, horizontal = 2.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        item.icon,
                                                        contentDescription = null,
                                                        tint = if (isSelected) Color.White else inactiveIcon,
                                                        modifier = Modifier.size(30.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            ) { innerPadding ->
                                NavHost(
                                    navController = navController,
                                    startDestination = menuItems.first().route,
                                    modifier = Modifier.padding(innerPadding)
                                ) {
                                    composable("animals") {
                                        AnimalListScreen(onAnimalClick = { animalMenuItem ->
                                            lastAnimalList = lastAnimalList + animalMenuItem
                                        })
                                    }
                                    composable("profile") {
                                        ProfileScreen(
                                            profile = profile,
                                            onLogout = {
                                                loginViewModel.logout(context)
                                                loginViewModel.clearProfile()
                                                loginViewModel.resetLoginState()
                                                selectedMenu = "animals"
                                                navController.navigate("animals") {
                                                    popUpTo(0) { inclusive = true }
                                                    launchSingleTop = true
                                                }
                                            },
                                            onAdminPanel = {
                                                navController.navigate("admin_panel")
                                            },
                                            loginViewModel = loginViewModel,
                                            navController = navController
                                        )
                                    }
                                    composable("staff") { StaffListScreen() }
                                    composable("supplies") { SuppliesListScreen() }
                                    composable("production") { ProductionListScreen() }
                                    composable("exchange") { ExchangeListScreen() }
                                    composable("admin_panel") {
                                        AdminPanelScreen(onBack = { navController.popBackStack() })
                                    }
                                    composable("treatment") {
                                        VetTreatmentListScreen()
                                    }
                                    composable("medical") { MedicalExamListScreen() }
                                    composable("feeding") { FeedingListScreen() }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
