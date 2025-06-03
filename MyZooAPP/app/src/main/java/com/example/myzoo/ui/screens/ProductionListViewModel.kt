package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.ProductionItem
import com.example.myzoo.data.repository.ProductionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.FeedTypeDto
import com.example.myzoo.data.remote.ApiModule

class ProductionListViewModel : ViewModel() {
    private val repository = ProductionRepository()
    private val _productions = MutableStateFlow<List<ProductionItem>>(emptyList())
    val productions: StateFlow<List<ProductionItem>> = _productions
    private val _feedTypes = MutableStateFlow<List<FeedTypeDto>>(emptyList())
    val feedTypes: StateFlow<List<FeedTypeDto>> = _feedTypes

    init {
        loadFeedTypes()
    }

    fun loadProductions(params: Map<String, Any?> = emptyMap()) {
        viewModelScope.launch {
            try {
                _productions.value = repository.getProductionsQuery9(
                    feedTypeId = params["feed_type_id"] as? Int,
                    orderBy = params["order_by"] as? String,
                    orderDir = params["order_dir"] as? String
                )
            } catch (e: Exception) {
                _productions.value = emptyList()
            }
        }
    }

    private fun loadFeedTypes() {
        viewModelScope.launch {
            try {
                _feedTypes.value = ApiModule.zooApi.getFeedTypes().data
            } catch (_: Exception) {}
        }
    }
} 