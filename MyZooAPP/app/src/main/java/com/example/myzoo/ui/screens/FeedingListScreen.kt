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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
import java.text.SimpleDateFormat
import java.util.Locale
import java.text.DecimalFormat
import com.example.myzoo.data.remote.FeedItemDto

// --- Data class ---
data class FeedingMenuItem(
    val id: Int,
    val animal_id: Int,
    val diet_id: Int?,
    val feeding_number: Int?,
    val feeding_date_time: String?,
    val feed_item_id: Int?,
    val quantity: Float?
)

// --- ViewModel ---
class FeedingListViewModel : ViewModel() {
    private val _records = MutableStateFlow<List<FeedingMenuItem>>(emptyList())
    val records: StateFlow<List<FeedingMenuItem>> = _records
    private val _total = MutableStateFlow(0)
    val total: StateFlow<Int> = _total

    // Фильтры
    var filterAnimalId by mutableStateOf<String?>(null)
    var filterDateFrom by mutableStateOf<String?>(null)
    var filterDateTo by mutableStateOf<String?>(null)

    fun loadRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = ApiModule.getAdminTable("daily_feeding_menu")
                val items = response.data.mapNotNull { row ->
                    try {
                        FeedingMenuItem(
                            id = (row["id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            animal_id = (row["animal_id"] as? Number)?.toInt() ?: return@mapNotNull null,
                            diet_id = (row["diet_id"] as? Number)?.toInt(),
                            feeding_number = (row["feeding_number"] as? Number)?.toInt(),
                            feeding_date_time = row["feeding_date_time"]?.toString(),
                            feed_item_id = (row["feed_item_id"] as? Number)?.toInt(),
                            quantity = (row["quantity"] as? Number)?.toFloat()
                        )
                    } catch (_: Exception) { null }
                }
                val filterAnimalIdVal = filterAnimalId
                val filterDateFromVal = filterDateFrom
                val filterDateToVal = filterDateTo
                val filtered = items.filter { item ->
                    (filterAnimalIdVal.isNullOrBlank() || item.animal_id == filterAnimalIdVal?.toIntOrNull()) &&
                    (filterDateFromVal.isNullOrBlank() || (item.feeding_date_time != null && item.feeding_date_time >= filterDateFromVal)) &&
                    (filterDateToVal.isNullOrBlank() || (item.feeding_date_time != null && item.feeding_date_time <= filterDateToVal))
                }
                _records.value = filtered
                _total.value = filtered.size
            } catch (e: Exception) {
                _records.value = emptyList()
                _total.value = 0
            }
        }
    }
}

