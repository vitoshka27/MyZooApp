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
data class FeedInventoryItem(
    val id: Int,
    val feed_item_id: Int,
    val quantity: Float?,
    val received_date: String?
)
data class FeedInventoryResponse(
    val total: Int?,
    val data: List<FeedInventoryItem>
) 