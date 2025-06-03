package com.example.myzoo.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myzoo.data.remote.UserProfileResponse
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import coil.compose.rememberAsyncImagePainter
import androidx.compose.runtime.saveable.rememberSaveable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Request
import java.io.File
import androidx.core.net.toFile
import android.widget.Toast
import android.util.Log
import coil.request.ImageRequest
import coil.request.CachePolicy
import com.example.myzoo.data.remote.ApiModule
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import java.io.FileOutputStream
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.shadow
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicYellow
import com.example.myzoo.ui.theme.TropicOrange
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.LocalDensity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Build
import android.Manifest
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.navigation.NavController
import androidx.lifecycle.SavedStateHandle
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import android.provider.MediaStore
import android.graphics.ImageDecoder
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.myzoo.data.remote.UploadAvatarResponse
import androidx.compose.material3.OutlinedTextFieldDefaults
import org.json.JSONObject

private val Context.profileDataStore by androidx.datastore.preferences.preferencesDataStore(name = "profile_prefs")
private val AVATAR_URI_KEY = stringPreferencesKey("avatar_uri")

fun saveAvatarUri(context: Context, uri: String?) {
    runBlocking {
        context.profileDataStore.edit { prefs ->
            if (uri != null) prefs[AVATAR_URI_KEY] = uri else prefs.remove(AVATAR_URI_KEY)
        }
    }
}

fun loadAvatarUri(context: Context): String? {
    return runBlocking {
        val prefs = context.profileDataStore.data.first()
        prefs[AVATAR_URI_KEY]
    }
}