// --- Экран ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingListScreen(viewModel: FeedingListViewModel = viewModel()) {
    val records by viewModel.records.collectAsState()
    val total by viewModel.total.collectAsState()
    var showFilterSheet by remember { mutableStateOf(false) }
    var sortField by remember { mutableStateOf("feeding_date_time") }
    var sortDir by remember { mutableStateOf("desc") }
    var showSortPopup by remember { mutableStateOf(false) }
    var sortButtonOffset by remember { mutableStateOf(Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }
    var sortButtonWidth by remember { mutableStateOf(0) }

    // --- Получаем справочники ---
    val animalListViewModel: AnimalListViewModel = viewModel()
    val animalsAll by animalListViewModel.animalsAll.collectAsState()
    val animalIdToName = remember(animalsAll) { animalsAll.associate { it.id to it.name } }
    var filterSpeciesId by remember { mutableStateOf<Int?>(null) }
    var filterSpeciesIdTmp by remember { mutableStateOf<Int?>(filterSpeciesId) }
    // --- Feed items and diets ---
    var feedItems by remember { mutableStateOf<List<FeedItemDto>>(emptyList()) }
    var diets by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    val feedItemIdToName = remember(feedItems) { feedItems.associate { it.id to it.name } }
    LaunchedEffect(Unit) {
        try { feedItems = com.example.myzoo.data.remote.ApiModule.zooApi.getFeedItems().data } catch (_: Exception) {}
        try { diets = com.example.myzoo.data.remote.ApiModule.getAdminTable("animal_diet_requirements").data } catch (_: Exception) {}
    }

    // --- Диалог редактирования ---
    var showEditDialog by remember { mutableStateOf(false) }
    var editRecord by remember { mutableStateOf<FeedingMenuItem?>(null) }
    var editValues by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // --- Автоматическая загрузка данных при первом входе ---
    LaunchedEffect(Unit) {
        viewModel.loadRecords()
    }

    // --- Сортировка и фильтрация по виду ---
    val filteredRecords = remember(records, filterSpeciesId, animalsAll) {
        records.filter { item ->
            filterSpeciesId == null || animalsAll.find { it.id == item.animal_id }?.species_id == filterSpeciesId
        }
    }
    val sortedRecords = remember(filteredRecords, sortField, sortDir) {
        val comparator = when (sortField) {
            "feeding_date_time" -> compareBy<FeedingMenuItem> { it.feeding_date_time ?: "" }
            "quantity" -> compareBy<FeedingMenuItem> { it.quantity ?: 0f }
            else -> compareBy<FeedingMenuItem> { it.feeding_date_time ?: "" }
        }
        val base = filteredRecords.sortedWith(comparator)
        if (sortDir == "desc") base.reversed() else base
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
                    Text(
                        text = "Кормление животных",
                        style = MaterialTheme.typography.titleLarge,
                        color = TropicOnPrimary,
                        modifier = Modifier.padding(start = 24.dp)
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
                    text = "Всего записей: $total",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    text = "Кормление",
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
                    Spacer(Modifier.weight(1f))
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
                                "feeding_date_time" -> "Дата кормления"
                                "quantity" -> "Количество"
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
                }
                if (showSortPopup) {
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
                                        "feeding_date_time" to "Дата кормления",
                                        "quantity" to "Количество"
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
                LazyColumn(
                    Modifier.fillMaxSize().padding(top = 0.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                ) {
                    if (sortedRecords.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет данных")
                            }
                        }
                    } else {
                        items(sortedRecords) { item ->
                            FeedingCard(
                                item = item,
                                animalName = animalIdToName[item.animal_id],
                                feedItemIdToName = feedItemIdToName,
                                onEdit = {
                                    editRecord = item
                                    editValues = mapOf(
                                        "animal_id" to item.animal_id.toString(),
                                        "diet_id" to (item.diet_id?.toString() ?: ""),
                                        "feeding_number" to (item.feeding_number?.toString() ?: ""),
                                        "feeding_date_time" to (item.feeding_date_time ?: ""),
                                        "feed_item_id" to (item.feed_item_id?.toString() ?: ""),
                                        "quantity" to (item.quantity?.toString() ?: "")
                                    )
                                    showEditDialog = true
                                },
                                onDelete = {
                                    isDeleting = true
                                    deleteId = item.id
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
        // --- Фильтры ---
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showFilterSheet = false
                    filterSpeciesIdTmp = filterSpeciesId
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
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            filterSpeciesId = null
                            filterSpeciesIdTmp = null
                            showFilterSheet = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = {
                                filterSpeciesId = filterSpeciesIdTmp
                                showFilterSheet = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
        // --- Плавающая кнопка плюсик ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp, start = 18.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(TropicGreen)
                    .clickable {
                        editRecord = null
                        editValues = mapOf(
                            "feeding_date_time" to "",
                            "quantity" to "",
                            "feeding_number" to "",
                            "diet_id" to "",
                            "feed_item_id" to ""
                        )
                        showEditDialog = true
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить запись", tint = Color.White, modifier = Modifier.size(32.dp))
            }
        }
    }

    // --- Диалог удаления ---
    if (isDeleting && deleteId != null) {
        AlertDialog(
            onDismissRequest = { isDeleting = false; deleteId = null },
            title = { Text("Удалить запись?", color = TropicOrange) },
            text = { Text("Вы уверены, что хотите удалить эту запись?", color = TropicOnBackground) },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        ApiModule.deleteAdminTableRow("daily_feeding_menu", deleteId!!)
                        viewModel.loadRecords()
                        isDeleting = false
                        deleteId = null
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = TropicOrange)) {
                    Text("Удалить", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { isDeleting = false; deleteId = null }, colors = ButtonDefaults.buttonColors(containerColor = TropicSurface)) {
                    Text("Отмена", color = TropicOnBackground)
                }
            }
        )
    }

    // --- Диалог редактирования ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (editRecord == null) "Добавить запись" else "Редактировать запись", color = TropicTurquoise) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editValues["feeding_date_time"] ?: "",
                        onValueChange = { editValues = editValues.toMutableMap().apply { put("feeding_date_time", it) } },
                        label = { Text("Дата и время кормления", color = Color.White) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editValues["quantity"] ?: "",
                        onValueChange = { editValues = editValues.toMutableMap().apply { put("quantity", it) } },
                        label = { Text("Количество", color = Color.White) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editValues["feeding_number"] ?: "",
                        onValueChange = { editValues = editValues.toMutableMap().apply { put("feeding_number", it) } },
                        label = { Text("Номер кормления", color = Color.White) },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editValues["diet_id"] ?: "",
                        onValueChange = { editValues = editValues.toMutableMap().apply { put("diet_id", it) } },
                        label = { Text("Номер диеты", color = Color.White) },
                        singleLine = true
                    )
                    FilterRow(label = "Корм", labelColor = Color.White) {
                        DropdownSelector(
                            label = "Выберите корм",
                            options = feedItems.map { it.id to it.name },
                            selected = editValues["feed_item_id"]?.toIntOrNull(),
                            onSelected = { editValues = editValues.toMutableMap().apply { put("feed_item_id", it?.toString() ?: "") } },
                            width = 220.dp
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    coroutineScope.launch {
                        val id = editRecord?.id
                        val body = editValues.filterKeys { it != "id" }
                        if (id != null) {
                            ApiModule.updateAdminTableRow("daily_feeding_menu", id, body)
                        }
                        viewModel.loadRecords()
                        showEditDialog = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)) {
                    Text("Сохранить", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = TropicSurface)) {
                    Text("Отмена", color = TropicOnBackground)
                }
            }
        )
    }
}

// --- Карточка кормления ---
@Composable
fun FeedingCard(item: FeedingMenuItem, animalName: String?, feedItemIdToName: Map<Int, String>, onEdit: () -> Unit, onDelete: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }
    val decimalFormat = remember { DecimalFormat("#") }
    val displayDate = item.feeding_date_time?.let {
        try {
            // Если только дата, добавляем время
            val dateTimeStr = if (it.length <= 10) it + " 00:00" else it.replace("T", " ").substring(0,16)
            // Пробуем LocalDateTime
            val ldt = java.time.LocalDateTime.parse(dateTimeStr, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            dateFormat.format(java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant()))
        } catch (_: Exception) {
            try {
                // Попробовать OffsetDateTime
                val odt = java.time.OffsetDateTime.parse(it)
                dateFormat.format(java.util.Date.from(odt.toInstant()))
            } catch (_: Exception) {
                try {
                    // Попробовать LocalDate
                    val ld = java.time.LocalDate.parse(it)
                    dateFormat.format(java.util.Date.from(ld.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()))
                } catch (_: Exception) { it }
            }
        }
    } ?: "-"
    val displayQuantity = item.quantity?.let {
        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
    } ?: "-"
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
                Text("Дата и время: $displayDate", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Количество: $displayQuantity кг", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Номер кормления: ${item.feeding_number?.toString() ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Номер диеты: ${item.diet_id?.toString() ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Корм: ${item.feed_item_id?.let { feedItemIdToName[it] } ?: item.feed_item_id?.toString() ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
            }
            Column(
                Modifier.align(Alignment.Top),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "Редактировать", tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = Color(0xFFFF7043), modifier = Modifier.size(28.dp))
                }
            }
        }
    }
} 