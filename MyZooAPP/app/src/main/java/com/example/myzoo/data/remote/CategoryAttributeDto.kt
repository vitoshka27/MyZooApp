package com.example.myzoo.data.remote

data class CategoryAttributeDto(
    val id: Int,
    val category_id: Int,
    val attribute_name: String
)

data class CategoryAttributeListResponse(
    val data: List<CategoryAttributeDto>
) 