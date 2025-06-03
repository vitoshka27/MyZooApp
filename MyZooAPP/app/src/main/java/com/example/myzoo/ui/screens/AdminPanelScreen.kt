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
                if (resp.success) {
                    _successMsg.value = "Удалено"
                    loadTable(table)
                } else {
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
        "animals", "species", "enclosures", "staff", "staff_categories", "diseases", "vaccines", "feed_types", "feed_orders", "feed_suppliers", "feed_inventory", "feed_items", "animal_medical_records", "animal_diseases", "animal_vaccinations", "animal_caretakers", "animal_movement_history", "zoo_exchanges", "climate_zones", "feeding_classifications", "supplier_feed_types", "category_attributes", "staff_attribute_values", "enclosure_neighbors", "incompatible_species", "daily_feeding_menu", "animal_diet_requirements"
    )
    val vm: AdminPanelViewModel = viewModel()
    val tableData by vm.tableData.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val successMsg by vm.successMsg.collectAsState()
    val columns = tableData.firstOrNull()?.keys?.toList() ?: emptyList()
    val totalCount = tableData.size

    Surface(
        color = TropicBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {
            // AppBar
            TopAppBar(
                title = { Text("Админ-панель", color = TropicTurquoise) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад", tint = TropicTurquoise)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TropicSurface)
            )
            Column(Modifier.fillMaxSize().padding(5.dp)) {
                // Селектор таблицы и всего записей в одной строке
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Таблица:", color = TropicGreen)
                    Spacer(Modifier.width(12.dp))
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Button(onClick = { expanded = true }, colors = ButtonDefaults.buttonColors(containerColor = TropicSurface)) {
                            Text(selectedTable ?: "Выберите таблицу", color = TropicTurquoise)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            allTables.forEach { table ->
                                DropdownMenuItem(
                                    text = { Text(table) },
                                    onClick = {
                                        selectedTable = table
                                        expanded = false
                                        vm.loadTable(table)
                                    }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    if (columns.isNotEmpty() && tableData.isNotEmpty()) {
                        Text("Всего записей: $totalCount", color = TropicTurquoise, style = MaterialTheme.typography.bodyMedium)
                    }
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
                        LazyColumn(Modifier.weight(1f)) {
                            items(tableData) { row ->
                                Card(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    colors = CardDefaults.cardColors(containerColor = TropicSurface)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        row.forEach { (key, value) ->
                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    key,
                                                    color = Color.Gray,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                                Text(
                                                    formatValue(value),
                                                    color = Color.Black,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            IconButton(onClick = { /* TODO: Редактировать */ }) {
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
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp, 52.dp)
                                    .clip(RoundedCornerShape(26.dp))
                                    .background(TropicGreen)
                                    .clickable { /* TODO: Добавить новую запись */ },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Добавить запись", tint = Color.White, modifier = Modifier.size(32.dp))
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