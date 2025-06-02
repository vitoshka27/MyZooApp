package com.example.myzoo.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call

object ApiModule {
    const val BASE_URL = "http://192.168.0.101:5000/"
    private var token: String? = null

    private val authInterceptor = Interceptor { chain ->
        val request = token?.let {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $it")
                .build()
        } ?: chain.request()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val zooApi: ZooApiService = retrofit.create(ZooApiService::class.java)

    // DTO для смены пароля
    data class ChangePasswordRequest(val old_password: String, val new_password: String)
    data class ChangePasswordResponse(val msg: String)

    interface ChangePasswordApi {
        @POST("api/auth/change_password")
        fun changePassword(@Body body: ChangePasswordRequest): Call<ChangePasswordResponse>
    }

    private val changePasswordApi: ChangePasswordApi = retrofit.create(ChangePasswordApi::class.java)

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? = token

    suspend fun getCategoryAttributes(): List<CategoryAttributeDto> = zooApi.getCategoryAttributes().data
    suspend fun getStaffAttributeValues(staffId: Int): List<StaffAttributeValueDto> = zooApi.getStaffAttributeValues(staffId).data
    suspend fun getProfile(): UserProfileResponse = zooApi.getProfile()

    fun uploadAvatar(avatar: MultipartBody.Part, avatarOriginal: MultipartBody.Part) =
        zooApi.uploadAvatar(avatar, avatarOriginal)

    fun changePassword(oldPassword: String, newPassword: String) =
        changePasswordApi.changePassword(ChangePasswordRequest(oldPassword, newPassword))
} 
 
 
 