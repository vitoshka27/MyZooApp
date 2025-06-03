package com.example.myzoo.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.StaffMenuItem
import com.example.myzoo.data.remote.AnimalShortDto
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.draw.clip
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Dp
import com.example.myzoo.ui.screens.FilterRow
import com.example.myzoo.ui.screens.DropdownSelector
import java.text.DecimalFormat
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import com.example.myzoo.ui.theme.TropicOrange
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffListScreen(
    viewModel: StaffListViewModel = viewModel()
) {
    val staff by viewModel.staff.collectAsState()
    val animals by viewModel.animals.collectAsState()
    val selectedStaffDetails by viewModel.selectedStaffDetails.collectAsState()
    var selectedQuery by remember { mutableStateOf(StaffQueryType.GENERAL) }
    var sortField by remember { mutableStateOf(StaffSortField.FIRST_NAME) }
    var sortDir by remember { mutableStateOf("asc") }
    var filterCategory by remember { mutableStateOf<String?>(null) }
    var filterGender by remember { mutableStateOf<String?>(null) }
    var filterAgeMin by remember { mutableStateOf("") }
    var filterAgeMax by remember { mutableStateOf("") }
    var filterYearsWorkedMin by remember { mutableStateOf("") }
    var filterYearsWorkedMax by remember { mutableStateOf("") }
    var filterSalaryMin by remember { mutableStateOf("") }
    var filterSalaryMax by remember { mutableStateOf("") }
    var filterAnimalId by remember { mutableStateOf<Int?>(null) }
    var filterStartDate by remember { mutableStateOf("") }
    var filterEndDate by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortPopup by remember { mutableStateOf(false) }
    var showStaffDialog by remember { mutableStateOf(false) }
    var selectedStaff by remember { mutableStateOf<StaffMenuItem?>(null) }
    val decimalFormat = remember { DecimalFormat("#") }
    val staffCategories by viewModel.staffCategories.collectAsState()
    val dropdownWidth = 180.dp
    // --- Автоматическая загрузка данных ---
    LaunchedEffect(selectedQuery, sortField, sortDir, filterCategory, filterGender, filterAgeMin, filterAgeMax, filterYearsWorkedMin, filterYearsWorkedMax, filterSalaryMin, filterSalaryMax, filterAnimalId, filterStartDate, filterEndDate) {
        val params = mutableMapOf<String, Any?>()
        when (selectedQuery) {
            StaffQueryType.GENERAL -> {
                params["category_id"] = staffCategories.find { it.name == filterCategory }?.id
                params["gender"] = filterGender
                params["age_min"] = filterAgeMin.toIntOrNull()
                params["age_max"] = filterAgeMax.toIntOrNull()
                params["years_worked_min"] = filterYearsWorkedMin.toIntOrNull()
                params["years_worked_max"] = filterYearsWorkedMax.toIntOrNull()
                params["salary_min"] = filterSalaryMin.toFloatOrNull()
                params["salary_max"] = filterSalaryMax.toFloatOrNull()
            }
            StaffQueryType.CARETAKERS -> {
                params["animal_id"] = filterAnimalId
                params["start_date"] = filterStartDate.takeIf { it.isNotBlank() }
                params["end_date"] = filterEndDate.takeIf { it.isNotBlank() }
                params["category_id"] = staffCategories.find { it.name == filterCategory }?.id
                params["gender"] = filterGender
            }
        }
        params["order_by"] = sortField.apiName
        params["order_dir"] = sortDir
        viewModel.loadStaff(selectedQuery, params)
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(TropicBackground)
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
                    var burgerButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
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
                                        .width(240.dp)
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
                                            StaffQueryType.values().forEach { queryType ->
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
                        text = "Сотрудники",
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
                val total = staff.firstOrNull()?.total_employees ?: staff.firstOrNull()?.total_caretakers ?: staff.size
                Text(
                    text = "Всего сотрудников: $total",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    text = selectedQuery.displayName,
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
                    var showSortPopup by remember { mutableStateOf(false) }
                    var buttonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
                    var buttonHeight by remember { mutableStateOf(0) }
                    var buttonWidth by remember { mutableStateOf(0) }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { showFilterSheet = true },
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = "Фильтры", tint = TropicGreen, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Фильтры", color = TropicGreen)
                        }
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
                            Text(sortField.displayName, color = TropicTurquoise)
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
                    }
                    if (showSortPopup) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(buttonOffset.x.roundToInt(), (buttonOffset.y + buttonHeight).roundToInt()),
                            properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                            onDismissRequest = { showSortPopup = false }
                        ) {
                            val popupWidth = 180.dp
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.Transparent,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .width(popupWidth)
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
                                        getStaffSortFieldsForQuery(selectedQuery).forEach { field ->
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
                                                    field.displayName,
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
            // --- Список сотрудников ---
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize().padding(top = 0.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                ) {
                    if (staff.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет данных")
                            }
                        }
                    } else {
                        items(staff) { item ->
                            StaffCardDynamic(item, decimalFormat, {
                                selectedStaff = item
                                viewModel.loadStaffDetails(item)
                                showStaffDialog = true
                            }, selectedQuery)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        // --- Фильтры ---
        if (showFilterSheet) {
            StaffFilterBottomSheet(
                selectedQuery = selectedQuery,
                animals = animals,
                staffCategories = staffCategories,
                filterCategory = filterCategory,
                onCategoryChange = { filterCategory = it },
                filterGender = filterGender,
                onGenderChange = { filterGender = it },
                filterAgeMin = filterAgeMin,
                onAgeMinChange = { filterAgeMin = it },
                filterAgeMax = filterAgeMax,
                onAgeMaxChange = { filterAgeMax = it },
                filterYearsWorkedMin = filterYearsWorkedMin,
                onYearsWorkedMinChange = { filterYearsWorkedMin = it },
                filterYearsWorkedMax = filterYearsWorkedMax,
                onYearsWorkedMaxChange = { filterYearsWorkedMax = it },
                filterSalaryMin = filterSalaryMin,
                onSalaryMinChange = { filterSalaryMin = it },
                filterSalaryMax = filterSalaryMax,
                onSalaryMaxChange = { filterSalaryMax = it },
                filterAnimalId = filterAnimalId,
                onAnimalIdChange = { filterAnimalId = it },
                filterStartDate = filterStartDate,
                onStartDateChange = { filterStartDate = it },
                filterEndDate = filterEndDate,
                onEndDateChange = { filterEndDate = it },
                onApply = { showFilterSheet = false },
                onDismiss = { showFilterSheet = false }
            )
        }
        // --- Диалог с деталями сотрудника ---
        if (showStaffDialog && selectedStaff != null) {
            AlertDialog(
                onDismissRequest = { showStaffDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showStaffDialog = false },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TropicOrange)
                    ) { Text("Закрыть", color = Color.White) }
                },
                title = { Text("Детали сотрудника") },
                text = {
                    val staff = selectedStaff!!
                    Column {
                        Text("Фамилия: ${staff.last_name ?: "-"}")
                        Text("Имя: ${staff.first_name ?: "-"}")
                        Text("Отчество: ${staff.middle_name ?: "-"}")
                        Text("Пол: ${staff.gender ?: "-"}")
                        Text("Дата рождения: ${staff.birth_date ?: "-"}")
                        Text("Возраст: ${staff.age ?: "-"}")
                        Text("Категория: ${staff.category ?: "-"}")
                        Text("Дата найма: ${staff.hire_date ?: "-"}")
                        Text("Стаж: ${staff.years_worked ?: "-"}")
                        Text("Зарплата: ${staff.salary?.let { decimalFormat.format(it) } ?: "-"}")
                        if (selectedQuery == StaffQueryType.CARETAKERS) {
                            Text("Животное: ${staff.animal_name ?: "-"}")
                            Text("Период: ${staff.start_date ?: "-"} — ${staff.end_date ?: "-"}")
                        }
                    }
                },
                containerColor = TropicOnBackground
            )
        }
    }
}

