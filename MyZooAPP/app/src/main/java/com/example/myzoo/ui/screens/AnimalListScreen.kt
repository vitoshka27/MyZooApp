package com.example.myzoo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.AnimalMenuItem
import com.example.myzoo.data.remote.SpeciesDto
import com.example.myzoo.data.remote.EnclosureDto
import com.example.myzoo.data.remote.FeedTypeDto
import com.example.myzoo.data.remote.DiseaseDto
import com.example.myzoo.data.remote.VaccineDto
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.imePadding
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.CoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicYellow
import com.example.myzoo.ui.theme.TropicOrange
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import kotlin.math.roundToInt
import com.example.myzoo.ui.screens.FilterRow
import com.example.myzoo.ui.screens.DropdownSelector

// --- UI config structures ---
data class FieldDescriptor(val key: String, val label: String)
data class FilterDescriptor(val key: String, val label: String, val type: FilterType)
data class SortDescriptor(val key: String, val label: String)
enum class FilterType { TEXT, NUMBER, DROPDOWN }
data class AnimalQueryUiConfig(
    val fields: List<FieldDescriptor>,
    val filters: List<FilterDescriptor>,
    val sorts: List<SortDescriptor>
)

val animalQueryUiConfigs = mapOf(
    AnimalQueryType.ALL to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("enclosure", "Клетка"),
            FieldDescriptor("weight", "Вес"),
            FieldDescriptor("height", "Рост")
        ),
        filters = listOf(
            FilterDescriptor("species_id", "Вид (ID)", FilterType.NUMBER),
            FilterDescriptor("enclosure_id", "Клетка (ID)", FilterType.NUMBER),
            FilterDescriptor("gender", "Пол", FilterType.DROPDOWN)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("weight", "Вес"),
            SortDescriptor("height", "Рост")
        )
    ),
    AnimalQueryType.NEED_WARM to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("enclosure", "Клетка")
        ),
        filters = listOf(
            FilterDescriptor("species_id", "Вид (ID)", FilterType.NUMBER),
            FilterDescriptor("age_min", "Мин. возраст", FilterType.NUMBER),
            FilterDescriptor("age_max", "Макс. возраст", FilterType.NUMBER)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("years_in_zoo", "Лет в зоопарке")
        )
    ),
    AnimalQueryType.BY_VACCINE_DISEASE to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("years_in_zoo", "Лет в зоопарке"),
            FieldDescriptor("enclosure", "Клетка"),
            FieldDescriptor("offspring_count", "Потомство"),
            FieldDescriptor("vaccinations", "Прививки"),
            FieldDescriptor("diseases", "Болезни")
        ),
        filters = listOf(
            FilterDescriptor("disease_id", "Болезнь (ID)", FilterType.NUMBER),
            FilterDescriptor("vaccine_id", "Прививка (ID)", FilterType.NUMBER),
            FilterDescriptor("gender", "Пол", FilterType.DROPDOWN),
            FilterDescriptor("age_min", "Мин. возраст", FilterType.NUMBER),
            FilterDescriptor("age_max", "Макс. возраст", FilterType.NUMBER),
            FilterDescriptor("years_in_zoo_min", "Мин. лет в зоопарке", FilterType.NUMBER),
            FilterDescriptor("years_in_zoo_max", "Макс. лет в зоопарке", FilterType.NUMBER),
            FilterDescriptor("offspring_min", "Мин. потомков", FilterType.NUMBER),
            FilterDescriptor("offspring_max", "Макс. потомков", FilterType.NUMBER)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("years_in_zoo", "Лет в зоопарке"),
            SortDescriptor("offspring_count", "Потомство")
        )
    ),
    AnimalQueryType.COMPATIBLE to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("years_in_zoo", "Лет в зоопарке"),
            FieldDescriptor("enclosure", "Клетка")
        ),
        filters = listOf(
            FilterDescriptor("compatible_with_species_id", "Совместим с видом (ID)", FilterType.NUMBER),
            FilterDescriptor("need_warm", "Требует тепло (Y/N)", FilterType.DROPDOWN)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("years_in_zoo", "Лет в зоопарке")
        )
    ),
    AnimalQueryType.BY_FEED to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("enclosure", "Клетка"),
            FieldDescriptor("feed_type", "Тип корма"),
            FieldDescriptor("season", "Сезон"),
            FieldDescriptor("age_group", "Группа")
        ),
        filters = listOf(
            FilterDescriptor("species_id", "Вид (ID)", FilterType.NUMBER),
            FilterDescriptor("feed_type_id", "Тип корма (ID)", FilterType.NUMBER),
            FilterDescriptor("season", "Сезон", FilterType.TEXT),
            FilterDescriptor("age_group", "Группа", FilterType.TEXT)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("feed_type", "Тип корма"),
            SortDescriptor("season", "Сезон"),
            SortDescriptor("age_group", "Группа")
        )
    ),
    AnimalQueryType.FULL_INFO to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("birth_date", "Дата рождения"),
            FieldDescriptor("age", "Возраст"),
            FieldDescriptor("arrival_date", "Дата прибытия"),
            FieldDescriptor("years_in_zoo", "Лет в зоопарке"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("enclosure", "Клетка"),
            FieldDescriptor("offspring_count", "Потомство"),
            FieldDescriptor("weight", "Вес"),
            FieldDescriptor("height", "Рост"),
            FieldDescriptor("vaccinations", "Прививки"),
            FieldDescriptor("diseases", "Болезни")
        ),
        filters = listOf(
            FilterDescriptor("species_id", "Вид (ID)", FilterType.NUMBER),
            FilterDescriptor("animal_id", "ID животного", FilterType.NUMBER),
            FilterDescriptor("enclosure_id", "Клетка (ID)", FilterType.NUMBER),
            FilterDescriptor("age_min", "Мин. возраст", FilterType.NUMBER),
            FilterDescriptor("age_max", "Макс. возраст", FilterType.NUMBER),
            FilterDescriptor("gender", "Пол", FilterType.DROPDOWN),
            FilterDescriptor("weight_min", "Мин. вес", FilterType.NUMBER),
            FilterDescriptor("weight_max", "Макс. вес", FilterType.NUMBER),
            FilterDescriptor("height_min", "Мин. рост", FilterType.NUMBER),
            FilterDescriptor("height_max", "Макс. рост", FilterType.NUMBER),
            FilterDescriptor("years_in_zoo_min", "Мин. лет в зоопарке", FilterType.NUMBER),
            FilterDescriptor("years_in_zoo_max", "Макс. лет в зоопарке", FilterType.NUMBER),
            FilterDescriptor("disease_id", "Болезнь (ID)", FilterType.NUMBER),
            FilterDescriptor("vaccine_id", "Прививка (ID)", FilterType.NUMBER)
        ),
        sorts = listOf(
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст"),
            SortDescriptor("years_in_zoo", "Лет в зоопарке"),
            SortDescriptor("offspring_count", "Потомство"),
            SortDescriptor("weight", "Вес"),
            SortDescriptor("height", "Рост")
        )
    ),
    AnimalQueryType.EXPECT_OFFSPRING to AnimalQueryUiConfig(
        fields = listOf(
            FieldDescriptor("name", "Имя"),
            FieldDescriptor("species", "Вид"),
            FieldDescriptor("gender", "Пол"),
            FieldDescriptor("age", "Возраст")
        ),
        filters = listOf(
            FilterDescriptor("species_id", "Вид (ID)", FilterType.NUMBER)
        ),
        sorts = listOf(
            SortDescriptor("species", "Вид"),
            SortDescriptor("name", "Имя"),
            SortDescriptor("age", "Возраст")
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalListScreen(
    viewModel: AnimalListViewModel = viewModel(),
    onAnimalClick: (AnimalMenuItem) -> Unit = {}
) {
    val animals by viewModel.animals.collectAsState()
    val species by viewModel.species.collectAsState()
    val enclosures by viewModel.enclosures.collectAsState()
    val feedTypes by viewModel.feedTypes.collectAsState()
    val diseases by viewModel.diseases.collectAsState()
    val vaccines by viewModel.vaccines.collectAsState()
    val animalsAllMenu by viewModel.animalsAllMenu.collectAsState()
    val selectedAnimalDetails by viewModel.selectedAnimalDetails.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // --- UI State ---
    var queryMenuExpanded by remember { mutableStateOf(false) }
    var filterSheetOpen by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var selectedQuery by remember { mutableStateOf(AnimalQueryType.ALL) }
    var sortField by remember { mutableStateOf<String?>("name") }
    var sortDir by remember { mutableStateOf("asc") }
    // Основные фильтры
    var filterSpecies by remember { mutableStateOf<Int?>(null) }
    var filterEnclosure by remember { mutableStateOf<Int?>(null) }
    var filterFeedType by remember { mutableStateOf<Int?>(null) }
    var filterGender by remember { mutableStateOf<String?>(null) }
    var filterNeedWarm by remember { mutableStateOf<String?>(null) }
    var filterSeason by remember { mutableStateOf<String?>(null) }
    var filterAgeGroup by remember { mutableStateOf<String?>(null) }
    var filterAgeMin by remember { mutableStateOf<String>("") }
    var filterAgeMax by remember { mutableStateOf<String>("") }
    var filterWeightMin by remember { mutableStateOf<String>("") }
    var filterWeightMax by remember { mutableStateOf<String>("") }
    var filterHeightMin by remember { mutableStateOf<String>("") }
    var filterHeightMax by remember { mutableStateOf<String>("") }
    // Временные фильтры для BottomSheet
    var filterSpeciesTmp by remember { mutableStateOf<Int?>(filterSpecies) }
    var filterEnclosureTmp by remember { mutableStateOf<Int?>(filterEnclosure) }
    var filterFeedTypeTmp by remember { mutableStateOf<Int?>(filterFeedType) }
    var filterGenderTmp by remember { mutableStateOf<String?>(filterGender) }
    var filterNeedWarmTmp by remember { mutableStateOf<String?>(filterNeedWarm) }
    var filterSeasonTmp by remember { mutableStateOf<String?>(filterSeason) }
    var filterAgeGroupTmp by remember { mutableStateOf<String?>(filterAgeGroup) }
    var filterAgeMinTmp by remember { mutableStateOf(filterAgeMin) }
    var filterAgeMaxTmp by remember { mutableStateOf(filterAgeMax) }
    var filterWeightMinTmp by remember { mutableStateOf(filterWeightMin) }
    var filterWeightMaxTmp by remember { mutableStateOf(filterWeightMax) }
    var filterHeightMinTmp by remember { mutableStateOf(filterHeightMin) }
    var filterHeightMaxTmp by remember { mutableStateOf(filterHeightMax) }
    var filterDiseaseId by remember { mutableStateOf("") }
    var filterVaccineId by remember { mutableStateOf("") }
    var filterArrivalDateMin by remember { mutableStateOf("") }
    var filterArrivalDateMax by remember { mutableStateOf("") }
    var filterYearsInZooMin by remember { mutableStateOf("") }
    var filterYearsInZooMax by remember { mutableStateOf("") }
    var filterOffspringMin by remember { mutableStateOf("") }
    var filterOffspringMax by remember { mutableStateOf("") }
    var filterCompatibleWithSpeciesId by remember { mutableStateOf<Int?>(null) }
    var filterCompatibleWithSpeciesIdTmp by remember { mutableStateOf<Int?>(filterCompatibleWithSpeciesId) }
    // Временные переменные для BottomSheet:
    var filterDiseaseIdTmp by remember { mutableStateOf(filterDiseaseId) }
    var filterVaccineIdTmp by remember { mutableStateOf(filterVaccineId) }
    var filterArrivalDateMinTmp by remember { mutableStateOf(filterArrivalDateMin) }
    var filterArrivalDateMaxTmp by remember { mutableStateOf(filterArrivalDateMax) }
    var filterYearsInZooMinTmp by remember { mutableStateOf(filterYearsInZooMin) }
    var filterYearsInZooMaxTmp by remember { mutableStateOf(filterYearsInZooMax) }
    var filterOffspringMinTmp by remember { mutableStateOf(filterOffspringMin) }
    var filterOffspringMaxTmp by remember { mutableStateOf(filterOffspringMax) }
    var filterAnimalId by remember { mutableStateOf<String?>(null) }
    var filterAnimalIdTmp by remember { mutableStateOf<String?>(filterAnimalId) }
    var showAnimalDialog by remember { mutableStateOf(false) }
    var selectedAnimalId by remember { mutableStateOf<Int?>(null) }
    var showFindOffspringDialog by remember { mutableStateOf(false) }
    var selectedAnimalForOffspring by remember { mutableStateOf<Int?>(null) }
    var offspringCountResult by remember { mutableStateOf<String?>(null) }
    var isLoadingOffspring by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // --- Popup positioning ---
    var rootOffset by remember { mutableStateOf(IntOffset.Zero) }
    Box(
        Modifier
            .fillMaxSize()
            .background(TropicBackground)
            .onGloballyPositioned { coords ->
                val windowPos = coords.localToWindow(Offset.Zero)
                rootOffset = IntOffset(windowPos.x.toInt(), windowPos.y.toInt())
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            // AppBar с бургер-меню для выбора запроса
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
                    // Кастомный Popup для бургер-меню
                    var burgerMenuPopup by remember { mutableStateOf(false) }
                    var burgerButtonOffset by remember { mutableStateOf(Offset.Zero) }
                    var burgerButtonHeight by remember { mutableStateOf(0) }
                    var burgerButtonWidth by remember { mutableStateOf(0) }
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
                            Icon(Icons.Filled.Menu, contentDescription = "Выбрать запрос", tint = TropicOnPrimary)
                        }
                        if (burgerMenuPopup) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = IntOffset(burgerButtonOffset.x.roundToInt(), (burgerButtonOffset.y + burgerButtonHeight).roundToInt()),
                                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                                onDismissRequest = { burgerMenuPopup = false }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(280.dp)
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
                                            AnimalQueryType.values().forEach { queryType ->
                                                val isSelected = selectedQuery == queryType
                                                Box(
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                                        .clickable {
                                                            selectedQuery = queryType
                                                            burgerMenuPopup = false
                                                        }
                                                        .padding(horizontal = 20.dp, vertical = 16.dp)
                                                ) {
                                                    Text(
                                                        queryType.displayName,
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
                        text = "Животные",
                        style = MaterialTheme.typography.titleLarge,
                        color = TropicOnPrimary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    TextButton(
                        onClick = { showFindOffspringDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.offset(x = (-20).dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Найти потомков",
                                tint = TropicOnPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Column(
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Найти", color = TropicOnPrimary, fontSize = 13.sp, lineHeight = 14.sp)
                                Text("потомков", color = TropicOnPrimary, fontSize = 13.sp, lineHeight = 14.sp)
                            }
                        }
                    }
                }
            }
            // --- Total/Query ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalAnimals = animals.firstOrNull()?.total_animals
                Text(
                    text = "Всего: ${totalAnimals ?: 0}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    text = "${selectedQuery.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TropicTurquoise
                )
            }
            // --- Полоска фильтров/сортировки ---
            Surface(
                tonalElevation = 2.dp,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = TropicSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Box {
                    // Всё оборачиваем в Box для Popup
                    // --- переменные состояния для Popup ---
                    var showSortPopup by remember { mutableStateOf(false) }
                    var buttonOffset by remember { mutableStateOf(Offset.Zero) }
                    var buttonHeight by remember { mutableStateOf(0) }
                    var buttonWidth by remember { mutableStateOf(0) }
                    Box {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Кнопка фильтра
                            Button(
                                onClick = { filterSheetOpen = true },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Filled.FilterList, contentDescription = "Фильтры", tint = TropicGreen, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Фильтры", color = TropicGreen)
                            }
                            // Кнопка сортировки + кастомный Popup
                            Button(
                                onClick = { showSortPopup = true },
                                shape = RoundedCornerShape(24.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(44.dp)
                                    .onGloballyPositioned { coords ->
                                        buttonOffset = coords.positionInParent()
                                        buttonHeight = coords.size.height
                                        buttonWidth = coords.size.width
                                    }
                            ) {
                                Text(getFieldDisplayName(sortField ?: "name"), color = TropicTurquoise)
                            }
                            // Кнопка смены направления сортировки
                            IconButton(
                                onClick = { sortDir = if (sortDir == "asc") "desc" else "asc" },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color.White, shape = CircleShape)
                            ) {
                                Icon(
                                    if (sortDir == "asc") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                    contentDescription = "Сменить направление сортировки",
                                    tint = TropicTurquoise,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        if (showSortPopup) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = IntOffset(buttonOffset.x.roundToInt(), (buttonOffset.y + buttonHeight).roundToInt()),
                                properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                                onDismissRequest = { showSortPopup = false }
                            ) {
                                val density = LocalDensity.current
                                val minWidth = with(density) { 160.dp.toPx() }
                                val maxWidth = with(density) { 240.dp.toPx() }
                                val popupWidthPx = buttonWidth.coerceIn(minWidth.toInt(), maxWidth.toInt())
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
                                            getSortFieldsForQuery(selectedQuery).forEach { field ->
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
                                                        getFieldDisplayName(field),
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
            }
            // --- Список животных ---
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize().padding(top = 0.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                ) {
                    if (animals.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет данных")
                            }
                        }
                    } else {
                        items(animals) { item ->
                            AnimalCardDynamic(item, selectedQuery) {
                                viewModel.clearSelectedAnimalDetails()
                                selectedAnimalId = item.id
                                viewModel.loadAnimalDetails(item.id)
                                showAnimalDialog = true
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        // Показываем фильтры только если filterSheetOpen == true
        if (filterSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { filterSheetOpen = false },
                shape = RoundedCornerShape(0.dp),
                containerColor = Color(0xFFEFFAF3),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Верхняя градиентная полоса со стрелкой вниз
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
                FilterBottomSheet(
                    selectedQuery = selectedQuery,
                    species = species,
                    enclosures = enclosures,
                    feedTypes = feedTypes,
                    diseases = diseases,
                    vaccines = vaccines,
                    animalsAllMenu = animalsAllMenu,
                    filterSpecies = filterSpeciesTmp,
                    onSpeciesChange = { filterSpeciesTmp = it },
                    filterEnclosure = filterEnclosureTmp,
                    onEnclosureChange = { filterEnclosureTmp = it },
                    filterFeedType = filterFeedTypeTmp,
                    onFeedTypeChange = { filterFeedTypeTmp = it },
                    filterGender = filterGenderTmp,
                    onGenderChange = { filterGenderTmp = it },
                    filterNeedWarm = filterNeedWarmTmp,
                    onNeedWarmChange = { filterNeedWarmTmp = it },
                    filterSeason = filterSeasonTmp,
                    onSeasonChange = { filterSeasonTmp = it },
                    filterAgeGroup = filterAgeGroupTmp,
                    onAgeGroupChange = { filterAgeGroupTmp = it },
                    filterAgeMin = filterAgeMinTmp,
                    onAgeMinChange = { filterAgeMinTmp = it },
                    filterAgeMax = filterAgeMaxTmp,
                    onAgeMaxChange = { filterAgeMaxTmp = it },
                    filterWeightMin = filterWeightMinTmp,
                    onWeightMinChange = { filterWeightMinTmp = it },
                    filterWeightMax = filterWeightMaxTmp,
                    onWeightMaxChange = { filterWeightMaxTmp = it },
                    filterHeightMin = filterHeightMinTmp,
                    onHeightMinChange = { filterHeightMinTmp = it },
                    filterHeightMax = filterHeightMaxTmp,
                    onHeightMaxChange = { filterHeightMaxTmp = it },
                    filterDiseaseId = filterDiseaseIdTmp,
                    onDiseaseIdChange = { filterDiseaseIdTmp = it },
                    filterVaccineId = filterVaccineIdTmp,
                    onVaccineIdChange = { filterVaccineIdTmp = it },
                    filterArrivalDateMin = filterArrivalDateMinTmp,
                    onArrivalDateMinChange = { filterArrivalDateMinTmp = it },
                    filterArrivalDateMax = filterArrivalDateMaxTmp,
                    onArrivalDateMaxChange = { filterArrivalDateMaxTmp = it },
                    filterYearsInZooMin = filterYearsInZooMinTmp,
                    onYearsInZooMinChange = { filterYearsInZooMinTmp = it },
                    filterYearsInZooMax = filterYearsInZooMaxTmp,
                    onYearsInZooMaxChange = { filterYearsInZooMaxTmp = it },
                    filterOffspringMin = filterOffspringMinTmp,
                    onOffspringMinChange = { filterOffspringMinTmp = it },
                    filterOffspringMax = filterOffspringMaxTmp,
                    onOffspringMaxChange = { filterOffspringMaxTmp = it },
                    filterCompatibleWithSpeciesId = filterCompatibleWithSpeciesIdTmp,
                    onCompatibleWithSpeciesIdChange = { filterCompatibleWithSpeciesIdTmp = it },
                    filterAnimalId = filterAnimalIdTmp,
                    onAnimalIdChange = { filterAnimalIdTmp = it },
                    dropdownWidth = 180.dp,
                    onApply = {
                        filterSheetOpen = false
                        filterSpecies = filterSpeciesTmp
                        filterEnclosure = filterEnclosureTmp
                        filterFeedType = filterFeedTypeTmp
                        filterGender = filterGenderTmp
                        filterNeedWarm = filterNeedWarmTmp
                        filterSeason = filterSeasonTmp
                        filterAgeGroup = filterAgeGroupTmp
                        filterAgeMin = filterAgeMinTmp
                        filterAgeMax = filterAgeMaxTmp
                        filterWeightMin = filterWeightMinTmp
                        filterWeightMax = filterWeightMaxTmp
                        filterHeightMin = filterHeightMinTmp
                        filterHeightMax = filterHeightMaxTmp
                        filterDiseaseId = filterDiseaseIdTmp
                        filterVaccineId = filterVaccineIdTmp
                        filterArrivalDateMin = filterArrivalDateMinTmp
                        filterArrivalDateMax = filterArrivalDateMaxTmp
                        filterYearsInZooMin = filterYearsInZooMinTmp
                        filterYearsInZooMax = filterYearsInZooMaxTmp
                        filterOffspringMin = filterOffspringMinTmp
                        filterOffspringMax = filterOffspringMaxTmp
                        filterCompatibleWithSpeciesId = filterCompatibleWithSpeciesIdTmp
                        filterAnimalId = filterAnimalIdTmp
                    },
                    onDismiss = {
                        filterSheetOpen = false
                        filterSpeciesTmp = filterSpecies
                        filterEnclosureTmp = filterEnclosure
                        filterFeedTypeTmp = filterFeedType
                        filterGenderTmp = filterGender
                        filterNeedWarmTmp = filterNeedWarm
                        filterSeasonTmp = filterSeason
                        filterAgeGroupTmp = filterAgeGroup
                        filterAgeMinTmp = filterAgeMin
                        filterAgeMaxTmp = filterAgeMax
                        filterWeightMinTmp = filterWeightMin
                        filterWeightMaxTmp = filterWeightMax
                        filterHeightMinTmp = filterHeightMin
                        filterHeightMaxTmp = filterHeightMax
                        filterDiseaseIdTmp = filterDiseaseId
                        filterVaccineIdTmp = filterVaccineId
                        filterArrivalDateMinTmp = filterArrivalDateMin
                        filterArrivalDateMaxTmp = filterArrivalDateMax
                        filterYearsInZooMinTmp = filterYearsInZooMin
                        filterYearsInZooMaxTmp = filterYearsInZooMax
                        filterOffspringMinTmp = filterOffspringMin
                        filterOffspringMaxTmp = filterOffspringMax
                        filterCompatibleWithSpeciesIdTmp = filterCompatibleWithSpeciesId
                        filterAnimalIdTmp = filterAnimalId
                    }
                )
            }
        }
    }

    // --- Автоматическая загрузка данных при изменении сортировки/запроса/фильтров ---
    LaunchedEffect(selectedQuery, sortField, sortDir, filterSpecies, filterEnclosure, filterFeedType, filterGender, filterNeedWarm, filterSeason, filterAgeGroup, filterAgeMin, filterAgeMax, filterWeightMin, filterWeightMax, filterHeightMin, filterHeightMax, filterDiseaseId, filterVaccineId, filterArrivalDateMin, filterArrivalDateMax, filterYearsInZooMin, filterYearsInZooMax, filterOffspringMin, filterOffspringMax, filterCompatibleWithSpeciesId, filterAnimalId) {
        val params = mutableMapOf<String, Any?>()
        when (selectedQuery) {
            AnimalQueryType.ALL -> {
                params["species_id"] = filterSpecies
                params["enclosure_id"] = filterEnclosure
                params["gender"] = filterGender
                params["age_min"] = filterAgeMin.toIntOrNull()
                params["age_max"] = filterAgeMax.toIntOrNull()
                params["weight_min"] = filterWeightMin.toFloatOrNull()
                params["weight_max"] = filterWeightMax.toFloatOrNull()
                params["height_min"] = filterHeightMin.toFloatOrNull()
                params["height_max"] = filterHeightMax.toFloatOrNull()
            }
            AnimalQueryType.NEED_WARM -> {
                params["species_id"] = filterSpecies
                params["age_min"] = filterAgeMin.toIntOrNull()
                params["age_max"] = filterAgeMax.toIntOrNull()
            }
            AnimalQueryType.BY_VACCINE_DISEASE -> {
                params["disease_id"] = filterDiseaseId.toIntOrNull()
                params["vaccine_id"] = filterVaccineId.toIntOrNull()
                params["species_id"] = filterSpecies
                params["years_in_zoo_min"] = filterYearsInZooMin.toIntOrNull()
                params["years_in_zoo_max"] = filterYearsInZooMax.toIntOrNull()
                params["offspring_min"] = filterOffspringMin.toIntOrNull()
                params["offspring_max"] = filterOffspringMax.toIntOrNull()
                params["age_min"] = filterAgeMin.toIntOrNull()
                params["age_max"] = filterAgeMax.toIntOrNull()
                params["gender"] = filterGender
            }
            AnimalQueryType.COMPATIBLE -> {
                params["need_warm"] = filterNeedWarm
                params["compatible_with_species_id"] = filterCompatibleWithSpeciesId
            }
            AnimalQueryType.BY_FEED -> {
                params["species_id"] = filterSpecies
                params["feed_type_id"] = filterFeedType
                params["season"] = filterSeason
                params["age_group"] = filterAgeGroup
            }
            AnimalQueryType.FULL_INFO -> {
                params["species_id"] = filterSpecies
                params["animal_id"] = filterAnimalId?.toIntOrNull()
                params["enclosure_id"] = filterEnclosure
                params["age_min"] = filterAgeMin.toIntOrNull()
                params["age_max"] = filterAgeMax.toIntOrNull()
                params["gender"] = filterGender
                params["weight_min"] = filterWeightMin.toFloatOrNull()
                params["weight_max"] = filterWeightMax.toFloatOrNull()
                params["height_min"] = filterHeightMin.toFloatOrNull()
                params["height_max"] = filterHeightMax.toFloatOrNull()
                params["years_in_zoo_min"] = filterYearsInZooMin.toIntOrNull()
                params["years_in_zoo_max"] = filterYearsInZooMax.toIntOrNull()
                params["disease_id"] = filterDiseaseId.toIntOrNull()
                params["vaccine_id"] = filterVaccineId.toIntOrNull()
                params["offspring_min"] = filterOffspringMin.toIntOrNull()
                params["offspring_max"] = filterOffspringMax.toIntOrNull()
            }
            AnimalQueryType.EXPECT_OFFSPRING -> {
                params["species_id"] = filterSpecies
            }
        }
        // Сортировка всегда передается
        params["order_by"] = sortField
        params["order_dir"] = sortDir
        viewModel.loadAnimals(selectedQuery, params)
    }

    // --- Диалог с деталями животного (query11) ---
    if (showAnimalDialog && selectedAnimalId != null) {
        AlertDialog(
            onDismissRequest = { showAnimalDialog = false },
            confirmButton = {
                Button(
                    onClick = { showAnimalDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TropicOrange)
                ) { Text("Закрыть", color = Color.White) }
            },
            title = { Text("Детали животного") },
            text = {
                if (selectedAnimalDetails == null) {
                    Box(Modifier.height(80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val animalDetails = selectedAnimalDetails!!
                    Column {
                        Text("Имя: ${animalDetails.name}")
                        Text("Вид: ${animalDetails.species ?: "-"}")
                        Text("Пол: ${animalDetails.gender ?: "-"}")
                        Text("Дата рождения: ${animalDetails.birth_date ?: "-"}")
                        Text("Возраст: ${animalDetails.age ?: "-"}")
                        Text("Клетка: ${animalDetails.enclosure ?: "-"}")
                        Text("Дата прибытия: ${animalDetails.arrival_date ?: "-"}")
                        Text("Лет в зоопарке: ${animalDetails.years_in_zoo ?: "-"}")
                        Text("Вес: ${animalDetails.weight ?: "-"}")
                        Text("Рост: ${animalDetails.height ?: "-"}")
                        Text("Потомство: ${animalDetails.offspring_count ?: "-"}")
                        Text("Прививки: ${animalDetails.vaccinations ?: "-"}")
                        Text("Болезни: ${animalDetails.diseases ?: "-"}")
                    }
                }
            },
            containerColor = TropicOnBackground
        )
    }

    if (showFindOffspringDialog) {
        Dialog(onDismissRequest = {
            showFindOffspringDialog = false
            selectedAnimalForOffspring = null
            offspringCountResult = null
            isLoadingOffspring = false
        }) {
            Box(
                Modifier
                    .fillMaxWidth(0.92f)
                    .background(
                        Color(0xFFEFFAF3),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(2.dp, TropicTurquoise, shape = RoundedCornerShape(24.dp))
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Найти потомков", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                    Spacer(Modifier.height(18.dp))
                    DropdownSelector(
                        label = "Выберите животное",
                        options = animalsAllMenu.map { it.id to it.name },
                        selected = selectedAnimalForOffspring,
                        onSelected = { selectedAnimalForOffspring = it },
                        width = 260.dp
                    )
                    Spacer(Modifier.height(18.dp))
                    if (isLoadingOffspring) {
                        CircularProgressIndicator(Modifier.size(28.dp), color = TropicTurquoise)
                    } else {
                        offspringCountResult?.let {
                            val isCount = it.startsWith("Потомков:")
                            Text(
                                it,
                                color = if (isCount) TropicGreen else TropicTurquoise,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            showFindOffspringDialog = false
                            selectedAnimalForOffspring = null
                            offspringCountResult = null
                            isLoadingOffspring = false
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TropicOrange),
                        ) { Text("Закрыть", color = Color.White) }
                        if (selectedAnimalForOffspring != null && !isLoadingOffspring) {
                            Button(
                                onClick = {
                                    isLoadingOffspring = true
                                    offspringCountResult = null
                                    coroutineScope.launch {
                                        val result = viewModel.getOffspringCount(selectedAnimalForOffspring!!)
                                        offspringCountResult = result
                                        isLoadingOffspring = false
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise),
                                enabled = true
                            ) {
                                Text("Показать", color = Color.White)
                            }
                        } else {
                            OutlinedButton(
                                onClick = {},
                                enabled = false,
                                border = BorderStroke(1.dp, TropicTurquoise),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                            ) {
                                Text("Показать", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Вспомогательные компоненты и функции ---

@Composable
fun AnimalCardDynamic(item: AnimalMenuItem, queryType: AnimalQueryType, onClick: () -> Unit) {
    val config = animalQueryUiConfigs[queryType] ?: animalQueryUiConfigs[AnimalQueryType.ALL]!!
    val itemMap = itemToMap(item)
    Card(
        onClick = onClick,
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
                        colors = listOf(Color.White, Color(0xFFFCFFFE), Color(0xFFf4fff9)),
                        startX = 0f,
                        endX = 600f
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Левая часть — только текст
            Column(
                Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                config.fields.forEach { field ->
                    when (field.key) {
                        "weight" -> Text(
                            "Вес: " + (item.weight?.toString()?.plus(" кг") ?: "-"),
                            color = TropicOnBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        "height" -> Text(
                            "Рост: " + (item.height?.toString()?.plus(" м") ?: "-"),
                            color = TropicOnBackground,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        else -> {
                            val value = itemMap[field.key]
                            Text(
                                "${field.label}: ${value ?: "-"}",
                                color = TropicOnBackground,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
            // Правая часть — декоративная иконка листа (справа вверху, очень блеклая)
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = null,
                    tint = TropicGreen.copy(alpha = 0.10f),
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                )
            }
        }
    }
}

fun itemToMap(item: AnimalMenuItem): Map<String, Any?> {
    // Преобразуем data class в Map
    return mapOf(
        "name" to item.name,
        "gender" to item.gender,
        "birth_date" to item.birth_date,
        "age" to item.age,
        "species" to item.species,
        "enclosure" to item.enclosure,
        "feed_type" to item.feed_type,
        "season" to item.season,
        "age_group" to item.age_group,
        "total_animals" to item.total_animals,
        "weight" to item.weight,
        "height" to item.height,
        "arrival_date" to item.arrival_date,
        "years_in_zoo" to item.years_in_zoo,
        "offspring_count" to item.offspring_count,
        "vaccinations" to item.vaccinations,
        "diseases" to item.diseases
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    selectedQuery: AnimalQueryType,
    species: List<SpeciesDto>,
    enclosures: List<EnclosureDto>,
    feedTypes: List<FeedTypeDto>,
    diseases: List<DiseaseDto>,
    vaccines: List<VaccineDto>,
    animalsAllMenu: List<AnimalMenuItem>,
    filterSpecies: Int?,
    onSpeciesChange: (Int?) -> Unit,
    filterEnclosure: Int?,
    onEnclosureChange: (Int?) -> Unit,
    filterFeedType: Int?,
    onFeedTypeChange: (Int?) -> Unit,
    filterGender: String?,
    onGenderChange: (String?) -> Unit,
    filterNeedWarm: String?,
    onNeedWarmChange: (String?) -> Unit,
    filterSeason: String?,
    onSeasonChange: (String?) -> Unit,
    filterAgeGroup: String?,
    onAgeGroupChange: (String?) -> Unit,
    filterAgeMin: String,
    onAgeMinChange: (String) -> Unit,
    filterAgeMax: String,
    onAgeMaxChange: (String) -> Unit,
    filterWeightMin: String,
    onWeightMinChange: (String) -> Unit,
    filterWeightMax: String,
    onWeightMaxChange: (String) -> Unit,
    filterHeightMin: String,
    onHeightMinChange: (String) -> Unit,
    filterHeightMax: String,
    onHeightMaxChange: (String) -> Unit,
    filterDiseaseId: String,
    onDiseaseIdChange: (String) -> Unit,
    filterVaccineId: String,
    onVaccineIdChange: (String) -> Unit,
    filterArrivalDateMin: String,
    onArrivalDateMinChange: (String) -> Unit,
    filterArrivalDateMax: String,
    onArrivalDateMaxChange: (String) -> Unit,
    filterYearsInZooMin: String,
    onYearsInZooMinChange: (String) -> Unit,
    filterYearsInZooMax: String,
    onYearsInZooMaxChange: (String) -> Unit,
    filterOffspringMin: String,
    onOffspringMinChange: (String) -> Unit,
    filterOffspringMax: String,
    onOffspringMaxChange: (String) -> Unit,
    filterCompatibleWithSpeciesId: Int?,
    onCompatibleWithSpeciesIdChange: (Int?) -> Unit,
    filterAnimalId: String?,
    onAnimalIdChange: (String?) -> Unit,
    dropdownWidth: Dp = 160.dp,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    // FocusRequesters и позиции для всех числовых полей
    val ageMinFocusRequester = remember { FocusRequester() }
    val ageMaxFocusRequester = remember { FocusRequester() }
    val weightMinFocusRequester = remember { FocusRequester() }
    val weightMaxFocusRequester = remember { FocusRequester() }
    val heightMinFocusRequester = remember { FocusRequester() }
    val heightMaxFocusRequester = remember { FocusRequester() }
    var ageMinY by remember { mutableStateOf(0) }
    var ageMaxY by remember { mutableStateOf(0) }
    var weightMinY by remember { mutableStateOf(0) }
    var weightMaxY by remember { mutableStateOf(0) }
    var heightMinY by remember { mutableStateOf(0) }
    var heightMaxY by remember { mutableStateOf(0) }
    Box(
        Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFEFFAF3),
                shape = RoundedCornerShape(0.dp)
            )
            .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(0.dp))
    ) {
        Column(
            Modifier
                .padding(12.dp)
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            Text("Фильтры", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
            Spacer(Modifier.height(18.dp))
            if (selectedQuery in listOf(AnimalQueryType.ALL, AnimalQueryType.NEED_WARM, AnimalQueryType.BY_FEED, AnimalQueryType.EXPECT_OFFSPRING)) {
                FilterRow(label = "Вид", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = species.map { it.id to it.type_name },
                        selected = filterSpecies,
                        onSelected = onSpeciesChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.ALL) {
                FilterRow(label = "Клетка", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = enclosures.map { it.id to it.name },
                        selected = filterEnclosure,
                        onSelected = onEnclosureChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.BY_FEED) {
                FilterRow(label = "Тип корма", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = feedTypes.map { it.id to it.name },
                        selected = filterFeedType,
                        onSelected = onFeedTypeChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Сезон", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("Зима", "Весна", "Лето", "Осень", "Годовой").map { it to it },
                        selected = filterSeason,
                        onSelected = onSeasonChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Группа", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("Взрослый", "Молодой").map { it to it },
                        selected = filterAgeGroup,
                        onSelected = onAgeGroupChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.ALL ) {
                FilterRow(label = "Пол", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("М" to "Мужской", "Ж" to "Женский"),
                        selected = filterGender,
                        onSelected = onGenderChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.COMPATIBLE) {
                FilterRow(label = "Совместим с видом", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = species.map { it.id to it.type_name },
                        selected = filterCompatibleWithSpeciesId,
                        onSelected = onCompatibleWithSpeciesIdChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Теплолюбивое", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("Y" to "Да", "N" to "Нет"),
                        selected = filterNeedWarm,
                        onSelected = onNeedWarmChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.FULL_INFO) {
                FilterRow(label = "Вид", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = species.map { it.id to it.type_name },
                        selected = filterSpecies,
                        onSelected = onSpeciesChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Животное", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = animalsAllMenu.map { it.id to it.name },
                        selected = filterAnimalId?.toIntOrNull(),
                        onSelected = { onAnimalIdChange(it?.toString() ?: "") },
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Клетка", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = enclosures.map { it.id to it.name },
                        selected = filterEnclosure,
                        onSelected = onEnclosureChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Пол", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("М" to "Мужской", "Ж" to "Женский"),
                        selected = filterGender,
                        onSelected = onGenderChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery == AnimalQueryType.BY_VACCINE_DISEASE) {
                FilterRow(label = "Болезнь", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = diseases.map { it.id to it.name },
                        selected = filterDiseaseId.toIntOrNull(),
                        onSelected = { onDiseaseIdChange(it?.toString() ?: "") },
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Прививка", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = vaccines.map { it.id to it.name },
                        selected = filterVaccineId.toIntOrNull(),
                        onSelected = { onVaccineIdChange(it?.toString() ?: "") },
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Вид", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = species.map { it.id to it.type_name },
                        selected = filterSpecies,
                        onSelected = onSpeciesChange,
                        width = dropdownWidth
                    )
                })
                FilterRow(label = "Пол", content = {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("М" to "Мужской", "Ж" to "Женский"),
                        selected = filterGender,
                        onSelected = onGenderChange,
                        width = dropdownWidth
                    )
                })
            }
            if (selectedQuery in listOf(AnimalQueryType.ALL, AnimalQueryType.FULL_INFO, AnimalQueryType.NEED_WARM, AnimalQueryType.BY_VACCINE_DISEASE)) {
                FilterRow(label = "Возраст, лет", content = {
                    Row(
                        Modifier.width(dropdownWidth),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = filterAgeMin,
                            onValueChange = onAgeMinChange,
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = filterAgeMax,
                            onValueChange = onAgeMaxChange,
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                })
            }
            if (selectedQuery in listOf(AnimalQueryType.FULL_INFO, AnimalQueryType.BY_VACCINE_DISEASE)) {
                FilterRow(label = "Лет в зоопарке", content = {
                    Row(
                        Modifier.width(dropdownWidth),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = filterYearsInZooMin,
                            onValueChange = onYearsInZooMinChange,
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = filterYearsInZooMax,
                            onValueChange = onYearsInZooMaxChange,
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                })
                FilterRow(label = "Потомство", content = {
                    Row(
                        Modifier.width(dropdownWidth),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = filterOffspringMin,
                            onValueChange = onOffspringMinChange,
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = filterOffspringMax,
                            onValueChange = onOffspringMaxChange,
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                })
            }
            if (selectedQuery in listOf(AnimalQueryType.ALL, AnimalQueryType.FULL_INFO)) {
                FilterRow(label = "Вес, кг", content = {
                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = filterWeightMin,
                            onValueChange = onWeightMinChange,
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = filterWeightMax,
                            onValueChange = onWeightMaxChange,
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                })
                FilterRow(label = "Рост, м", content = {
                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = filterHeightMin,
                            onValueChange = onHeightMinChange,
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = filterHeightMax,
                            onValueChange = onHeightMaxChange,
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .width(dropdownWidth / 2 - 8.dp),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TropicOnBackground),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TropicOnBackground,
                                unfocusedTextColor = TropicOnBackground,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                focusedBorderColor = TropicTurquoise,
                                cursorColor = TropicTurquoise,
                                focusedLabelColor = Color.Gray,
                                unfocusedLabelColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                })
            }
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = {
                    onSpeciesChange(null)
                    onEnclosureChange(null)
                    onFeedTypeChange(null)
                    onGenderChange(null)
                    onNeedWarmChange(null)
                    onSeasonChange(null)
                    onAgeGroupChange(null)
                    onAgeMinChange("")
                    onAgeMaxChange("")
                    onWeightMinChange("")
                    onWeightMaxChange("")
                    onHeightMinChange("")
                    onHeightMaxChange("")
                    onDiseaseIdChange("")
                    onVaccineIdChange("")
                    onArrivalDateMinChange("")
                    onArrivalDateMaxChange("")
                    onYearsInZooMinChange("")
                    onYearsInZooMaxChange("")
                    onOffspringMinChange("")
                    onOffspringMaxChange("")
                    onCompatibleWithSpeciesIdChange(null)
                    onAnimalIdChange(null)
                    onApply()
                }) { Text("Сбросить фильтры", color = TropicGreen) }
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                ) { Text("Применить", color = Color.White) }
            }
        }
    }
}

fun getSortFieldsForQuery(query: AnimalQueryType): List<String> = when (query) {
    AnimalQueryType.ALL -> listOf("name", "age", "weight", "height")
    AnimalQueryType.NEED_WARM -> listOf("name", "age")
    AnimalQueryType.BY_FEED -> listOf("name")
    AnimalQueryType.EXPECT_OFFSPRING -> listOf("name", "age")
    AnimalQueryType.BY_VACCINE_DISEASE -> listOf("name", "age", "years_in_zoo", "offspring_count")
    AnimalQueryType.COMPATIBLE -> listOf("name", "age", "years_in_zoo")
    AnimalQueryType.FULL_INFO -> listOf("name", "age", "years_in_zoo", "offspring_count", "weight", "height")
    else -> listOf("name")
}

fun getFieldDisplayName(field: String): String = when (field) {
    "name" -> "Имя"
    "age" -> "Возраст"
    "species" -> "Вид"
    "enclosure" -> "Клетка"
    "feed_type" -> "Корм"
    "season" -> "Сезон"
    "age_group" -> "Группа"
    "weight" -> "Вес"
    "height" -> "Рост"
    "years_in_zoo" -> "Лет в зоопарке"
    "offspring_count" -> "Потомство"
    else -> field
} 
 
 
 