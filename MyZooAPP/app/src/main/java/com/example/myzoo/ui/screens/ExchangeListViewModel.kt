package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.ExchangeItem
import com.example.myzoo.data.remote.SpeciesDto
import com.example.myzoo.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.myzoo.data.remote.ApiModule

class ExchangeListViewModel : ViewModel() {
    private val repository = AnimalRepository()
    private val _exchanges = MutableStateFlow<List<ExchangeItem>>(emptyList())
    val exchanges: StateFlow<List<ExchangeItem>> = _exchanges
    private val _species = MutableStateFlow<List<SpeciesDto>>(emptyList())
    val species: StateFlow<List<SpeciesDto>> = _species

    // UI state (private, чтобы не было конфликтов с сеттерами)
    private var filterSpecies: Int? = null
    private var sortField: String = "partner_zoo"
    private var sortDir: String = "asc"

    init {
        loadSpecies()
        loadExchanges()
    }

    fun loadSpecies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _species.value = ApiModule.zooApi.getSpecies().data
            } catch (_: Exception) {}
        }
    }

    fun loadExchanges(
        speciesId: Int? = filterSpecies,
        orderBy: String = sortField,
        orderDir: String = sortDir
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getExchangeQuery13(speciesId, orderBy, orderDir)
                _exchanges.value = response.data
            } catch (e: Exception) {
                _exchanges.value = emptyList()
            }
        }
    }

    fun setFilterSpecies(speciesId: Int?) {
        filterSpecies = speciesId
    }
    fun setSortField(field: String) {
        sortField = field
    }
    fun setSortDir(dir: String) {
        sortDir = dir
    }

    // Для доступа к текущим значениям из UI
    fun getFilterSpecies(): Int? = filterSpecies
    fun getSortField(): String = sortField
    fun getSortDir(): String = sortDir
} 