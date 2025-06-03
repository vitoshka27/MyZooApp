package com.example.myzoo.data.repository

import com.example.myzoo.data.remote.ApiModule
import com.example.myzoo.data.remote.SuppliesItem
import com.example.myzoo.data.remote.ProductionItem

class SuppliesRepository {
    private val api = ApiModule.zooApi

    suspend fun getSupplies(
        feedTypeId: Int? = null,
        orderDateStart: String? = null,
        orderDateEnd: String? = null,
        quantityMin: Float? = null,
        quantityMax: Float? = null,
        priceMin: Float? = null,
        priceMax: Float? = null,
        deliveryDateStart: String? = null,
        deliveryDateEnd: String? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<SuppliesItem> = api.getSuppliesQuery8(
        feedTypeId, orderDateStart, orderDateEnd, quantityMin, quantityMax, priceMin, priceMax, deliveryDateStart, deliveryDateEnd, orderBy, orderDir
    ).data
}

class ProductionRepository {
    suspend fun getProductionsQuery9(
        feedTypeId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ): List<ProductionItem> = ApiModule.getProductionQuery9(feedTypeId, orderBy, orderDir)
} 