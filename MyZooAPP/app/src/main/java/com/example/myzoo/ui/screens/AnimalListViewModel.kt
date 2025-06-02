package com.example.myzoo.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myzoo.data.remote.AnimalMenuItem
import com.example.myzoo.data.repository.AnimalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.SpeciesDto
import com.example.myzoo.data.remote.EnclosureDto
import com.example.myzoo.data.remote.FeedTypeDto
import com.example.myzoo.data.remote.ApiModule
import kotlinx.coroutines.Dispatchers
import com.example.myzoo.data.remote.DiseaseDto
import com.example.myzoo.data.remote.VaccineDto
import android.util.Log

class AnimalListViewModel : ViewModel() {
    private val repository = AnimalRepository()
    private val _animals = MutableStateFlow<List<AnimalMenuItem>>(emptyList())
    val animals: StateFlow<List<AnimalMenuItem>> = _animals
    var currentQuery: AnimalQueryType = AnimalQueryType.ALL
        private set
    private val _species = MutableStateFlow<List<SpeciesDto>>(emptyList())
    val species: StateFlow<List<SpeciesDto>> = _species
    private val _enclosures = MutableStateFlow<List<EnclosureDto>>(emptyList())
    val enclosures: StateFlow<List<EnclosureDto>> = _enclosures
    private val _feedTypes = MutableStateFlow<List<FeedTypeDto>>(emptyList())
    val feedTypes: StateFlow<List<FeedTypeDto>> = _feedTypes
    private val _diseases = MutableStateFlow<List<DiseaseDto>>(emptyList())
    val diseases: StateFlow<List<DiseaseDto>> = _diseases
    private val _vaccines = MutableStateFlow<List<VaccineDto>>(emptyList())
    val vaccines: StateFlow<List<VaccineDto>> = _vaccines
    private val _animalsAll = MutableStateFlow<List<com.example.myzoo.data.remote.AnimalDto>>(emptyList())
    val animalsAll: StateFlow<List<com.example.myzoo.data.remote.AnimalDto>> = _animalsAll
    private val _animalsAllMenu = MutableStateFlow<List<AnimalMenuItem>>(emptyList())
    val animalsAllMenu: StateFlow<List<AnimalMenuItem>> = _animalsAllMenu
    private val _selectedAnimalDetails = MutableStateFlow<AnimalMenuItem?>(null)
    val selectedAnimalDetails: StateFlow<AnimalMenuItem?> = _selectedAnimalDetails

    init {
        loadDictionaries()
        loadDiseaseAndVaccineDictionaries()
        loadAllAnimals()
    }

