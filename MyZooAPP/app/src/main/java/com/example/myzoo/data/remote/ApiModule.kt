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
import com.example.myzoo.data.remote.StaffMenuItem
import com.example.myzoo.data.remote.StaffMenuResponse
import com.example.myzoo.data.remote.AnimalShortDto
import retrofit2.Response
import com.google.gson.JsonObject

object ApiModule {
    const val BASE_URL = "http://45.156.26.89:5000/"
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

    suspend fun getStaffQuery1(
        categoryId: Int? = null,
        gender: String? = null,
        salaryMin: Float? = null,
        salaryMax: Float? = null,
        yearsWorkedMin: Int? = null,
        yearsWorkedMax: Int? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<StaffMenuItem> = zooApi.getStaffQuery1(
        categoryId, gender, salaryMin, salaryMax, yearsWorkedMin, yearsWorkedMax, ageMin, ageMax, orderBy, orderDir
    ).data

    suspend fun getStaffQuery2(
        animalId: Int?,
        startDate: String? = null,
        endDate: String? = null,
        gender: String? = null,
        categoryId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<StaffMenuItem> = zooApi.getStaffQuery2(
        animalId, startDate, endDate, gender, categoryId, orderBy, orderDir
    ).data

    suspend fun getAnimalsShort(): List<AnimalShortDto> = zooApi.getAnimalsShort().data

    suspend fun getStaffCategories(): List<StaffCategoryDto> = zooApi.getStaffCategories().data

    suspend fun getExchangeQuery13(
        speciesId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<ExchangeItem> = zooApi.getExchangeQuery13(speciesId, orderBy, orderDir).data

    suspend fun getProductionQuery9(
        feedTypeId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<ProductionItem> = zooApi.getProductionQuery9(feedTypeId, orderBy, orderDir).data

    // --- Ответы для админ-панели ---
    data class AdminTableResponse(
        val data: List<Map<String, Any?>> = emptyList(),
        val total: Int = 0,
        val page: Int = 1,
        val limit: Int = 20
    )
    data class AdminTableDeleteResponse(val success: Boolean, val msg: String?)
    data class AdminTableEditResponse(val success: Boolean, val msg: String?)

    suspend fun getAdminTable(table: String): AdminTableResponse = zooApi.getAdminTable(table)
    suspend fun deleteAdminTableRow(table: String, id: Int): AdminTableDeleteResponse {
        // Пробуем обычный вызов
        return try {
            val response = zooApi.deleteAdminTableRowRaw(table, id)
            if (response.isSuccessful) {
                val body = response.body()
                val json = body?.string()?.let { com.google.gson.JsonParser.parseString(it).asJsonObject }
                val msg = json?.get("msg")?.asString ?: json?.get("message")?.asString
                val success = when {
                    json?.has("success") == true -> json["success"].asBoolean
                    msg?.contains("удален", ignoreCase = true) == true -> true
                    else -> false
                }
                AdminTableDeleteResponse(success, msg)
            } else {
                AdminTableDeleteResponse(false, response.errorBody()?.string())
            }
        } catch (e: Exception) {
            AdminTableDeleteResponse(false, e.message)
        }
    }
    suspend fun addAdminTableRow(table: String, body: Map<String, String>): AdminTableEditResponse = zooApi.addAdminTableRow(table, body)
    suspend fun updateAdminTableRow(table: String, id: Int, body: Map<String, String>): AdminTableEditResponse = zooApi.updateAdminTableRow(table, id, body)
} 
 
 
 