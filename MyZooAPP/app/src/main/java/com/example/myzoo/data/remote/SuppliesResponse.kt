package com.example.myzoo.data.remote

data class SuppliesItem(
    val name: String,
    val phone: String?,
    val address: String?,
    val order_count: Int?,
    val total_ordered_quantity: Float?,
    val avg_price: Float?,
    val total_suppliers: Int?
)

data class SuppliesResponse(
    val data: List<SuppliesItem>
) 
 
 