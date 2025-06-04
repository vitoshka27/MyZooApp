package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.SuppliesItem
import com.example.myzoo.data.repository.SuppliesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.FeedItemDto
import com.example.myzoo.data.remote.FeedOrderItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class SuppliesViewModel : ViewModel() {
    private val repository = SuppliesRepository()
    private val _supplies = MutableStateFlow<List<SuppliesItem>>(emptyList())
    val supplies: StateFlow<List<SuppliesItem>> = _supplies
    private val _feedItems = MutableStateFlow<List<FeedItemDto>>(emptyList())
    val feedItems: StateFlow<List<FeedItemDto>> = _feedItems
    private val _feedOrders = mutableStateOf<List<FeedOrderItem>>(emptyList())
    val feedOrders: State<List<FeedOrderItem>> = _feedOrders

    init {
        loadFeedItems()
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

    private fun loadFeedItems() {
        viewModelScope.launch {
            try {
                _feedItems.value = com.example.myzoo.data.remote.ApiModule.zooApi.getFeedItems().data
            } catch (_: Exception) {}
        }
    }

    fun loadFeedOrders() {
        viewModelScope.launch {
            _feedOrders.value = repository.getFeedOrders()
        }
    }
} 
 
 
 