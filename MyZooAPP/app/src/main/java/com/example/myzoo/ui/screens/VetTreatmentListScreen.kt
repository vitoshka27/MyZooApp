package com.example.myzoo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.ApiModule
import com.example.myzoo.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import android.util.Log

// --- Data class ---
data class AnimalVaccinationItem(
    val id: Int,
    val animal_id: Int,
    val vaccine_id: Int,
    val vaccination_date: String?,
    val next_vaccination_date: String?
)

data class AnimalDiseaseItem(
    val id: Int,
    val animal_id: Int,
    val veterinarian_id: Int?,
    val disease_id: Int,
    val diagnosed_date: String?,
    val recovery_date: String?,
    val notes: String?
)

// --- ViewModel ---
class VetTreatmentListViewModel : ViewModel() {
    private val _vaccinations = MutableStateFlow<List<AnimalVaccinationItem>>(emptyList())
    val vaccinations: StateFlow<List<AnimalVaccinationItem>> = _vaccinations
    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    private val _diseases = MutableStateFlow<List<AnimalDiseaseItem>>(emptyList())
    val diseases: StateFlow<List<AnimalDiseaseItem>> = _diseases
    private val _diseasesTotal = MutableStateFlow(0)
    val diseasesTotal: StateFlow<Int> = _diseasesTotal

    // Фильтры
    var filterAnimalId by mutableStateOf<String?>(null)
    var filterVaccineId by mutableStateOf<String?>(null)
    var filterDateFrom by mutableStateOf<String?>(null)
    var filterDateTo by mutableStateOf<String?>(null)

