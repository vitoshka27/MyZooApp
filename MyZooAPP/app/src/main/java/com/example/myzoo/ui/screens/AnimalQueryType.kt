package com.example.myzoo.ui.screens

enum class AnimalQueryType(val displayName: String) {
    ALL("Все животные"),
    NEED_WARM("Нуждающиеся в тепле"),
    BY_VACCINE_DISEASE("По прививкам/болезням/потомству"),
    COMPATIBLE("Совместимость"),
    BY_FEED("По корму/сезону"),
    FULL_INFO("Полная информация"),
    EXPECT_OFFSPRING("Возможно потомство")
}

enum class StaffSortField(val apiName: String, val displayName: String) {
    LAST_NAME("last_name", "Фамилия"),
    FIRST_NAME("first_name", "Имя"),
    AGE("age", "Возраст"),
    YEARS_WORKED("years_worked", "Стаж"),
    SALARY("salary", "Зарплата")
}

fun getStaffSortFieldsForQuery(query: StaffQueryType): List<StaffSortField> = when (query) {
    StaffQueryType.GENERAL -> listOf(
        StaffSortField.FIRST_NAME, StaffSortField.LAST_NAME, StaffSortField.AGE, StaffSortField.YEARS_WORKED, StaffSortField.SALARY
    )
    StaffQueryType.CARETAKERS -> listOf(
        StaffSortField.FIRST_NAME, StaffSortField.LAST_NAME, StaffSortField.YEARS_WORKED
    )
}

fun getStaffFieldDisplayName(field: String): String = when (field) {
    "last_name" -> "Фамилия"
    "first_name" -> "Имя"
    "middle_name" -> "Отчество"
    "gender" -> "Пол"
    "birth_date" -> "Дата рождения"
    "age" -> "Возраст"
    "hire_date" -> "Дата найма"
    "years_worked" -> "Стаж"
    "salary" -> "Зарплата"
    "category" -> "Категория"
    "animal_name" -> "Животное"
    "start_date" -> "Начало ухода"
    "end_date" -> "Конец ухода"
    else -> field
} 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 