fun displayDate(date: String?): String {
    if (date.isNullOrBlank() || date == "null") return "-"
    return try {
        LocalDate.parse(date.substring(0, 10)).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: Exception) {
        date
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfileResponse?,
    onLogout: () -> Unit = {},
    onAdminPanel: () -> Unit = {},
    loginViewModel: LoginViewModel,
    onSettings: () -> Unit = {},
    navController: NavController
) {
    val context = LocalContext.current
    val defaultAvatar = painterResource(id = com.example.myzoo.R.drawable.ic_default_avatar)
    var avatarUpdateKey by remember { mutableStateOf(0) }
    val categoryAttributes by loginViewModel.categoryAttributes.collectAsState()
    val staffAttributeValues by loginViewModel.staffAttributeValues.collectAsState()
    var showAvatarDialog by remember { mutableStateOf(false) }
    var showSettingsDrawer by remember { mutableStateOf(false) }
    var avatarUrlState by rememberSaveable(profile?.id) { mutableStateOf(profile?.avatar_url) }
    var avatarVersion by rememberSaveable(profile?.id) { mutableStateOf<String?>(null) }
    var avatarOriginalUrlState by rememberSaveable(profile?.id) { mutableStateOf(profile?.avatar_original_url) }
    var originalPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var showOriginalDialog by remember { mutableStateOf(false) }
    // --- Новый способ: CropImageContract ---
    var croppedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var croppedAvatarBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            croppedAvatarUri = uri
            if (uri != null && originalPhotoUri != null) {
                croppedAvatarBitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
                // --- Отправка на сервер ---
                uploadAvatarToServer(context, uri, originalPhotoUri!!) { success, newAvatarUrl, newAvatarOriginalUrl ->
                    if (success) {
                        loginViewModel.loadProfile()
                        avatarVersion = System.currentTimeMillis().toString()
                    } else {
                    }
                }
            }
        } else {
            val exception = result.error
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            originalPhotoUri = uri // сохраняем оригинал
            val cropOptions = CropImageContractOptions(
                uri,
                CropImageOptions().apply {
                    cropShape = CropImageView.CropShape.OVAL
                    fixAspectRatio = true
                    aspectRatioX = 1
                    aspectRatioY = 1
                    guidelines = CropImageView.Guidelines.ON
                }
            )
            imageCropLauncher.launch(cropOptions)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Нет доступа к фото. Разрешите доступ в настройках.", Toast.LENGTH_LONG).show()
        }
    }

    // Синхронизация состояния с profile при изменении профиля
    LaunchedEffect(profile?.avatar_url, profile?.avatar_original_url) {
        avatarUrlState = profile?.avatar_url
        avatarOriginalUrlState = profile?.avatar_original_url
        avatarVersion = null
    }
    Box(modifier = Modifier.fillMaxSize().background(TropicBackground)) {
        // Верхняя цветная шапка
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            TropicGreen,
                            TropicTurquoise
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        ) {
            // Кнопка настроек
            IconButton(onClick = { showSettingsDrawer = true }, modifier = Modifier.align(Alignment.TopEnd).padding(20.dp)) {
                Icon(Icons.Filled.Settings, contentDescription = "Настройки", tint = TropicOnPrimary)
            }
            // ФИО и username/email
            Column(
                modifier = Modifier.align(Alignment.TopStart).padding(start = 32.dp, top = 56.dp)
            ) {
                Text(
                    text = "${profile?.last_name ?: ""} ${profile?.first_name ?: ""} ${profile?.middle_name ?: ""}",
                    color = TropicOnPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = profile?.username ?: "",
                    color = TropicOnPrimary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        // Аватарка (крупная, частично на шапке)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp)
        ) {
            LaunchedEffect(profile?.avatar_url) { avatarUrlState = profile?.avatar_url }
            Box(contentAlignment = Alignment.BottomEnd) {
                val showOriginal = showOriginalDialog && (originalPhotoUri != null || croppedAvatarBitmap != null)
                if (croppedAvatarBitmap != null) {
                    Image(
                        bitmap = croppedAvatarBitmap!!.asImageBitmap(),
                        contentDescription = "Аватарка",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(4.dp, TropicLime, CircleShape)
                            .shadow(12.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { showOriginalDialog = true },
                    )
                } else if (!avatarUrlState.isNullOrBlank()) {
                    val avatarUrlWithVersion = if (!avatarVersion.isNullOrBlank()) {
                        ApiModule.BASE_URL.trimEnd('/') + avatarUrlState + "?v=" + avatarVersion
                    } else {
                        ApiModule.BASE_URL.trimEnd('/') + avatarUrlState
                    }
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(avatarUrlWithVersion)
                                .build()
                        ),
                        contentDescription = "Аватарка",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(4.dp, TropicLime, CircleShape)
                            .shadow(12.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { showOriginalDialog = true },
                    )
                } else {
                    Image(
                        painter = defaultAvatar,
                        contentDescription = "Аватарка",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape)
                            .border(4.dp, TropicLime, CircleShape)
                            .shadow(12.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { showOriginalDialog = true },
                    )
                }
                // Кнопка смены аватарки
                IconButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= 33) {
                            val perm = Manifest.permission.READ_MEDIA_IMAGES
                            if (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED) {
                                imagePickerLauncher.launch("image/*")
                            } else {
                                permissionLauncher.launch(perm)
                            }
                        } else {
                            imagePickerLauncher.launch("image/*")
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(TropicGreen, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Сменить аватарку", tint = Color.White)
                }
            }
        }
        // --- Информация о сотруднике под аватаркой ---
        val darkBlue = Color(0xFF232946)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 400.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Должность
            if (!profile?.category_name.isNullOrBlank()) {
                Column(Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.WorkOutline, contentDescription = null, tint = darkBlue.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Категория",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkBlue.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = profile.category_name,
                        style = MaterialTheme.typography.titleMedium,
                        color = darkBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                    )
                }
            }
            // Дата рождения
            if (!profile?.birth_date.isNullOrBlank()) {
                Column(Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Cake, contentDescription = null, tint = darkBlue.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Дата рождения",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkBlue.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = displayDate(profile.birth_date),
                        style = MaterialTheme.typography.titleMedium,
                        color = darkBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                    )
                }
            }
            // Дата найма
            if (!profile?.hire_date.isNullOrBlank()) {
                Column(Modifier.padding(bottom = 12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Badge, contentDescription = null, tint = darkBlue.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Дата найма",
                            style = MaterialTheme.typography.bodySmall,
                            color = darkBlue.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = displayDate(profile.hire_date),
                        style = MaterialTheme.typography.titleMedium,
                        color = darkBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                    )
                }
            }
            // Атрибуты
            if (categoryAttributes.isNotEmpty()) {
                categoryAttributes.forEach { attr ->
                    val value = staffAttributeValues.firstOrNull { it.attribute_id == attr.id }?.attribute_value
                    Column(Modifier.padding(bottom = 12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = darkBlue.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = attr.attribute_name,
                                style = MaterialTheme.typography.bodySmall,
                                color = darkBlue.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            text = value?.takeIf { it.isNotBlank() } ?: "-",
                            style = MaterialTheme.typography.titleMedium,
                            color = darkBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 32.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
        // Диалог с оригиналом
        if (showOriginalDialog) {
            Dialog(onDismissRequest = { showOriginalDialog = false }) {
                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    if (!avatarOriginalUrlState.isNullOrBlank()) {
                        val avatarOriginalUrlWithVersion = if (!avatarVersion.isNullOrBlank()) {
                            ApiModule.BASE_URL.trimEnd('/') + avatarOriginalUrlState + "?v=" + avatarVersion
                        } else {
                            ApiModule.BASE_URL.trimEnd('/') + avatarOriginalUrlState
                        }
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(avatarOriginalUrlWithVersion)
                                    .build(),
                                placeholder = painterResource(id = com.example.myzoo.R.drawable.ic_default_avatar)
                            ),
                            contentDescription = "Оригинал",
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showOriginalDialog = false },
                            alignment = Alignment.Center
                        )
                    } else if (originalPhotoUri != null) {
                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, originalPhotoUri)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, originalPhotoUri!!)
                            ImageDecoder.decodeBitmap(source)
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Оригинал",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { showOriginalDialog = false },
                                alignment = Alignment.Center
                            )
                        }
                    }
                }
            }
        }
        // Контент профиля
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 320.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопки выхода и админ-панели убраны отсюда, теперь они в настройках
        }
        // Окно настроек справа
        SettingsDrawer(
            visible = showSettingsDrawer,
            profile = profile,
            navController = navController,
            onLogout = {
                showSettingsDrawer = false
                onLogout()
            },
            onClose = { showSettingsDrawer = false },
            onChangePassword = { showChangePasswordDialog = true }
        )
    }
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSuccess = { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                showChangePasswordDialog = false
            }
        )
    }
}

