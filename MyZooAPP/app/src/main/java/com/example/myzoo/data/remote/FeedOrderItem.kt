package com.example.myzoo.data.remote

data class FeedOrderItem(
    val id: Int,
    val feed_supplier_id: Int,
    val feed_item_id: Int,
    val ordered_quantity: Float,
    val order_date: String,
    val delivery_date: String?,
    val price: Float,
    val status: String
)

data class FeedOrdersResponse(
    val data: List<FeedOrderItem>
)

data class FeedItemDto(
    val id: Int,
    val name: String
)

data class FeedItemListResponse(
    val data: List<FeedItemDto>
) 