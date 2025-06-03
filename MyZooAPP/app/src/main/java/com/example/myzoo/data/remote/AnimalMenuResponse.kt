package com.example.myzoo.data.remote

data class AnimalMenuResponse(
    val data: List<AnimalMenuItem>
)

data class AnimalMenuItem(
    val id: Int,
    val name: String,
    val gender: String?,
    val birth_date: String?,
    val age: Int?,
    val species: String?,
    val enclosure: String?,
    val feed_type: String?,
    val season: String?,
    val age_group: String?,
    val total_animals: Int?,
    val weight: Float?,
    val height: Float?,
    val arrival_date: String?,
    val years_in_zoo: Int?,
    val offspring_count: Int?,
    val vaccinations: String?,
    val diseases: String?
)

data class SpeciesListResponse(val data: List<SpeciesDto>)
data class SpeciesDto(val id: Int, val type_name: String)

data class EnclosureListResponse(val data: List<EnclosureDto>)
data class EnclosureDto(val id: Int, val name: String)

data class FeedTypeListResponse(val data: List<FeedTypeDto>)
data class FeedTypeDto(val id: Int, val name: String)

data class ExchangeItem(
    val partner_zoo: String,
    val exchange_count: Int,
    val total_zoos: Int
)
data class ExchangeResponse(
    val data: List<ExchangeItem>
) 
 
 
 
 
 
 
 
 
 
 
 
 