@Composable
fun StaffCardDynamic(item: StaffMenuItem, decimalFormat: DecimalFormat, onClick: () -> Unit, selectedQuery: StaffQueryType = StaffQueryType.GENERAL) {
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
                        colors = listOf(Color.White, Color(0xFFFAFEFF), Color(0xFFebfafb)),
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
                Text("${item.last_name ?: "-"} ${item.first_name ?: "-"}", fontWeight = FontWeight.Bold, color = TropicOnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Категория: ${item.category ?: "-"}", color = TropicOnBackground)
                Text("Пол: ${item.gender ?: "-"}", color = TropicOnBackground)
                Text("Возраст: ${item.age ?: "-"}", color = TropicOnBackground)
                Text("Стаж: ${item.years_worked ?: "-"}", color = TropicOnBackground)
                Text("Зарплата: ${item.salary?.let { decimalFormat.format(it) } ?: "-"}", color = TropicGreen)
                if (selectedQuery == StaffQueryType.CARETAKERS) {
                    Text("Животное: ${item.animal_name ?: "-"}", color = TropicOnBackground)
                    Text("Период: ${(item.start_date ?: "-")} — ${(item.end_date ?: "-")}", color = TropicOnBackground)
                } else {
                    if (item.animal_name != null) Text("Животное: ${item.animal_name}", color = TropicOnBackground)
                }
            }
            // Правая часть — иконка портфеля
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Work,
                    contentDescription = null,
                    tint = Color(0xFFd4eaec),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 14.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffFilterBottomSheet(
    selectedQuery: StaffQueryType,
    animals: List<AnimalShortDto>,
    staffCategories: List<com.example.myzoo.data.remote.StaffCategoryDto>,
    filterCategory: String?,
    onCategoryChange: (String?) -> Unit,
    filterGender: String?,
    onGenderChange: (String?) -> Unit,
    filterAgeMin: String,
    onAgeMinChange: (String) -> Unit,
    filterAgeMax: String,
    onAgeMaxChange: (String) -> Unit,
    filterYearsWorkedMin: String,
    onYearsWorkedMinChange: (String) -> Unit,
    filterYearsWorkedMax: String,
    onYearsWorkedMaxChange: (String) -> Unit,
    filterSalaryMin: String,
    onSalaryMinChange: (String) -> Unit,
    filterSalaryMax: String,
    onSalaryMaxChange: (String) -> Unit,
    filterAnimalId: Int?,
    onAnimalIdChange: (Int?) -> Unit,
    filterStartDate: String,
    onStartDateChange: (String) -> Unit,
    filterEndDate: String,
    onEndDateChange: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit
) {
    val dropdownWidth = 180.dp
    // --- Временные переменные для фильтров ---
    if (selectedQuery == StaffQueryType.CARETAKERS) {
        var tmpAnimalId by remember { mutableStateOf(filterAnimalId) }
        var tmpStartDate by remember { mutableStateOf(filterStartDate) }
        var tmpEndDate by remember { mutableStateOf(filterEndDate) }
        var showStartDatePicker by remember { mutableStateOf(false) }
        var showEndDatePicker by remember { mutableStateOf(false) }
        ModalBottomSheet(
            onDismissRequest = {
                tmpAnimalId = filterAnimalId
                tmpStartDate = filterStartDate
                tmpEndDate = filterEndDate
                onDismiss()
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
            Column(Modifier.padding(16.dp)) {
                Text("Фильтры", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                Spacer(Modifier.height(18.dp))
                FilterRow(label = "Животное") {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = animals.map { it.id to it.name },
                        selected = tmpAnimalId,
                        onSelected = { tmpAnimalId = it },
                        width = 180.dp
                    )
                }
                FilterRow(label = "Период ухода") {
                    val displayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(
                            onClick = { showStartDatePicker = true },
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, TropicTurquoise),
                            modifier = Modifier.width(width = 180.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                        ) {
                            val startDateText = tmpStartDate.takeIf { it.isNotBlank() }?.let {
                                try { LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                            } ?: "от"
                            Text(
                                startDateText,
                                color = if (tmpStartDate.isNullOrBlank()) Color.Gray else TropicTurquoise
                            )
                        }
                        OutlinedButton(
                            onClick = { showEndDatePicker = true },
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, TropicTurquoise),
                            modifier = Modifier.width(width = 180.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                        ) {
                            val endDateText = tmpEndDate.takeIf { it.isNotBlank() }?.let {
                                try { LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                            } ?: "до"
                            Text(
                                endDateText,
                                color = if (tmpEndDate.isNullOrBlank()) Color.Gray else TropicTurquoise
                            )
                        }
                    }
                }
                if (showStartDatePicker) {
                    DatePickerDialog(
                        initialDate = tmpStartDate,
                        onDateSelected = {
                            tmpStartDate = it
                            showStartDatePicker = false
                        },
                        onDismiss = { showStartDatePicker = false }
                    )
                }
                if (showEndDatePicker) {
                    DatePickerDialog(
                        initialDate = tmpEndDate,
                        onDateSelected = {
                            tmpEndDate = it
                            showEndDatePicker = false
                        },
                        onDismiss = { showEndDatePicker = false }
                    )
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = {
                        tmpAnimalId = null
                        tmpStartDate = ""
                        tmpEndDate = ""
                        onAnimalIdChange(null)
                        onStartDateChange("")
                        onEndDateChange("")
                        onApply()
                    }) { Text("Сбросить фильтры", color = TropicGreen) }
                    Button(
                        onClick = {
                            onAnimalIdChange(tmpAnimalId)
                            onStartDateChange(tmpStartDate)
                            onEndDateChange(tmpEndDate)
                            onApply()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                    ) { Text("Применить", color = Color.White) }
                }
            }
        }
    } else {
        // GENERAL — прежние фильтры
        var tmpCategory by remember { mutableStateOf(filterCategory) }
        var tmpGender by remember { mutableStateOf(filterGender) }
        var tmpAgeMin by remember { mutableStateOf(filterAgeMin) }
        var tmpAgeMax by remember { mutableStateOf(filterAgeMax) }
        var tmpYearsWorkedMin by remember { mutableStateOf(filterYearsWorkedMin) }
        var tmpYearsWorkedMax by remember { mutableStateOf(filterYearsWorkedMax) }
        var tmpSalaryMin by remember { mutableStateOf(filterSalaryMin) }
        var tmpSalaryMax by remember { mutableStateOf(filterSalaryMax) }
        ModalBottomSheet(
            onDismissRequest = {
                tmpCategory = filterCategory
                tmpGender = filterGender
                tmpAgeMin = filterAgeMin
                tmpAgeMax = filterAgeMax
                tmpYearsWorkedMin = filterYearsWorkedMin
                tmpYearsWorkedMax = filterYearsWorkedMax
                tmpSalaryMin = filterSalaryMin
                tmpSalaryMax = filterSalaryMax
                onDismiss()
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
            Column(Modifier.padding(16.dp)) {
                Text("Фильтры", style = MaterialTheme.typography.titleLarge, color = TropicTurquoise)
                Spacer(Modifier.height(18.dp))
                FilterRow(label = "Категория") {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = staffCategories.map { it.name to it.name },
                        selected = tmpCategory,
                        onSelected = { tmpCategory = it },
                        width = 180.dp
                    )
                }
                FilterRow(label = "Пол") {
                    DropdownSelector(
                        label = "Не выбрано",
                        options = listOf("М" to "Мужской", "Ж" to "Женский"),
                        selected = tmpGender,
                        onSelected = { tmpGender = it },
                        width = 180.dp
                    )
                }
                FilterRow(label = "Возраст, лет") {
                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = tmpAgeMin,
                            onValueChange = { tmpAgeMin = it },
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                            value = tmpAgeMax,
                            onValueChange = { tmpAgeMax = it },
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                }
                FilterRow(label = "Стаж, лет") {
                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = tmpYearsWorkedMin,
                            onValueChange = { tmpYearsWorkedMin = it },
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                            value = tmpYearsWorkedMax,
                            onValueChange = { tmpYearsWorkedMax = it },
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                }
                FilterRow(label = "Зарплата") {
                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = tmpSalaryMin,
                            onValueChange = { tmpSalaryMin = it },
                            label = { Text("от", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                            value = tmpSalaryMax,
                            onValueChange = { tmpSalaryMax = it },
                            label = { Text("до", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.width((dropdownWidth / 2f) - 8.dp),
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
                }
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = {
                        tmpCategory = null
                        tmpGender = null
                        tmpAgeMin = ""
                        tmpAgeMax = ""
                        tmpYearsWorkedMin = ""
                        tmpYearsWorkedMax = ""
                        tmpSalaryMin = ""
                        tmpSalaryMax = ""
                        onCategoryChange(null)
                        onGenderChange(null)
                        onAgeMinChange("")
                        onAgeMaxChange("")
                        onYearsWorkedMinChange("")
                        onYearsWorkedMaxChange("")
                        onSalaryMinChange("")
                        onSalaryMaxChange("")
                        onApply()
                    }) { Text("Сбросить фильтры", color = TropicGreen) }
                    Button(
                        onClick = {
                            onCategoryChange(tmpCategory)
                            onGenderChange(tmpGender)
                            onAgeMinChange(tmpAgeMin)
                            onAgeMaxChange(tmpAgeMax)
                            onYearsWorkedMinChange(tmpYearsWorkedMin)
                            onYearsWorkedMaxChange(tmpYearsWorkedMax)
                            onSalaryMinChange(tmpSalaryMin)
                            onSalaryMaxChange(tmpSalaryMax)
                            onApply()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                    ) { Text("Применить", color = Color.White) }
                }
            }
        }
    }
} 
 
 