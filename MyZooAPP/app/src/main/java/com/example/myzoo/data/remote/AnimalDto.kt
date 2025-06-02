package com.example.myzoo.data.remote

data class AnimalDto(
    val id: Int,
    val name: String,
    val species_id: Int,
    val gender: String?,
    val birth_date: String?,
    val arrival_date: String?,
    val enclosure_id: Int?,
    val parent1_id: Int?,
    val parent2_id: Int?
)

data class DiseaseDto(val id: Int, val name: String)
data class VaccineDto(val id: Int, val name: String)
data class DiseaseListResponse(val data: List<DiseaseDto>)
data class VaccineListResponse(val data: List<VaccineDto>)
data class AnimalListResponse(val data: List<AnimalDto>) 
 
 
 
 
 
 
 
 
 
 
 