package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.StaffMenuItem
import com.example.myzoo.data.remote.AnimalShortDto
import com.example.myzoo.data.remote.ApiModule
import com.example.myzoo.data.remote.StaffCategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

enum class StaffQueryType(val displayName: String) {
    GENERAL("Все сотрудники"),
    CARETAKERS("Ответственные за животное")
}

class StaffListViewModel : ViewModel() {
    private val _staff = MutableStateFlow<List<StaffMenuItem>>(emptyList())
    val staff: StateFlow<List<StaffMenuItem>> = _staff
    private val _animals = MutableStateFlow<List<AnimalShortDto>>(emptyList())
    val animals: StateFlow<List<AnimalShortDto>> = _animals
    var currentQuery: StaffQueryType = StaffQueryType.GENERAL
        private set
    private val _selectedStaffDetails = MutableStateFlow<StaffMenuItem?>(null)
    val selectedStaffDetails: StateFlow<StaffMenuItem?> = _selectedStaffDetails
    private val _staffCategories = MutableStateFlow<List<StaffCategoryDto>>(emptyList())
    val staffCategories: StateFlow<List<StaffCategoryDto>> = _staffCategories

    init {
        loadAnimals()
        loadStaffCategories()
    }

    fun loadStaff(
        queryType: StaffQueryType = currentQuery,
        params: Map<String, Any?> = emptyMap()
    ) {
        currentQuery = queryType
        viewModelScope.launch {
            try {
                val result = when (queryType) {
                    StaffQueryType.GENERAL -> ApiModule.getStaffQuery1(
                        categoryId = params["category_id"] as? Int,
                        gender = params["gender"] as? String,
                        salaryMin = params["salary_min"] as? Float,
                        salaryMax = params["salary_max"] as? Float,
                        yearsWorkedMin = params["years_worked_min"] as? Int,
                        yearsWorkedMax = params["years_worked_max"] as? Int,
                        ageMin = params["age_min"] as? Int,
                        ageMax = params["age_max"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    StaffQueryType.CARETAKERS -> {
                        val animalId = params["animal_id"] as? Int
                        ApiModule.getStaffQuery2(
                            animalId = animalId,
                            startDate = params["start_date"] as? String,
                            endDate = params["end_date"] as? String,
                            gender = params["gender"] as? String,
                            categoryId = params["category_id"] as? Int,
                            orderBy = params["order_by"] as? String,
                            orderDir = params["order_dir"] as? String
                        )
                    }
                }
                _staff.value = result
            } catch (e: Exception) {
                _staff.value = emptyList()
            }
        }
    }

    fun loadAnimals() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _animals.value = ApiModule.getAnimalsShort()
            } catch (_: Exception) {}
        }
    }

    fun loadStaffDetails(staff: StaffMenuItem) {
        _selectedStaffDetails.value = staff
    }

    fun clearSelectedStaffDetails() {
        _selectedStaffDetails.value = null
    }

    fun loadStaffCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _staffCategories.value = ApiModule.getStaffCategories()
            } catch (_: Exception) {}
        }
    }
} 