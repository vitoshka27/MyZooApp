package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.SuppliesItem
import com.example.myzoo.data.repository.SuppliesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.FeedTypeDto

class SuppliesViewModel : ViewModel() {
    private val repository = SuppliesRepository()
    private val _supplies = MutableStateFlow<List<SuppliesItem>>(emptyList())
    val supplies: StateFlow<List<SuppliesItem>> = _supplies
    private val _feedTypes = MutableStateFlow<List<FeedTypeDto>>(emptyList())
    val feedTypes: StateFlow<List<FeedTypeDto>> = _feedTypes

    init {
        loadFeedTypes()
    }

    fun loadSupplies(params: Map<String, Any?> = emptyMap()) {
        viewModelScope.launch {
            try {
                _supplies.value = repository.getSupplies(
                    feedTypeId = params["feed_type_id"] as? Int,
                    orderDateStart = params["order_date_start"] as? String,
                    orderDateEnd = params["order_date_end"] as? String,
                    quantityMin = params["quantity_min"] as? Float,
                    quantityMax = params["quantity_max"] as? Float,
                    priceMin = params["price_min"] as? Float,
                    priceMax = params["price_max"] as? Float,
                    deliveryDateStart = params["delivery_date_start"] as? String,
                    deliveryDateEnd = params["delivery_date_end"] as? String,
                    orderBy = params["order_by"] as? String,
                    orderDir = params["order_dir"] as? String
                )
            } catch (e: Exception) {
                _supplies.value = emptyList()
            }
        }
    }

    private fun loadFeedTypes() {
        viewModelScope.launch {
            try {
                _feedTypes.value = com.example.myzoo.data.remote.ApiModule.zooApi.getFeedTypes().data
            } catch (_: Exception) {}
        }
    }
} 
 
 
 