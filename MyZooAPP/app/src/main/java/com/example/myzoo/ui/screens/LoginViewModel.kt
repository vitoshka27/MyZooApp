package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.ApiModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.myzoo.data.remote.UserProfileResponse
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.runBlocking
import com.example.myzoo.authDataStore
import com.example.myzoo.data.remote.CategoryAttributeDto
import com.example.myzoo.data.remote.StaffAttributeValueDto
import kotlinx.coroutines.flow.asStateFlow

private val LOGGED_OUT_KEY = stringPreferencesKey("user_logged_out")

private fun setLoggedOutFlag(context: Context, value: Boolean) {
    runBlocking {
        context.authDataStore.edit { prefs ->
            prefs[LOGGED_OUT_KEY] = if (value) "true" else "false"
        }
    }
}
class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    var token: String? = null
        private set
    private val _profile = MutableStateFlow<UserProfileResponse?>(null)
    val profile: StateFlow<UserProfileResponse?> = _profile
    private val _categoryAttributes = MutableStateFlow<List<CategoryAttributeDto>>(emptyList())
    val categoryAttributes: StateFlow<List<CategoryAttributeDto>> = _categoryAttributes.asStateFlow()
    private val _staffAttributeValues = MutableStateFlow<List<StaffAttributeValueDto>>(emptyList())
    val staffAttributeValues: StateFlow<List<StaffAttributeValueDto>> = _staffAttributeValues.asStateFlow()

    fun login(context: Context, username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val json = JSONObject()
                    json.put("username", username)
                    json.put("password", password)
                    val body = json.toString().toRequestBody("application/json".toMediaType())
                    val request = Request.Builder()
                        .url(ApiModule.BASE_URL + "api/auth/login")
                        .post(body)
                        .build()
                    client.newCall(request).execute()
                }
                if (response.isSuccessful) {
                    val respJson = JSONObject(response.body?.string() ?: "")
                    token = respJson.getString("access_token")
                    ApiModule.setToken(token!!)
                    setLoggedOutFlag(context, false)
                    loadProfile()
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Неверный логин или пароль")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Ошибка авторизации", e)
                _loginState.value = LoginState.Error(e.message ?: "Ошибка сети")
            }
        }
    }


    fun loadProfile() {
        viewModelScope.launch {
            try {
                val profileResp = ApiModule.getProfile()
                _profile.value = profileResp
                if (profileResp != null) {
                    _categoryAttributes.value = try { ApiModule.getCategoryAttributes().filter { it.category_id == profileResp.category_id } } catch (_: Exception) { emptyList() }
                    _staffAttributeValues.value = try { ApiModule.getStaffAttributeValues(profileResp.id) } catch (_: Exception) { emptyList() }
                } else {
                    _categoryAttributes.value = emptyList()
                    _staffAttributeValues.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Ошибка загрузки профиля", e)
                _profile.value = null
            }
        }
    }

    fun clearProfile() {
        _profile.value = null
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
        token = null
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
} 
 
 
 
 
 
 