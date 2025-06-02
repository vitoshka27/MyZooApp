package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.AnimalMenuItem
import com.example.myzoo.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalMenuViewModel : ViewModel() {
    private val repository = AnimalRepository()
    private val _animalMenu = MutableStateFlow<List<AnimalMenuItem>>(emptyList())
    val animalMenu: StateFlow<List<AnimalMenuItem>> = _animalMenu

    fun loadAnimalMenu(
        speciesId: Int? = null,
        feedTypeId: Int? = null,
        season: String? = null,
        ageGroup: String? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) {
        viewModelScope.launch {
            try {
                val response = repository.getAnimalsQuery10(speciesId, feedTypeId, season, ageGroup, orderBy, orderDir)
                _animalMenu.value = response.data
            } catch (e: Exception) {
                _animalMenu.value = emptyList() // Можно добавить обработку ошибок
            }
        }
    }
} 
 
 
 
 
 
 
 
 
 
 
 
 