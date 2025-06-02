package com.example.myzoo.data.remote

data class UserProfileResponse(
    val id: Int,
    val username: String,
    val last_name: String,
    val first_name: String,
    val middle_name: String?,
    val category_id: Int,
    val category_name: String?,
    val role: Int,
    val is_active: Boolean,
    val birth_date: String?,
    val hire_date: String?,
    val avatar_url: String?,
    val avatar_original_url: String?
)

// Ответ на загрузку аватара
data class UploadAvatarResponse(
    val avatarUrl: String?,
    val avatarOriginalUrl: String?
) 
 
 