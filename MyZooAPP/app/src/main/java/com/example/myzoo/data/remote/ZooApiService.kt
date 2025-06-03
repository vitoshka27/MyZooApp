package com.example.myzoo.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import okhttp3.MultipartBody
import com.example.myzoo.data.remote.StaffMenuResponse
import com.example.myzoo.data.remote.AnimalShortListResponse

interface ZooApiService {
    @GET("api/animals")
    suspend fun getAnimals(): AnimalListResponse

    @GET("api/custom_queries/query4")
    suspend fun getAnimalsQuery4(
        @Query("species_id") speciesId: Int? = null,
        @Query("enclosure_id") enclosureId: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("weight_min") weightMin: Float? = null,
        @Query("weight_max") weightMax: Float? = null,
        @Query("height_min") heightMin: Float? = null,
        @Query("height_max") heightMax: Float? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query5")
    suspend fun getAnimalsQuery5(
        @Query("species_id") speciesId: Int? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query6")
    suspend fun getAnimalsQuery6(
        @Query("vaccine_id") vaccineId: Int? = null,
        @Query("disease_id") diseaseId: Int? = null,
        @Query("species_id") speciesId: Int? = null,
        @Query("years_in_zoo_min") yearsInZooMin: Int? = null,
        @Query("years_in_zoo_max") yearsInZooMax: Int? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("offspring_min") offspringMin: Int? = null,
        @Query("offspring_max") offspringMax: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query7")
    suspend fun getAnimalsQuery7(
        @Query("need_warm") needWarm: String? = null,
        @Query("compatible_with_species_id") compatibleWithSpeciesId: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query10")
    suspend fun getAnimalsQuery10(
        @Query("species_id") speciesId: Int? = null,
        @Query("feed_type_id") feedTypeId: Int? = null,
        @Query("season") season: String? = null,
        @Query("age_group") ageGroup: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query11")
    suspend fun getAnimalsQuery11(
        @Query("species_id") speciesId: Int? = null,
        @Query("animal_id") animalId: Int? = null,
        @Query("enclosure_id") enclosureId: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("vaccine_id") vaccineId: Int? = null,
        @Query("disease_id") diseaseId: Int? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("years_in_zoo_min") yearsInZooMin: Int? = null,
        @Query("years_in_zoo_max") yearsInZooMax: Int? = null,
        @Query("offspring_min") offspringMin: Int? = null,
        @Query("offspring_max") offspringMax: Int? = null,
        @Query("weight_min") weightMin: Float? = null,
        @Query("weight_max") weightMax: Float? = null,
        @Query("height_min") heightMin: Float? = null,
        @Query("height_max") heightMax: Float? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/custom_queries/query12")
    suspend fun getAnimalsQuery12(
        @Query("species_id") speciesId: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): AnimalMenuResponse

    @GET("api/species/")
    suspend fun getSpecies(): SpeciesListResponse

    @GET("api/enclosures/")
    suspend fun getEnclosures(): EnclosureListResponse

    @GET("api/feed_types/")
    suspend fun getFeedTypes(): FeedTypeListResponse

    @GET("api/diseases/")
    suspend fun getDiseases(): DiseaseListResponse

    @GET("api/vaccines/")
    suspend fun getVaccines(): VaccineListResponse

    @GET("api/auth/me")
    suspend fun getProfile(): UserProfileResponse

    @GET("api/category_attributes/")
    suspend fun getCategoryAttributes(): CategoryAttributeListResponse

    @GET("api/staff_attribute_values/")
    suspend fun getStaffAttributeValues(@Query("staff_id") staffId: Int): StaffAttributeValueListResponse

    @Multipart
    @POST("api/profile/avatar")
    fun uploadAvatar(
        @Part avatar: MultipartBody.Part,
        @Part avatar_original: MultipartBody.Part
    ): Call<UploadAvatarResponse>

    @GET("api/custom_queries/query1")
    suspend fun getStaffQuery1(
        @Query("category_id") categoryId: Int? = null,
        @Query("gender") gender: String? = null,
        @Query("salary_min") salaryMin: Float? = null,
        @Query("salary_max") salaryMax: Float? = null,
        @Query("years_worked_min") yearsWorkedMin: Int? = null,
        @Query("years_worked_max") yearsWorkedMax: Int? = null,
        @Query("age_min") ageMin: Int? = null,
        @Query("age_max") ageMax: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): StaffMenuResponse

    @GET("api/custom_queries/query2")
    suspend fun getStaffQuery2(
        @Query("animal_id") animalId: Int? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null,
        @Query("gender") gender: String? = null,
        @Query("category_id") categoryId: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): StaffMenuResponse

    @GET("api/animals")
    suspend fun getAnimalsShort(): AnimalShortListResponse

    @GET("api/staff_categories/")
    suspend fun getStaffCategories(): StaffCategoryListResponse

    @GET("api/custom_queries/query8")
    suspend fun getSuppliesQuery8(
        @Query("feed_type_id") feedTypeId: Int? = null,
        @Query("order_date_start") orderDateStart: String? = null,
        @Query("order_date_end") orderDateEnd: String? = null,
        @Query("quantity_min") quantityMin: Float? = null,
        @Query("quantity_max") quantityMax: Float? = null,
        @Query("price_min") priceMin: Float? = null,
        @Query("price_max") priceMax: Float? = null,
        @Query("delivery_date_start") deliveryDateStart: String? = null,
        @Query("delivery_date_end") deliveryDateEnd: String? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): SuppliesResponse

    @GET("api/custom_queries/query13")
    suspend fun getExchangeQuery13(
        @Query("species_id") speciesId: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): ExchangeResponse

    @GET("api/custom_queries/query9")
    suspend fun getProductionQuery9(
        @Query("feed_type_id") feedTypeId: Int? = null,
        @Query("order_by") orderBy: String? = null,
        @Query("order_dir") orderDir: String? = null
    ): ProductionResponse
}

data class StaffCategoryDto(
    val id: Int,
    val name: String
)

data class StaffCategoryListResponse(
    val data: List<StaffCategoryDto>
) 
 
 
 