package com.example.myzoo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.ExchangeItem
import com.example.myzoo.data.remote.SpeciesDto
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.res.painterResource
import com.example.myzoo.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeListScreen(viewModel: ExchangeListViewModel = viewModel()) {
    val exchanges by viewModel.exchanges.collectAsState()
    val species by viewModel.species.collectAsState()
    var filterSheetOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var sortField by remember { mutableStateOf(viewModel.getSortField()) }
    var sortDir by remember { mutableStateOf(viewModel.getSortDir()) }
    var filterSpecies by remember { mutableStateOf(viewModel.getFilterSpecies()) }
    var sortButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }

    // Автоматическая загрузка при изменении фильтра/сортировки
    LaunchedEffect(filterSpecies, sortField, sortDir) {
        viewModel.setFilterSpecies(filterSpecies)
        viewModel.setSortField(sortField)
        viewModel.setSortDir(sortDir)
        viewModel.loadExchanges(filterSpecies, sortField, sortDir)
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
                        text = "Обмены между зоопарками",
                        style = MaterialTheme.typography.titleLarge,
                        color = TropicOnPrimary,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
            }
            // --- Total ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val total = exchanges.firstOrNull()?.total_zoos ?: 0
                Text(
                    text = "Всего зоопарков: $total",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
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
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
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
                    Button(
                        onClick = { sortMenuOpen = true },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .onGloballyPositioned { coords ->
                                sortButtonOffset = coords.positionInParent()
                                sortButtonHeight = coords.size.height
                            }
                    ) {
                        Text(
                            when (sortField) {
                                "partner_zoo" -> "Имя"
                                "exchange_count" -> "Кол-во обменов"
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
                if (sortMenuOpen) {
                    Popup(
                        alignment = Alignment.TopStart,
                        offset = IntOffset(sortButtonOffset.x.roundToInt(), (sortButtonOffset.y + sortButtonHeight).roundToInt()),
                        properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                        onDismissRequest = { sortMenuOpen = false }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Transparent,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .width(180.dp)
                                .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                        ) {
                            Column {
                                listOf("partner_zoo" to "Имя", "exchange_count" to "Кол-во обменов").forEach { (field, label) ->
                                    val isSelected = sortField == field
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                            .clickable {
                                                sortField = field
                                                sortMenuOpen = false
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
            // --- Список обменов ---
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize().padding(top = 0.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                ) {
                    if (exchanges.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет данных")
                            }
                        }
                    } else {
                        items(exchanges) { item ->
                            ExchangeCard(item)
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
                // --- Шапка фильтров ---
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
                        imageVector = Icons.Filled.ExpandMore,
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
                    FilterRow(label = "Вид", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = species.map { it.id to it.type_name },
                            selected = filterSpecies,
                            onSelected = { filterSpecies = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            filterSpecies = null
                            filterSheetOpen = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = { filterSheetOpen = false },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
    }
}

@Composable
fun ExchangeCard(item: ExchangeItem) {
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
                Text(item.partner_zoo, fontWeight = FontWeight.Bold, color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Кол-во обменов: ${item.exchange_count}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
            }
            // Правая часть — кастомная иконка Business Messages
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_business_messages),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 10.dp),
                    alpha = 0.10f,
                    colorFilter = ColorFilter.tint(Color(0xFF55008e))
                )
            }
        }
    }
} 