    fun loadAnimals(queryType: AnimalQueryType = currentQuery, params: Map<String, Any?> = emptyMap()) {
        currentQuery = queryType
        viewModelScope.launch {
            try {
                val response = when (queryType) {
                    AnimalQueryType.ALL -> repository.getAnimalsQuery4(
                        speciesId = params["species_id"] as? Int,
                        enclosureId = params["enclosure_id"] as? Int,
                        gender = params["gender"] as? String,
                        ageMin = params["age_min"] as? Int,
                        ageMax = params["age_max"] as? Int,
                        weightMin = params["weight_min"] as? Float,
                        weightMax = params["weight_max"] as? Float,
                        heightMin = params["height_min"] as? Float,
                        heightMax = params["height_max"] as? Float,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.NEED_WARM -> repository.getAnimalsQuery5(
                        speciesId = params["species_id"] as? Int,
                        ageMin = params["age_min"] as? Int,
                        ageMax = params["age_max"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.BY_VACCINE_DISEASE -> repository.getAnimalsQuery6(
                        vaccineId = params["vaccine_id"] as? Int,
                        diseaseId = params["disease_id"] as? Int,
                        speciesId = params["species_id"] as? Int,
                        yearsInZooMin = params["years_in_zoo_min"] as? Int,
                        yearsInZooMax = params["years_in_zoo_max"] as? Int,
                        ageMin = params["age_min"] as? Int,
                        ageMax = params["age_max"] as? Int,
                        gender = params["gender"] as? String,
                        offspringMin = params["offspring_min"] as? Int,
                        offspringMax = params["offspring_max"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.COMPATIBLE -> repository.getAnimalsQuery7(
                        needWarm = params["need_warm"] as? String,
                        compatibleWithSpeciesId = params["compatible_with_species_id"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.BY_FEED -> repository.getAnimalsQuery10(
                        speciesId = params["species_id"] as? Int,
                        feedTypeId = params["feed_type_id"] as? Int,
                        season = params["season"] as? String,
                        ageGroup = params["age_group"] as? String,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.FULL_INFO -> repository.getAnimalsQuery11(
                        speciesId = params["species_id"] as? Int,
                        animalId = params["animal_id"] as? Int,
                        enclosureId = params["enclosure_id"] as? Int,
                        vaccineId = params["vaccine_id"] as? Int,
                        diseaseId = params["disease_id"] as? Int,
                        gender = params["gender"] as? String,
                        ageMin = params["age_min"] as? Int,
                        ageMax = params["age_max"] as? Int,
                        weightMin = params["weight_min"] as? Float,
                        weightMax = params["weight_max"] as? Float,
                        heightMin = params["height_min"] as? Float,
                        heightMax = params["height_max"] as? Float,
                        yearsInZooMin = params["years_in_zoo_min"] as? Int,
                        yearsInZooMax = params["years_in_zoo_max"] as? Int,
                        offspringMin = params["offspring_min"] as? Int,
                        offspringMax = params["offspring_max"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                    AnimalQueryType.EXPECT_OFFSPRING -> repository.getAnimalsQuery12(
                        speciesId = params["species_id"] as? Int,
                        orderBy = params["order_by"] as? String,
                        orderDir = params["order_dir"] as? String
                    )
                }
                _animals.value = response.data
            } catch (e: Exception) {
                _animals.value = emptyList()
            }
        }
    }

    fun loadAnimalDetails(animalId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getAnimalsQuery11(animalId = animalId)
                val animal = response.data.firstOrNull()
                Log.d("AnimalDetails", "Запрошен id=$animalId, получен id=${animal?.id}, name=${animal?.name}")
                _selectedAnimalDetails.value = animal
            } catch (e: Exception) {
                Log.e("AnimalDetails", "Ошибка загрузки деталей: ${e.message}")
                _selectedAnimalDetails.value = null
            }
        }
    }

    suspend fun getAnimalDtoById(id: Int): com.example.myzoo.data.remote.AnimalDto? {
        return repository.getAnimals().find { it.id == id }
    }

    fun loadDictionaries() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _species.value = ApiModule.zooApi.getSpecies().data
            } catch (_: Exception) {}
            try {
                _enclosures.value = ApiModule.zooApi.getEnclosures().data
            } catch (_: Exception) {}
            try {
                _feedTypes.value = ApiModule.zooApi.getFeedTypes().data
            } catch (_: Exception) {}
        }
    }

    fun loadDiseaseAndVaccineDictionaries() {
        viewModelScope.launch(Dispatchers.IO) {
            try { _diseases.value = repository.getDiseases() } catch (_: Exception) {}
            try { _vaccines.value = repository.getVaccines() } catch (_: Exception) {}
        }
    }

    fun loadAllAnimals() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val all = repository.getAnimals()
                _animalsAll.value = all
                _animalsAllMenu.value = all.map {
                    AnimalMenuItem(
                        id = it.id,
                        name = it.name,
                        gender = null,
                        birth_date = null,
                        age = null,
                        species = null,
                        enclosure = null,
                        feed_type = null,
                        season = null,
                        age_group = null,
                        total_animals = null,
                        weight = null,
                        height = null,
                        arrival_date = null,
                        years_in_zoo = null,
                        offspring_count = null,
                        vaccinations = null,
                        diseases = null
                    )
                }
            } catch (_: Exception) {}
        }
    }

    fun clearSelectedAnimalDetails() {
        _selectedAnimalDetails.value = null
    }
} 
 
 
 
 
 
 
 
 
 
 
 
 