@Composable
fun SettingsDrawer(
    visible: Boolean,
    profile: UserProfileResponse?,
    navController: NavController,
    onLogout: () -> Unit = {},
    onClose: () -> Unit = {},
    onChangePassword: () -> Unit = {},
){
    // Анимация выдвижения справа
    val panelWidthDp = 260.dp
    val density = LocalDensity.current
    val panelWidthPx = with(density) { panelWidthDp.roundToPx() }
    Box(
        Modifier
            .fillMaxSize()
    ) {
        // Затемнение с fade
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(onClick = onClose, indication = null, interactionSource = remember { MutableInteractionSource() })
            )
        }
        // Панель слайдится справа
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInHorizontally(initialOffsetX = { panelWidthPx }),
                exit = slideOutHorizontally(targetOffsetX = { panelWidthPx })
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 32.dp, bottomStart = 32.dp),
                    tonalElevation = 8.dp,
                    color = TropicSurface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(panelWidthDp)
                ) {
                    Column(
                        Modifier
                            .fillMaxHeight()
                            .padding(28.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Настройки", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = onChangePassword,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TropicGreen)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Сменить пароль", color = Color.White)
                            }
                            Spacer(Modifier.height(18.dp))
                            if (profile?.category_id == 5) {
                                Button(
                                    onClick = {
                                        onClose()
                                        navController.navigate("admin_panel")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                                ) {
                                    Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.White)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Редактировать данные системы", color = Color.White)
                                }
                                Spacer(Modifier.height(18.dp))
                            }
                        }
                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TropicOrange)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Выйти", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onSuccess: (String) -> Unit) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val successColor = Color(0xFF2E7D32)
    val errorColor = Color(0xFFD32F2F)
    val dialogShape = RoundedCornerShape(24.dp)
    val buttonShape = RoundedCornerShape(16.dp)
    val fieldShape = RoundedCornerShape(16.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
        focusedBorderColor = TropicTurquoise,
        cursorColor = TropicTurquoise,
        focusedLabelColor = Color.Gray,
        unfocusedLabelColor = Color.Gray
    )
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = dialogShape,
            tonalElevation = 8.dp,
            color = Color(0xFFEFFAF3)
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(
                    "Смена пароля",
                    style = MaterialTheme.typography.titleLarge,
                    color = TropicTurquoise,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("Старый пароль") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Новый пароль") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Повторите новый пароль") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                if (errorMsg != null) {
                    Text(errorMsg!!, color = errorColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 6.dp))
                }
                Spacer(Modifier.height(18.dp))
                Divider(color = Color(0x22000000), thickness = 1.dp)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        shape = buttonShape,
                        colors = ButtonDefaults.buttonColors(containerColor = TropicGreen, contentColor = Color.White)
                    ) { Text("Отмена") }
                    val isButtonActive = !isLoading && oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank()
                    val buttonTextColor = if (isButtonActive) Color.White else TropicTurquoise
                    Button(
                        onClick = {
                            if (newPassword != confirmPassword) {
                                errorMsg = "Пароли не совпадают"
                                return@Button
                            }
                            isLoading = true
                            errorMsg = null
                            ApiModule.changePassword(oldPassword, newPassword).enqueue(object : retrofit2.Callback<ApiModule.ChangePasswordResponse> {
                                override fun onResponse(call: retrofit2.Call<ApiModule.ChangePasswordResponse>, response: retrofit2.Response<ApiModule.ChangePasswordResponse>) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        onSuccess(response.body()?.msg ?: "Пароль успешно изменён")
                                    } else {
                                        val errorBody = response.errorBody()?.string() ?: "Ошибка смены пароля"
                                        var msg: String? = null
                                        try {
                                            val json = JSONObject(errorBody)
                                            msg = json.optString("msg")
                                        } catch (_: Exception) {}
                                        if (response.code() == 400 && msg == "Старый пароль неверен") {
                                            errorMsg = "Старый пароль неверен"
                                        } else if (!msg.isNullOrBlank()) {
                                            errorMsg = msg
                                        } else {
                                            errorMsg = errorBody
                                        }
                                    }
                                }
                                override fun onFailure(call: retrofit2.Call<ApiModule.ChangePasswordResponse>, t: Throwable) {
                                    isLoading = false
                                    errorMsg = t.message ?: "Ошибка сети"
                                }
                            })
                        },
                        enabled = isButtonActive,
                        shape = buttonShape,
                        colors = if (isButtonActive) {
                            ButtonDefaults.buttonColors(containerColor = TropicTurquoise, contentColor = Color.White)
                        } else {
                            ButtonDefaults.buttonColors(containerColor = TropicTurquoise.copy(alpha = 0.6f), contentColor = TropicTurquoise)
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .defaultMinSize(minWidth = 120.dp)
                            .border(1.dp, TropicTurquoise, buttonShape)
                    ) {
                        if (isLoading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = TropicTurquoise)
                        else Text("Сменить", color = buttonTextColor)
                    }
                }
            }
        }
    }
}