    fun loadVaccinations() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiModule.getAdminTable("animal_vaccinations")
                val items = response.data.mapNotNull { row ->
                    try {
                        AnimalVaccinationItem(
                            id = (row["id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            animal_id = (row["animal_id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            vaccine_id = (row["vaccine_id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            vaccination_date = row["vaccination_date"]?.toString(),
                            next_vaccination_date = row["next_vaccination_date"]?.toString()
                        )
                    } catch (_: Exception) { null }
                }
                val filterAnimalIdVal = filterAnimalId
                val filterVaccineIdVal = filterVaccineId
                val filterDateFromVal = filterDateFrom
                val filterDateToVal = filterDateTo
                val filtered = items.filter { item ->
                    (filterAnimalIdVal.isNullOrBlank() || item.animal_id == filterAnimalIdVal?.toIntOrNull()) &&
                    (filterVaccineIdVal.isNullOrBlank() || item.vaccine_id == filterVaccineIdVal?.toIntOrNull()) &&
                    (filterDateFromVal.isNullOrBlank() || (item.vaccination_date != null && item.vaccination_date >= filterDateFromVal)) &&
                    (filterDateToVal.isNullOrBlank() || (item.vaccination_date != null && item.vaccination_date <= filterDateToVal))
                }
                _vaccinations.value = filtered
                _total.value = filtered.size
            } catch (e: Exception) {
                _vaccinations.value = emptyList()
                _total.value = 0
            }
        }
    }

    fun loadDiseases() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiModule.getAdminTable("animal_diseases")
                val items = response.data.mapNotNull { row ->
                    try {
                        AnimalDiseaseItem(
                            id = (row["id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            animal_id = (row["animal_id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            veterinarian_id = (row["veterinarian_id"] as? Number)?.toInt(),
                            disease_id = (row["disease_id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            diagnosed_date = row["diagnosed_date"]?.toString(),
                            recovery_date = row["recovery_date"]?.toString(),
                            notes = row["notes"]?.toString()
                        )
                    } catch (_: Exception) { null }
                }
                _diseases.value = items
                _diseasesTotal.value = items.size
            } catch (e: Exception) {
                _diseases.value = emptyList()
                _diseasesTotal.value = 0
            }
        }
    }
}

// --- Экран ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VetTreatmentListScreen(viewModel: VetTreatmentListViewModel = viewModel()) {
    val vaccinations by viewModel.vaccinations.collectAsState()
    val total by viewModel.total.collectAsState()
    val diseases by viewModel.diseases.collectAsState()
    val diseasesTotal by viewModel.diseasesTotal.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var showDiseasesFilterSheet by remember { mutableStateOf(false) }
    var sortField by remember { mutableStateOf("vaccination_date") }
    var sortDir by remember { mutableStateOf("desc") }
    var showSortPopup by remember { mutableStateOf(false) }
    var sortButtonOffset by remember { mutableStateOf(Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }
    var sortButtonWidth by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf(0) } // 0 - Лечение, 1 - Больные
    var burgerMenuPopup by remember { mutableStateOf(false) }
    var burgerButtonOffset by remember { mutableStateOf(Offset.Zero) }
    var burgerButtonHeight by remember { mutableStateOf(0) }
    var burgerButtonWidth by remember { mutableStateOf(0) }

    // --- Получаем справочники ---
    val animalListViewModel: AnimalListViewModel = viewModel()
    val animalsAll by animalListViewModel.animalsAll.collectAsState()
    val vaccines by animalListViewModel.vaccines.collectAsState()
    val diseasesDict by animalListViewModel.diseases.collectAsState()
    val animalIdToName = remember(animalsAll) { animalsAll.associate { it.id to it.name } }
    val vaccineIdToName = remember(vaccines) { vaccines.associate { it.id to it.name } }
    val diseaseIdToName = remember(diseasesDict) { diseasesDict.associate { it.id to it.name } }
    // --- Справочник ветеринаров ---
    val veterinariansState = remember { mutableStateOf(emptyList<com.example.myzoo.data.remote.StaffMenuItem>()) }
    val veterinarianIdToStaff = remember(veterinariansState.value) {
        veterinariansState.value.filter { it.id != null }.associateBy { it.id!! }
    }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        try {
            val vets = com.example.myzoo.data.remote.ApiModule.getStaffQuery1(categoryId = 1)
            veterinariansState.value = vets
            Log.d("VetScreen", "Загружено ветеринаров через getStaffQuery1: ${vets.map { v -> v.id to v.last_name }}")
        } catch (e: Exception) {
            Log.e("VetScreen", "Ошибка загрузки ветеринаров через getStaffQuery1: $e")
        }
    }
    // --- Динамическая подгрузка ФИО по id, если не найдено ---
    val missingVetCache = remember { mutableStateMapOf<Int, com.example.myzoo.data.remote.StaffMenuItem?>() }
    fun fetchVetById(id: Int) {
        if (missingVetCache.containsKey(id)) return
        coroutineScope.launch {
            try {
                val result = com.example.myzoo.data.remote.ApiModule.getStaffQuery1(categoryId = 1).firstOrNull { it.id == id }
                missingVetCache[id] = result
            } catch (_: Exception) {
                missingVetCache[id] = null
            }
        }
    }
    // --- Автоматическая загрузка данных при первом входе ---
    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) viewModel.loadVaccinations() else viewModel.loadDiseases()
    }

    // --- Сортировка ---
    val sortedVaccinations = remember(vaccinations, sortField, sortDir) {
        val base = vaccinations.sortedWith(compareBy<AnimalVaccinationItem> {
            when (sortField) {
                "animal_id" -> it.animal_id
                "vaccine_id" -> it.vaccine_id
                "vaccination_date" -> it.vaccination_date ?: ""
                "next_vaccination_date" -> it.next_vaccination_date ?: ""
                else -> it.vaccination_date ?: ""
            }
        })
        if (sortDir == "desc") base.reversed() else base
    }

    // --- Состояния фильтров для 'Больные' ---
    var filterSpeciesId by remember { mutableStateOf<Int?>(null) }
    var filterDiseaseId by remember { mutableStateOf<Int?>(null) }
    var filterSpeciesIdTmp by remember { mutableStateOf<Int?>(filterSpeciesId) }
    var filterDiseaseIdTmp by remember { mutableStateOf<Int?>(filterDiseaseId) }

    // --- Сортировка для Больные ---
    var sortDiseasesField by remember { mutableStateOf("diagnosed_date") }
    var sortDiseasesDir by remember { mutableStateOf("desc") }
    var showDiseasesSortPopup by remember { mutableStateOf(false) }
    var diseasesSortButtonOffset by remember { mutableStateOf(Offset.Zero) }
    var diseasesSortButtonHeight by remember { mutableStateOf(0) }
    var diseasesSortButtonWidth by remember { mutableStateOf(0) }
    // --- Фильтрация списка болезней ---
    val filteredDiseases = remember(diseases, filterSpeciesId, filterDiseaseId, animalsAll) {
        diseases.filter { item ->
            (filterSpeciesId == null || animalsAll.find { it.id == item.animal_id }?.species_id == filterSpeciesId) &&
            (filterDiseaseId == null || item.disease_id == filterDiseaseId)
        }
    }
    // --- Сортировка списка болезней ---
    val sortedDiseases = remember(filteredDiseases, sortDiseasesField, sortDiseasesDir) {
        val base = filteredDiseases.sortedWith(compareBy<AnimalDiseaseItem> {
            when (sortDiseasesField) {
                "animal_id" -> it.animal_id
                "disease_id" -> it.disease_id
                "diagnosed_date" -> it.diagnosed_date ?: ""
                "recovery_date" -> it.recovery_date ?: ""
                else -> it.diagnosed_date ?: ""
            }
        })
        if (sortDiseasesDir == "desc") base.reversed() else base
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(TropicBackground)
    ) {
        Column(Modifier.fillMaxSize()) {
            // AppBar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        Brush.horizontalGradient(listOf(TropicGreen, TropicTurquoise)),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    ),
            ) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // --- Бургер-меню ---
                    Box {
                        IconButton(
                            onClick = { burgerMenuPopup = true },
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .onGloballyPositioned { coords ->
                                    burgerButtonOffset = coords.positionInParent()
                                    burgerButtonHeight = coords.size.height
                                    burgerButtonWidth = coords.size.width
                                }
                        ) {
                            Icon(Icons.Filled.Menu, contentDescription = "Меню", tint = TropicOnPrimary)
                        }
                        if (burgerMenuPopup) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = IntOffset(burgerButtonOffset.x.toInt(), (burgerButtonOffset.y + burgerButtonHeight).toInt()),
                                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                                onDismissRequest = { burgerMenuPopup = false }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))
                                            ),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(24.dp),
                                        color = Color.Transparent,
                                        tonalElevation = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            listOf("Больные", "Лечение").forEachIndexed { idx, label ->
                                                val tabIndex = if (label == "Лечение") 0 else 1
                                                val isSelected = selectedTab == tabIndex
                                                Box(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                                        .clickable {
                                                            selectedTab = tabIndex
                                                            burgerMenuPopup = false
                                                        }
                                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                                ) {
                                                    Text(
                                                        label,
                                                        color = if (isSelected) TropicTurquoise else TropicOnBackground,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Text(
                        text = "Лечение животных",
                        style = MaterialTheme.typography.titleLarge,
                        color = TropicOnPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            // --- Total/Query ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Всего записей: ${if (selectedTab == 0) total else diseasesTotal}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    text = if (selectedTab == 0) "Лечение" else "Больные",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TropicTurquoise
                )
            }
            // --- Фильтры/сортировка ---
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = TropicSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = {
                            if (selectedTab == 0) showFilterSheet = true else showDiseasesFilterSheet = true
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Фильтры", tint = TropicGreen, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Фильтры", color = TropicGreen)
                    }
                    Spacer(Modifier.weight(1f))
                    if (selectedTab == 0) {
                        Button(
                            onClick = { showSortPopup = true },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .onGloballyPositioned { coords ->
                                    sortButtonOffset = coords.positionInParent()
                                    sortButtonHeight = coords.size.height
                                    sortButtonWidth = coords.size.width
                                }
                        ) {
                            Text(
                                when (sortField) {
                                    "animal_id" -> "Животное"
                                    "vaccine_id" -> "Вакцина"
                                    "vaccination_date" -> "Дата вакцинации"
                                    "next_vaccination_date" -> "Следующая вакцинация"
                                    else -> sortField
                                },
                                color = TropicTurquoise
                            )
                        }
                        IconButton(
                            onClick = { sortDir = if (sortDir == "asc") "desc" else "asc" },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, shape = RoundedCornerShape(50))
                        ) {
                            Icon(
                                if (sortDir == "asc") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = "Сменить направление сортировки",
                                tint = TropicTurquoise,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = { showDiseasesSortPopup = true },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            modifier = Modifier
                                .height(44.dp)
                                .onGloballyPositioned { coords ->
                                    diseasesSortButtonOffset = coords.positionInParent()
                                    diseasesSortButtonHeight = coords.size.height
                                    diseasesSortButtonWidth = coords.size.width
                                }
                        ) {
                            Text(
                                when (sortDiseasesField) {
                                    "animal_id" -> "Животное"
                                    "disease_id" -> "Болезнь"
                                    "diagnosed_date" -> "Дата диагноза"
                                    "recovery_date" -> "Выздоровление"
                                    else -> sortDiseasesField
                                },
                                color = TropicTurquoise
                            )
                        }
                        IconButton(
                            onClick = { sortDiseasesDir = if (sortDiseasesDir == "asc") "desc" else "asc" },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, shape = RoundedCornerShape(50))
                        ) {
                            Icon(
                                if (sortDiseasesDir == "asc") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = "Сменить направление сортировки",
                                tint = TropicTurquoise,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
                if (showSortPopup && selectedTab == 0) {
                    Popup(
                        alignment = Alignment.TopStart,
                        offset = IntOffset(sortButtonOffset.x.toInt(), (sortButtonOffset.y + sortButtonHeight).toInt()),
                        properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                        onDismissRequest = { showSortPopup = false }
                    ) {
                        val density = LocalDensity.current
                        val minWidth = with(density) { 160.dp.toPx() }
                        val maxWidth = with(density) { 240.dp.toPx() }
                        val popupWidthPx = sortButtonWidth.coerceIn(minWidth.toInt(), maxWidth.toInt())
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Transparent,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .width(with(density) { popupWidthPx.toDp() })
                                .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.Transparent,
                                tonalElevation = 8.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    val sortFields = listOf(
                                        "animal_id" to "Животное",
                                        "vaccine_id" to "Вакцина",
                                        "vaccination_date" to "Дата вакцинации",
                                        "next_vaccination_date" to "Следующая вакцинация"
                                    )
                                    sortFields.forEach { (field, label) ->
                                        val isSelected = sortField == field
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                                .clickable {
                                                    sortField = field
                                                    showSortPopup = false
                                                }
                                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                        ) {
                                            Text(
                                                label,
                                                color = if (isSelected) TropicTurquoise else TropicOnBackground,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // --- Список ---
            Box(Modifier.weight(1f)) {
                if (selectedTab == 0) {
                    LazyColumn(
                        Modifier.fillMaxSize().padding(top = 0.dp),
                        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                    ) {
                        if (sortedVaccinations.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Нет данных")
                                }
                            }
                        } else {
                            items(sortedVaccinations) { item ->
                                VaccinationCard(
                                    item = item,
                                    animalName = animalIdToName[item.animal_id],
                                    vaccineName = vaccineIdToName[item.vaccine_id]
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier.fillMaxSize().padding(top = 0.dp),
                        contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                    ) {
                        if (sortedDiseases.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Нет данных")
                                }
                            }
                        } else {
                            items(sortedDiseases) { item ->
                                var vet = item.veterinarian_id?.let { veterinarianIdToStaff[it] }
                                if (vet == null && item.veterinarian_id != null) {
                                    vet = missingVetCache[item.veterinarian_id]
                                    if (vet == null && !missingVetCache.containsKey(item.veterinarian_id)) {
                                        fetchVetById(item.veterinarian_id)
                                    }
                                }
                                if (vet == null) Log.w("DiseaseCard", "ВНИМАНИЕ: vetObj=null для veterinarian_id=${item.veterinarian_id}. Справочник id: ${veterinarianIdToStaff.keys}")
                                DiseaseCard(
                                    item = item,
                                    animalName = animalIdToName[item.animal_id],
                                    veterinarian = vet,
                                    diseaseName = diseaseIdToName[item.disease_id]
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
        // --- Фильтры ---
        if (showFilterSheet && selectedTab == 0) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFilterSheet = false
                    filterSpeciesIdTmp = filterSpeciesId
                    filterDiseaseIdTmp = filterDiseaseId
                },
                shape = RoundedCornerShape(0.dp),
                containerColor = Color(0xFFEFFAF3),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(
                            Brush.horizontalGradient(listOf(TropicGreen, TropicTurquoise)),
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 30.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                    )
                }
                Column(
                    Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    Text("Фильтры", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                    Spacer(Modifier.height(18.dp))
                    FilterRow(label = "Вид", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = animalListViewModel.species.collectAsState().value.map { it.id to it.type_name },
                            selected = filterSpeciesIdTmp,
                            onSelected = { filterSpeciesIdTmp = it },
                            width = 220.dp
                        )
                    })
                    FilterRow(label = "Болезнь", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = diseasesDict.map { it.id to it.name },
                            selected = filterDiseaseIdTmp,
                            onSelected = { filterDiseaseIdTmp = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            filterSpeciesId = null
                            filterDiseaseId = null
                            filterSpeciesIdTmp = null
                            filterDiseaseIdTmp = null
                            showFilterSheet = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = {
                                filterSpeciesId = filterSpeciesIdTmp
                                filterDiseaseId = filterDiseaseIdTmp
                                showFilterSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
        if (showDiseasesFilterSheet && selectedTab == 1) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDiseasesFilterSheet = false
                    filterSpeciesIdTmp = filterSpeciesId
                    filterDiseaseIdTmp = filterDiseaseId
                },
                shape = RoundedCornerShape(0.dp),
                containerColor = Color(0xFFEFFAF3),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .background(
                            Brush.horizontalGradient(listOf(TropicGreen, TropicTurquoise)),
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 30.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(36.dp)
                    )
                }
                Column(
                    Modifier
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    Text("Фильтры", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                    Spacer(Modifier.height(18.dp))
                    FilterRow(label = "Вид", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = animalListViewModel.species.collectAsState().value.map { it.id to it.type_name },
                            selected = filterSpeciesIdTmp,
                            onSelected = { filterSpeciesIdTmp = it },
                            width = 220.dp
                        )
                    })
                    FilterRow(label = "Болезнь", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = diseasesDict.map { it.id to it.name },
                            selected = filterDiseaseIdTmp,
                            onSelected = { filterDiseaseIdTmp = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            filterSpeciesId = null
                            filterDiseaseId = null
                            filterSpeciesIdTmp = null
                            filterDiseaseIdTmp = null
                            showDiseasesFilterSheet = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = {
                                filterSpeciesId = filterSpeciesIdTmp
                                filterDiseaseId = filterDiseaseIdTmp
                                showDiseasesFilterSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
        if (showDiseasesSortPopup && selectedTab == 1) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(diseasesSortButtonOffset.x.toInt(), (diseasesSortButtonOffset.y + diseasesSortButtonHeight).toInt()),
                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                onDismissRequest = { showDiseasesSortPopup = false }
            ) {
                val density = LocalDensity.current
                val minWidth = with(density) { 160.dp.toPx() }
                val maxWidth = with(density) { 240.dp.toPx() }
                val popupWidthPx = diseasesSortButtonWidth.coerceIn(minWidth.toInt(), maxWidth.toInt())
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .width(with(density) { popupWidthPx.toDp() })
                        .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Transparent,
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            val sortFields = listOf(
                                "animal_id" to "Животное",
                                "disease_id" to "Болезнь",
                                "diagnosed_date" to "Дата диагноза",
                                "recovery_date" to "Выздоровление"
                            )
                            sortFields.forEach { (field, label) ->
                                val isSelected = sortDiseasesField == field
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                        .clickable {
                                            sortDiseasesField = field
                                            showDiseasesSortPopup = false
                                        }
                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                ) {
                                    Text(
                                        label,
                                        color = if (isSelected) TropicTurquoise else TropicOnBackground,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Карточка вакцинации ---
@Composable
fun VaccinationCard(item: AnimalVaccinationItem, animalName: String?, vaccineName: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.White, Color(0xFFfefdff), Color(0xFFfbf6ff)),
                        startX = 0f,
                        endX = 600f
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("${animalName ?: item.animal_id}", fontWeight = FontWeight.Bold, color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Вакцина: ${vaccineName ?: item.vaccine_id}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Дата вакцинации: ${item.vaccination_date ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Следующая вакцинация: ${item.next_vaccination_date ?: "-"}", color = TropicGreen, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

// --- Карточка болезни ---
@Composable
fun DiseaseCard(item: AnimalDiseaseItem, animalName: String?, veterinarian: com.example.myzoo.data.remote.StaffMenuItem?, diseaseName: String?) {
    // Явно достаем все 3 значения и соединяем
    val lastName = veterinarian?.last_name ?: ""
    val firstName = veterinarian?.first_name ?: ""
    val middleName = veterinarian?.middle_name ?: ""
    val fio = (lastName + " " + firstName + " " + middleName).trim().replace(Regex(" +"), " ")
    Log.d("DiseaseCard", "veterinarian_id=${item.veterinarian_id}, fio=$fio, vetObj=$veterinarian")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.White, Color(0xFFfefdff), Color(0xFFfbf6ff)),
                        startX = 0f,
                        endX = 600f
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("${animalName ?: item.animal_id}", fontWeight = FontWeight.Bold, color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Ветеринар: ${if (fio.isNotBlank()) fio else "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Болезнь: ${diseaseName ?: item.disease_id}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Дата диагноза: ${item.diagnosed_date ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Выздоровление: ${item.recovery_date ?: "-"}", color = TropicGreen, style = MaterialTheme.typography.bodyLarge)
                Text("Заметки: ${item.notes ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
} 