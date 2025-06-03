package com.example.myzoo.data.repository

import com.example.myzoo.data.remote.AnimalDto
import com.example.myzoo.data.remote.AnimalMenuResponse
import com.example.myzoo.data.remote.ApiModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AnimalRepository {
    private val api = ApiModule.zooApi

    suspend fun getAnimals(): List<AnimalDto> = withContext(Dispatchers.IO) {
        api.getAnimals().data
    }

    suspend fun getAnimalsQuery4(
        speciesId: Int? = null,
        enclosureId: Int? = null,
        gender: String? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        weightMin: Float? = null,
        weightMax: Float? = null,
        heightMin: Float? = null,
        heightMax: Float? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery4(speciesId, enclosureId, gender, ageMin, ageMax, weightMin, weightMax, heightMin, heightMax, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery5(
        speciesId: Int? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery5(speciesId, ageMin, ageMax, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery6(
        vaccineId: Int? = null,
        diseaseId: Int? = null,
        speciesId: Int? = null,
        yearsInZooMin: Int? = null,
        yearsInZooMax: Int? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        gender: String? = null,
        offspringMin: Int? = null,
        offspringMax: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery6(vaccineId, diseaseId, speciesId, yearsInZooMin, yearsInZooMax, ageMin, ageMax, gender, offspringMin, offspringMax, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery7(
        needWarm: String? = null,
        compatibleWithSpeciesId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery7(needWarm, compatibleWithSpeciesId, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery10(
        speciesId: Int? = null,
        feedTypeId: Int? = null,
        season: String? = null,
        ageGroup: String? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery10(speciesId, feedTypeId, season, ageGroup, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery11(
        speciesId: Int? = null,
        animalId: Int? = null,
        enclosureId: Int? = null,
        gender: String? = null,
        vaccineId: Int? = null,
        diseaseId: Int? = null,
        ageMin: Int? = null,
        ageMax: Int? = null,
        yearsInZooMin: Int? = null,
        yearsInZooMax: Int? = null,
        offspringMin: Int? = null,
        offspringMax: Int? = null,
        weightMin: Float? = null,
        weightMax: Float? = null,
        heightMin: Float? = null,
        heightMax: Float? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery11(speciesId, animalId, enclosureId, gender, vaccineId, diseaseId, ageMin, ageMax, yearsInZooMin, yearsInZooMax, offspringMin, offspringMax, weightMin, weightMax, heightMin, heightMax, orderBy, orderDir)
    }

    suspend fun getAnimalsQuery12(
        speciesId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getAnimalsQuery12(speciesId, orderBy, orderDir)
    }

    suspend fun getDiseases() = withContext(Dispatchers.IO) { api.getDiseases().data }
    suspend fun getVaccines() = withContext(Dispatchers.IO) { api.getVaccines().data }

    suspend fun getExchangeQuery13(
        speciesId: Int? = null,
        orderBy: String? = null,
        orderDir: String? = null
    ) = withContext(Dispatchers.IO) {
        api.getExchangeQuery13(speciesId, orderBy, orderDir)
    }
} 
 
 
 
 
 
 
 
 
 
 
 