fun uploadAvatarToServer(context: Context, croppedUri: Uri, originalUri: Uri, onResult: (Boolean, String?, String?) -> Unit) {
    val contentResolver = context.contentResolver
    // Копируем обрезанный файл
    val croppedFile = File(context.cacheDir, "avatar_upload_cropped.jpg")
    contentResolver.openInputStream(croppedUri)?.use { input ->
        croppedFile.outputStream().use { output -> input.copyTo(output) }
    }
    val croppedPart = MultipartBody.Part.createFormData(
        "avatar", croppedFile.name, croppedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    )
    // Копируем оригинал
    val originalFile = File(context.cacheDir, "avatar_upload_original.jpg")
    contentResolver.openInputStream(originalUri)?.use { input ->
        originalFile.outputStream().use { output -> input.copyTo(output) }
    }
    val originalPart = MultipartBody.Part.createFormData(
        "avatar_original", originalFile.name, originalFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
    )
    val call = ApiModule.uploadAvatar(croppedPart, originalPart)
    call.enqueue(object : retrofit2.Callback<UploadAvatarResponse> {
        override fun onResponse(call: retrofit2.Call<UploadAvatarResponse>, response: retrofit2.Response<UploadAvatarResponse>) {
            if (response.isSuccessful) {
                onResult(true, response.body()?.avatarUrl, response.body()?.avatarOriginalUrl)
            } else {
                onResult(false, null, null)
            }
        }
        override fun onFailure(call: retrofit2.Call<UploadAvatarResponse>, t: Throwable) {
            onResult(false, null, null)
        }
    })
}
