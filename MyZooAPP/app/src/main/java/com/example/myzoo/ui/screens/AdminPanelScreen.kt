package com.example.myzoo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicOrange
import com.example.myzoo.ui.theme.TropicSurface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.myzoo.data.remote.ApiModule
import com.example.myzoo.ui.theme.TropicYellow
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

class AdminPanelViewModel : ViewModel() {
    private val _tableData = MutableStateFlow<List<Map<String, Any?>>>(emptyList())
    val tableData: StateFlow<List<Map<String, Any?>>> = _tableData
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _successMsg = MutableStateFlow<String?>(null)
    val successMsg: StateFlow<String?> = _successMsg

    fun loadTable(table: String) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val resp = ApiModule.getAdminTable(table, limit = 100)
                _tableData.value = resp.data ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.message
                _tableData.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteRow(table: String, id: Int) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val resp = ApiModule.deleteAdminTableRow(table, id)
                // Всегда обновляем таблицу
                loadTable(table)
                // Ждём обновления данных
                kotlinx.coroutines.delay(300)
                val stillExists = _tableData.value.any { (it["id"] as? Number)?.toInt() == id }
                if (!stillExists) {
                    _successMsg.value = "Удалено"
                } else if (!resp.success) {
                    _error.value = resp.msg ?: "Ошибка удаления"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearMsg() { _successMsg.value = null }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    var selectedTable by remember { mutableStateOf<String?>(null) }
    val allTables = listOf(
        "animals", "species", "enclosures", "staff", "staff_categories", "diseases", "vaccines", "feed_types", "feed_orders", "feed_suppliers", "feed_inventory", "feed_items", "animal_medical_records", "animal_diseases", "animal_vaccinations", "animal_caretakers", "animal_movement_history", "zoo_exchanges", "climate_zones", "feeding_classifications", "supplier_feed_types", "category_attributes", "staff_attribute_values", "enclosure_neighbors", "incompatible_species", "daily_feeding_menu", "animal_diet_requirements", "feed_production"
    ).sorted()
    val vm: AdminPanelViewModel = viewModel()
    val tableData by vm.tableData.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val successMsg by vm.successMsg.collectAsState()
    val columns = tableData.firstOrNull()?.keys?.toList() ?: emptyList()
    val totalCount = tableData.size
    var showEditDialog by remember { mutableStateOf(false) }
    var editRow by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var editValues by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    Surface(
        color = TropicBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            // Кастомная шапка с градиентом и скруглением
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(
                        Brush.horizontalGradient(listOf(TropicOrange, TropicTurquoise)),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    ),
            ) {
                Row(
                    Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
                    }
                    Text(
                        "Админ-панель",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
            Column(Modifier.fillMaxSize().padding(5.dp)) {
                // Селектор таблицы и всего записей в одной строке
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Таблица:", color = TropicGreen)
                    Spacer(Modifier.width(12.dp))
                    DropdownSelector(
                        label = "Выберите таблицу",
                        options = allTables.map { it to it },
                        selected = selectedTable,
                        onSelected = {
                            selectedTable = it
                            if (it != null) vm.loadTable(it)
                        },
                        width = 255.dp,
                        popupMaxHeight = 420.dp
                    )
                }
                if (columns.isNotEmpty() && tableData.isNotEmpty()) {
                    Text(
                        "Всего записей: $totalCount",
                        color = TropicTurquoise,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 0.dp, top = 4.dp, bottom = 8.dp)
                    )
                }
                Spacer(Modifier.height(16.dp))
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TropicTurquoise)
                    }
                } else if (error != null) {
                    Text("Ошибка: $error", color = TropicOrange)
                } else if (selectedTable != null) {
                    if (successMsg != null) {
                        Text(successMsg!!, color = TropicGreen)
                        LaunchedEffect(successMsg) {
                            kotlinx.coroutines.delay(1200)
                            vm.clearMsg()
                        }
                    }
                    // Всего записей
                    if (columns.isNotEmpty() && tableData.isNotEmpty()) {
                        Box(Modifier.weight(1f)) {
                            LazyColumn(Modifier.fillMaxSize()) {
                                items(tableData) { row ->
                                    Card(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(containerColor = TropicSurface)
                                    ) {
                                        Column(Modifier.padding(12.dp)) {
                                            row.entries.forEachIndexed { idx, (key, value) ->
                                                Row(
                                                    Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        key,
                                                        color = Color.Gray,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        modifier = Modifier.weight(0.4f)
                                                    )
                                                    Text(
                                                        formatValue(value),
                                                        color = Color.Black,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.weight(0.6f),
                                                        maxLines = 5,
                                                        softWrap = true
                                                    )
                                                }
                                                if (idx != row.size - 1) {
                                                    Divider(
                                                        color = Color(0x22000000),
                                                        thickness = 1.dp,
                                                        modifier = Modifier.padding(vertical = 4.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = {
                                                editRow = row
                                                editValues = columns.associateWith { 
                                                    val v = row[it]
                                                    if (v is Number) {
                                                        val intVal = v.toInt()
                                                        if (v.toDouble() == intVal.toDouble()) intVal.toString() else v.toString()
                                                    } else v?.toString() ?: ""
                                                }
                                                showEditDialog = true
                                            }) {
                                                Icon(Icons.Filled.EditNote, contentDescription = "Редактировать", tint = TropicYellow, modifier = Modifier.size(35.dp))
                                            }
                                            IconButton(onClick = {
                                                val id = (row["id"] as? Number)?.toInt() ?: return@IconButton
                                                vm.deleteRow(selectedTable!!, id)
                                            }) {
                                                Icon(Icons.Filled.Delete, contentDescription = "Удалить", tint = TropicOrange, modifier = Modifier.size(28.dp))
                                            }
                                        }
                                    }
                                }
                            }
                            // Плавающая кнопка плюсик
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 10.dp, end = 15.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(TropicGreen)
                                        .clickable {
                                            editRow = null
                                            editValues = columns.associateWith { "" }
                                            showEditDialog = true
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Добавить запись", tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    } else {
                        Text("Нет данных", color = Color.Gray)
                    }
                } else {
                    Text("Выберите таблицу для просмотра и редактирования.", color = Color.Gray)
                }
            }
        }
    }
    // Диалог добавления/редактирования
    if (showEditDialog && columns.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            modifier = Modifier
                .defaultMinSize(minWidth = 280.dp)
                .widthIn(max = 420.dp)
                .heightIn(max = 600.dp),
            confirmButton = {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { showEditDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = TropicOrange),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Text("Отмена", color = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.horizontalGradient(listOf(TropicGreen, TropicTurquoise))
                            )
                    ) {
                        Button(
                            onClick = {
                                val id = editRow?.get("id") as? Number
                                val body = editValues.filterKeys { it != "id" }
                                if (editRow == null) {
                                    vm.viewModelScope.launch {
                                        ApiModule.addAdminTableRow(selectedTable!!, body)
                                        vm.loadTable(selectedTable!!)
                                    }
                                } else if (id != null) {
                                    vm.viewModelScope.launch {
                                        ApiModule.updateAdminTableRow(selectedTable!!, id.toInt(), body)
                                        vm.loadTable(selectedTable!!)
                                    }
                                }
                                showEditDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (editRow == null) "Добавить" else "Сохранить",
                                color = Color.White
                            )
                        }
                    }
                }
            },
            title = { Text(if (editRow == null) "Добавить запись" else "Редактировать запись", color = Color.White) },
            containerColor = TropicOnBackground,
            text = {
                Box(Modifier.verticalScroll(rememberScrollState())) {
                    Column {
                        columns.filter { it != "id" }.forEach { key ->
                            OutlinedTextField(
                                value = editValues[key] ?: "",
                                onValueChange = { editValues = editValues.toMutableMap().apply { put(key, it) } },
                                label = { Text(key, color = Color.White) },
                                singleLine = false,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedContainerColor = TropicOnBackground,
                                    unfocusedContainerColor = TropicOnBackground,
                                    unfocusedBorderColor = TropicTurquoise.copy(alpha = 0.2f),
                                    focusedBorderColor = TropicTurquoise,
                                    cursorColor = TropicTurquoise,
                                    focusedLabelColor = Color.White,
                                    unfocusedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}

// Добавляю функцию для форматирования чисел без .0
@Composable
fun formatValue(value: Any?): String {
    return when (value) {
        is Number -> {
            val intVal = value.toInt()
            if (value.toDouble() == intVal.toDouble()) intVal.toString() else value.toString()
        }
        else -> value?.toString() ?: "-"
    }
} 