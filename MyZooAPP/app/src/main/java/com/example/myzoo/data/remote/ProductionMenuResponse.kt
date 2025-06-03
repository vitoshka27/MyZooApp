package com.example.myzoo.data.remote

data class ProductionItem(
    val feed_item: String,
    val feed_type: String,
    val total_produced: Float?,
    val total_feed_items: Int?
)
data class ProductionResponse(
    val data: List<ProductionItem>
) 