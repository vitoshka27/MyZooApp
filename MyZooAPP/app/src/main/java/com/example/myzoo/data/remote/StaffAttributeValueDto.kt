package com.example.myzoo.data.remote

data class StaffAttributeValueDto(
    val staff_id: Int,
    val attribute_id: Int,
    val attribute_value: String
)

data class StaffAttributeValueListResponse(
    val data: List<StaffAttributeValueDto>
) 