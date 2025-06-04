package com.example.myzoo.data.remote

import com.google.gson.annotations.SerializedName

data class StaffMenuItem(
    @SerializedName("id")
    val id: Int?,
    val last_name: String?,
    val first_name: String?,
    val middle_name: String?,
    val gender: String?,
    val birth_date: String?,
    val age: Int?,
    val hire_date: String?,
    val years_worked: Int?,
    val salary: Float?,
    val category_id: Int?,
    val category: String?,
    val animal_id: Int? = null, // только для query2
    val animal_name: String? = null, // только для query2
    val start_date: String? = null, // только для query2
    val end_date: String? = null, // только для query2
    val total_employees: Int? = null, // query1
    val total_caretakers: Int? = null // query2
)

data class StaffMenuResponse(
    val data: List<StaffMenuItem>
)

// Для выпадающего списка животных (query2)
data class AnimalShortDto(
    val id: Int,
    val name: String
)
data class AnimalShortListResponse(
    val data: List<AnimalShortDto>
) 