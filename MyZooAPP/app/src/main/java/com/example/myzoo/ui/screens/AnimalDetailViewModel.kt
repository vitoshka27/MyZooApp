package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.AnimalDto
import com.example.myzoo.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AnimalDetailViewModel : ViewModel() {
    private val repository = AnimalRepository()
    private val _animal = MutableStateFlow<AnimalDto?>(null)
    val animal: StateFlow<AnimalDto?> = _animal

    fun loadAnimal(id: Int) {
        viewModelScope.launch {
            try {
                val animals = repository.getAnimals()
                _animal.value = animals.find { it.id == id }
            } catch (e: Exception) {
                _animal.value = null
            }
        }
    }
} 
 
 
 
 
 
 
 
 
 
 
 
 