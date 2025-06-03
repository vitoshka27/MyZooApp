package com.example.myzoo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.ProductionItem
import com.example.myzoo.data.remote.FeedTypeDto
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import java.text.DecimalFormat
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import com.example.myzoo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionListScreen(
    viewModel: ProductionListViewModel = viewModel()
) {
    val productions by viewModel.productions.collectAsState()
    val feedTypes by viewModel.feedTypes.collectAsState()
    var sortField by remember { mutableStateOf("feed_item") }
    var sortDir by remember { mutableStateOf("asc") }
    var filterFeedTypeId by remember { mutableStateOf<Int?>(null) }
    var tmpFeedTypeId by remember { mutableStateOf<Int?>(filterFeedTypeId) }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortPopup by remember { mutableStateOf(false) }
    val decimalFormat = remember { DecimalFormat("#") }
    val dropdownWidth = 180.dp
    var sortButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }
    var sortButtonWidth by remember { mutableStateOf(0) }

    // --- Автоматическая загрузка данных ---
    LaunchedEffect(sortField, sortDir, filterFeedTypeId) {
        val params = mutableMapOf<String, Any?>()
        params["feed_type_id"] = filterFeedTypeId
        params["order_by"] = sortField
        params["order_dir"] = sortDir
        viewModel.loadProductions(params)
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
                        text = "Производство корма",
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
                val total = productions.firstOrNull()?.total_feed_items ?: productions.size
                Text(
                    text = "Всего позиций: $total",
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
                Box {
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
                                    sortButtonOffset = coords.positionInParent()
                                    sortButtonHeight = coords.size.height
                                    sortButtonWidth = coords.size.width
                                }
                        ) {
                            Text(
                                when (sortField) {
                                    "feed_item" -> "Корм"
                                    "feed_type" -> "Тип корма"
                                    "total_produced" -> "Произведено"
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
                        val density = LocalDensity.current
                        val minWidth = with(density) { 160.dp.toPx() }
                        val maxWidth = with(density) { 240.dp.toPx() }
                        val popupWidthPx = sortButtonWidth.coerceIn(minWidth.toInt(), maxWidth.toInt())
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(sortButtonOffset.x.roundToInt(), (sortButtonOffset.y + sortButtonHeight).roundToInt()),
                            properties = PopupProperties(focusable = true, dismissOnClickOutside = true),
                            onDismissRequest = { showSortPopup = false }
                        ) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = Color.Transparent,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .width(with(density) { popupWidthPx.toDp() })
                                    .border(1.dp, Color(0x22000000), shape = RoundedCornerShape(24.dp))
                                    .background(
                                        Brush.verticalGradient(colors = listOf(Color(0xFFFFFFFF), Color(0xFFFCFFFE))),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                            ) {
                                Column {
                                    listOf(
                                        "feed_item" to "Корм",
                                        "feed_type" to "Тип корма",
                                        "total_produced" to "Произведено"
                                    ).forEach { (field, label) ->
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
                                            Text(label, color = if (isSelected) TropicTurquoise else TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // --- Фильтры ---
            if (showFilterSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        tmpFeedTypeId = filterFeedTypeId
                        showFilterSheet = false
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
                        FilterRow(label = "Тип корма") {
                            DropdownSelector(
                                label = "Не выбрано",
                                options = feedTypes.map { it.id to it.name },
                                selected = tmpFeedTypeId,
                                onSelected = { tmpFeedTypeId = it },
                                width = dropdownWidth
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = {
                                tmpFeedTypeId = null
                                filterFeedTypeId = null
                                showFilterSheet = false
                            }) { Text("Сбросить фильтры", color = TropicGreen) }
                            Button(
                                onClick = {
                                    filterFeedTypeId = tmpFeedTypeId
                                    showFilterSheet = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                            ) { Text("Применить", color = Color.White) }
                        }
                    }
                }
            }
            // --- Список производства ---
            Box(Modifier.weight(1f)) {
                LazyColumn(
                    Modifier.fillMaxSize().padding(top = 0.dp),
                    contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                ) {
                    if (productions.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет данных")
                            }
                        }
                    } else {
                        items(productions) { item ->
                            ProductionCard(item, decimalFormat)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductionCard(item: ProductionItem, decimalFormat: DecimalFormat) {
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
                        colors = listOf(Color.White, Color(0xFFfffff2), Color(0xFFfffeeb)),
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
                Text(
                    item.feed_item,
                    fontWeight = FontWeight.Bold,
                    color = TropicOnBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Тип корма: ${item.feed_type}", color = TropicOnBackground)
                Text("Произведено: ${item.total_produced?.let { decimalFormat.format(it) } ?: "-"}",
                    color = TropicGreen)
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_local_dining),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 10.dp),
                    alpha = 0.10f,
                    colorFilter = ColorFilter.tint(Color(0xFF615f00))
                )
            }
        }
    }
}