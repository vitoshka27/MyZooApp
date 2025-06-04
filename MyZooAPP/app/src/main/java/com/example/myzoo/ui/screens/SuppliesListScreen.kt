package com.example.myzoo.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.SuppliesItem
import com.example.myzoo.data.remote.FeedOrderItem
import com.example.myzoo.ui.theme.TropicBackground
import com.example.myzoo.ui.theme.TropicGreen
import com.example.myzoo.ui.theme.TropicTurquoise
import com.example.myzoo.ui.theme.TropicLime
import com.example.myzoo.ui.theme.TropicSurface
import com.example.myzoo.ui.theme.TropicOnPrimary
import com.example.myzoo.ui.theme.TropicOnBackground
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import java.text.DecimalFormat
import com.example.myzoo.ui.screens.FilterRow
import com.example.myzoo.ui.screens.DropdownSelector
import com.example.myzoo.ui.screens.DatePickerDialog
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.platform.LocalDensity
import com.example.myzoo.data.remote.FeedItemDto
import com.example.myzoo.ui.theme.TropicOrange
import android.util.Log

enum class SuppliesTab { ORDERS, SUPPLIERS }

val LocalSupplies = compositionLocalOf<List<SuppliesItem>> { emptyList() }
val LocalFeedItems = compositionLocalOf<List<FeedItemDto>> { emptyList() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesListScreen(
    viewModel: SuppliesViewModel = viewModel()
) {
    val supplies by viewModel.supplies.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    val feedOrders by viewModel.feedOrders
    var sortField by remember { mutableStateOf("name") }
    var sortDir by remember { mutableStateOf("asc") }
    var filterFeedTypeId by remember { mutableStateOf<Int?>(null) }
    var filterOrderDateStart by remember { mutableStateOf("") }
    var filterOrderDateEnd by remember { mutableStateOf("") }
    var filterQuantityMin by remember { mutableStateOf("") }
    var filterQuantityMax by remember { mutableStateOf("") }
    var filterPriceMin by remember { mutableStateOf("") }
    var filterPriceMax by remember { mutableStateOf("") }
    var filterDeliveryDateStart by remember { mutableStateOf("") }
    var filterDeliveryDateEnd by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortPopup by remember { mutableStateOf(false) }
    var showOrderDateStartPicker by remember { mutableStateOf(false) }
    var showOrderDateEndPicker by remember { mutableStateOf(false) }
    var showDeliveryDateStartPicker by remember { mutableStateOf(false) }
    var showDeliveryDateEndPicker by remember { mutableStateOf(false) }
    val decimalFormat = remember { DecimalFormat("#") }
    val dropdownWidth = 180.dp
    val displayFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")

    // --- Временные переменные для фильтров ---
    var tmpFeedTypeId by remember { mutableStateOf(filterFeedTypeId) }
    var tmpOrderDateStart by remember { mutableStateOf(filterOrderDateStart) }
    var tmpOrderDateEnd by remember { mutableStateOf(filterOrderDateEnd) }
    var tmpQuantityMin by remember { mutableStateOf(filterQuantityMin) }
    var tmpQuantityMax by remember { mutableStateOf(filterQuantityMax) }
    var tmpPriceMin by remember { mutableStateOf(filterPriceMin) }
    var tmpPriceMax by remember { mutableStateOf(filterPriceMax) }
    var tmpDeliveryDateStart by remember { mutableStateOf(filterDeliveryDateStart) }
    var tmpDeliveryDateEnd by remember { mutableStateOf(filterDeliveryDateEnd) }

    var sortButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }
    var sortButtonWidth by remember { mutableStateOf(0) }

    var selectedTab by remember { mutableStateOf(SuppliesTab.SUPPLIERS) }
    var burgerMenuPopup by remember { mutableStateOf(false) }
    var burgerButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var burgerButtonHeight by remember { mutableStateOf(0) }
    var burgerButtonWidth by remember { mutableStateOf(0) }

    var filterSupplierId by remember { mutableStateOf<Int?>(null) }
    var filterFeedItemId by remember { mutableStateOf<Int?>(null) }
    var filterActualOnly by remember { mutableStateOf(false) }
    var tmpSupplierId by remember { mutableStateOf(filterSupplierId) }
    var tmpFeedItemId by remember { mutableStateOf(filterFeedItemId) }
    var tmpActualOnly by remember { mutableStateOf(filterActualOnly) }

    // --- Автоматическая загрузка данных ---
    LaunchedEffect(sortField, sortDir, filterFeedTypeId, filterOrderDateStart, filterOrderDateEnd, filterQuantityMin, filterQuantityMax, filterPriceMin, filterPriceMax, filterDeliveryDateStart, filterDeliveryDateEnd) {
        val params = mutableMapOf<String, Any?>()
        params["feed_type_id"] = filterFeedTypeId
        params["order_date_start"] = filterOrderDateStart.takeIf { it.isNotBlank() }
        params["order_date_end"] = filterOrderDateEnd.takeIf { it.isNotBlank() }
        params["quantity_min"] = filterQuantityMin.toFloatOrNull()
        params["quantity_max"] = filterQuantityMax.toFloatOrNull()
        params["price_min"] = filterPriceMin.toFloatOrNull()
        params["price_max"] = filterPriceMax.toFloatOrNull()
        params["delivery_date_start"] = filterDeliveryDateStart.takeIf { it.isNotBlank() }
        params["delivery_date_end"] = filterDeliveryDateEnd.takeIf { it.isNotBlank() }
        params["order_by"] = sortField
        params["order_dir"] = sortDir
        viewModel.loadSupplies(params)
    }
    // --- Загрузка заказов при выборе вкладки ---
    LaunchedEffect(selectedTab) {
        if (selectedTab == SuppliesTab.ORDERS) {
            viewModel.loadSupplies()
            viewModel.loadFeedOrders()
        }
        sortField = if (selectedTab == SuppliesTab.ORDERS) "price" else "name"
    }
    CompositionLocalProvider(
        LocalSupplies provides supplies,
        LocalFeedItems provides feedItems
    ) {
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
                        // Бургер-меню
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
                                    offset = IntOffset(burgerButtonOffset.x.roundToInt(), (burgerButtonOffset.y + burgerButtonHeight).roundToInt()),
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
                                                SuppliesTab.values().forEach { tab ->
                                                    val isSelected = selectedTab == tab
                                                    Box(
                                                        Modifier
                                                            .fillMaxWidth()
                                                            .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                                            .clickable {
                                                                selectedTab = tab
                                                                burgerMenuPopup = false
                                                            }
                                                            .padding(horizontal = 20.dp, vertical = 16.dp)
                                                    ) {
                                                        Text(
                                                            when (tab) {
                                                                SuppliesTab.ORDERS -> "Заказы"
                                                                SuppliesTab.SUPPLIERS -> "Поставщики"
                                                            },
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
                            text = "Поставки",
                            style = MaterialTheme.typography.titleLarge,
                            color = TropicOnPrimary,
                            modifier = Modifier.padding(start = 8.dp)
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
                    val total = when (selectedTab) {
                        SuppliesTab.SUPPLIERS -> supplies.firstOrNull()?.total_suppliers ?: supplies.size
                        SuppliesTab.ORDERS -> feedOrders.size
                    }
                    Text(
                        text = when (selectedTab) {
                            SuppliesTab.ORDERS -> "Всего заказов: $total"
                            SuppliesTab.SUPPLIERS -> "Всего поставщиков: $total"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = TropicGreen
                    )
                    Spacer(Modifier.width(24.dp))
                    Text(
                        text = when (selectedTab) {
                            SuppliesTab.ORDERS -> "Заказы"
                            SuppliesTab.SUPPLIERS -> "Поставщики"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TropicTurquoise
                    )
                }
                // --- Полоска фильтров/сортировки ---
                if (selectedTab == SuppliesTab.SUPPLIERS || selectedTab == SuppliesTab.ORDERS) {
                    Surface(
                        tonalElevation = 2.dp,
                        shadowElevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                        color = TropicSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Box(Modifier.fillMaxWidth()) {
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
                                    val sortLabel = if (selectedTab == SuppliesTab.ORDERS) {
                                        when (sortField) {
                                            "price" -> "Цена"
                                            "ordered_quantity" -> "Объем"
                                            else -> sortField
                                        }
                                    } else {
                                        when (sortField) {
                                            "name" -> "Имя"
                                            "order_count" -> "Кол-во заказов"
                                            "total_ordered_quantity" -> "Общий объём"
                                            "avg_price" -> "Средняя цена"
                                            else -> sortField
                                        }
                                    }
                                    Text(sortLabel, color = TropicTurquoise)
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
                            // --- Sort popup ---
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
                                            val sortOptions = if (selectedTab == SuppliesTab.ORDERS) listOf(
                                                "price" to "Цена",
                                                "ordered_quantity" to "Объем"
                                            ) else listOf(
                                                "name" to "Имя",
                                                "order_count" to "Кол-во заказов",
                                                "total_ordered_quantity" to "Общий объём",
                                                "avg_price" to "Средняя цена"
                                            )
                                            sortOptions.forEach { (field, label) ->
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
                }
                // --- Фильтры ---
                if (showFilterSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            tmpFeedTypeId = filterFeedTypeId
                            tmpOrderDateStart = filterOrderDateStart
                            tmpOrderDateEnd = filterOrderDateEnd
                            tmpQuantityMin = filterQuantityMin
                            tmpQuantityMax = filterQuantityMax
                            tmpPriceMin = filterPriceMin
                            tmpPriceMax = filterPriceMax
                            tmpDeliveryDateStart = filterDeliveryDateStart
                            tmpDeliveryDateEnd = filterDeliveryDateEnd
                            tmpSupplierId = filterSupplierId
                            tmpFeedItemId = filterFeedItemId
                            tmpActualOnly = filterActualOnly
                            showFilterSheet = false
                        },
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
                            if (selectedTab == SuppliesTab.ORDERS) {
                                FilterRow(label = "Поставщик") {
                                    DropdownSelector(
                                        label = "Не выбрано",
                                        options = supplies.map { it.id to it.name },
                                        selected = tmpSupplierId,
                                        onSelected = { tmpSupplierId = it },
                                        width = dropdownWidth
                                    )
                                }
                                FilterRow(label = "Корм") {
                                    DropdownSelector(
                                        label = "Не выбрано",
                                        options = feedItems.map { it.id to it.name },
                                        selected = tmpFeedItemId,
                                        onSelected = { tmpFeedItemId = it },
                                        width = dropdownWidth
                                    )
                                }
                                FilterRow(label = "Актуальный") {
                                    Box(
                                        Modifier
                                            .height(44.dp)
                                            .width(44.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Checkbox(
                                            checked = tmpActualOnly,
                                            onCheckedChange = { tmpActualOnly = it },
                                            modifier = Modifier
                                                .offset(x = (-67).dp),
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = TropicTurquoise,
                                                checkmarkColor = Color.White
                                            )
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                            if (selectedTab == SuppliesTab.SUPPLIERS) {
                                FilterRow(label = "Тип корма") {
                                    DropdownSelector(
                                        label = "Не выбрано",
                                        options = feedItems.map { it.id to it.name },
                                        selected = tmpFeedTypeId,
                                        onSelected = { tmpFeedTypeId = it },
                                        width = dropdownWidth
                                    )
                                }
                                FilterRow(label = "Объём заказа") {
                                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(
                                            value = tmpQuantityMin,
                                            onValueChange = { tmpQuantityMin = it },
                                            label = { Text("от", color = Color.Gray) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
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
                                            value = tmpQuantityMax,
                                            onValueChange = { tmpQuantityMax = it },
                                            label = { Text("до", color = Color.Gray) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
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
                                FilterRow(label = "Цена") {
                                    Row(Modifier.width(dropdownWidth), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        OutlinedTextField(
                                            value = tmpPriceMin,
                                            onValueChange = { tmpPriceMin = it },
                                            label = { Text("от", color = Color.Gray) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
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
                                            value = tmpPriceMax,
                                            onValueChange = { tmpPriceMax = it },
                                            label = { Text("до", color = Color.Gray) },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
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
                                FilterRow(label = "Дата заказа") {
                                    Column(Modifier.width(dropdownWidth)) {
                                        OutlinedButton(
                                            onClick = { showOrderDateStartPicker = true },
                                            shape = RoundedCornerShape(24.dp),
                                            border = BorderStroke(1.dp, TropicTurquoise),
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                                        ) {
                                            val text = tmpOrderDateStart.takeIf { it.isNotBlank() }?.let {
                                                try { java.time.LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                                            } ?: "от"
                                            Text(text, color = if (tmpOrderDateStart.isBlank()) Color.Gray else TropicTurquoise)
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showOrderDateEndPicker = true },
                                            shape = RoundedCornerShape(24.dp),
                                            border = BorderStroke(1.dp, TropicTurquoise),
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                                        ) {
                                            val text = tmpOrderDateEnd.takeIf { it.isNotBlank() }?.let {
                                                try { java.time.LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                                            } ?: "до"
                                            Text(text, color = if (tmpOrderDateEnd.isBlank()) Color.Gray else TropicTurquoise)
                                        }
                                    }
                                }
                                if (showOrderDateStartPicker) {
                                    DatePickerDialog(
                                        initialDate = tmpOrderDateStart,
                                        onDateSelected = { tmpOrderDateStart = it; showOrderDateStartPicker = false },
                                        onDismiss = { showOrderDateStartPicker = false }
                                    )
                                }
                                if (showOrderDateEndPicker) {
                                    DatePickerDialog(
                                        initialDate = tmpOrderDateEnd,
                                        onDateSelected = { tmpOrderDateEnd = it; showOrderDateEndPicker = false },
                                        onDismiss = { showOrderDateEndPicker = false }
                                    )
                                }
                                FilterRow(label = "Дата поставки") {
                                    Column(Modifier.width(dropdownWidth)) {
                                        OutlinedButton(
                                            onClick = { showDeliveryDateStartPicker = true },
                                            shape = RoundedCornerShape(24.dp),
                                            border = BorderStroke(1.dp, TropicTurquoise),
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                                        ) {
                                            val text = tmpDeliveryDateStart.takeIf { it.isNotBlank() }?.let {
                                                try { java.time.LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                                            } ?: "от"
                                            Text(text, color = if (tmpDeliveryDateStart.isBlank()) Color.Gray else TropicTurquoise)
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showDeliveryDateEndPicker = true },
                                            shape = RoundedCornerShape(24.dp),
                                            border = BorderStroke(1.dp, TropicTurquoise),
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
                                        ) {
                                            val text = tmpDeliveryDateEnd.takeIf { it.isNotBlank() }?.let {
                                                try { java.time.LocalDate.parse(it).format(displayFormatter) } catch (_: Exception) { it }
                                            } ?: "до"
                                            Text(text, color = if (tmpDeliveryDateEnd.isBlank()) Color.Gray else TropicTurquoise)
                                        }
                                    }
                                }
                                if (showDeliveryDateStartPicker) {
                                    DatePickerDialog(
                                        initialDate = tmpDeliveryDateStart,
                                        onDateSelected = { tmpDeliveryDateStart = it; showDeliveryDateStartPicker = false },
                                        onDismiss = { showDeliveryDateStartPicker = false }
                                    )
                                }
                                if (showDeliveryDateEndPicker) {
                                    DatePickerDialog(
                                        initialDate = tmpDeliveryDateEnd,
                                        onDateSelected = { tmpDeliveryDateEnd = it; showDeliveryDateEndPicker = false },
                                        onDismiss = { showDeliveryDateEndPicker = false }
                                    )
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(onClick = {
                                    tmpFeedTypeId = null
                                    tmpOrderDateStart = ""
                                    tmpOrderDateEnd = ""
                                    tmpQuantityMin = ""
                                    tmpQuantityMax = ""
                                    tmpPriceMin = ""
                                    tmpPriceMax = ""
                                    tmpDeliveryDateStart = ""
                                    tmpDeliveryDateEnd = ""
                                    tmpSupplierId = null
                                    tmpFeedItemId = null
                                    tmpActualOnly = false
                                    filterFeedTypeId = null
                                    filterOrderDateStart = ""
                                    filterOrderDateEnd = ""
                                    filterQuantityMin = ""
                                    filterQuantityMax = ""
                                    filterPriceMin = ""
                                    filterPriceMax = ""
                                    filterDeliveryDateStart = ""
                                    filterDeliveryDateEnd = ""
                                    filterSupplierId = null
                                    filterFeedItemId = null
                                    filterActualOnly = false
                                    showFilterSheet = false
                                }) { Text("Сбросить фильтры", color = TropicGreen) }
                                Button(
                                    onClick = {
                                        filterFeedTypeId = tmpFeedTypeId
                                        filterOrderDateStart = tmpOrderDateStart
                                        filterOrderDateEnd = tmpOrderDateEnd
                                        filterQuantityMin = tmpQuantityMin
                                        filterQuantityMax = tmpQuantityMax
                                        filterPriceMin = tmpPriceMin
                                        filterPriceMax = tmpPriceMax
                                        filterDeliveryDateStart = tmpDeliveryDateStart
                                        filterDeliveryDateEnd = tmpDeliveryDateEnd
                                        filterSupplierId = tmpSupplierId
                                        filterFeedItemId = tmpFeedItemId
                                        filterActualOnly = tmpActualOnly
                                        showFilterSheet = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                                ) { Text("Применить", color = Color.White) }
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
                        val itemsList = when (selectedTab) {
                            SuppliesTab.SUPPLIERS -> supplies
                            SuppliesTab.ORDERS -> feedOrders
                        }.let { list ->
                            if (selectedTab == SuppliesTab.ORDERS) {
                                val filtered = list.filter { item ->
                                    val order = item as? FeedOrderItem
                                    if (order != null) {
                                        (filterSupplierId == null || order.feed_supplier_id == filterSupplierId) &&
                                        (filterFeedItemId == null || order.feed_item_id == filterFeedItemId) &&
                                        (!filterActualOnly || order.delivery_date == null)
                                    } else true
                                }
                                // --- Сортировка заказов ---
                                val sorted = when (sortField) {
                                    "price" -> if (sortDir == "asc") filtered.sortedBy { (it as FeedOrderItem).price } else filtered.sortedByDescending { (it as FeedOrderItem).price }
                                    "ordered_quantity" -> if (sortDir == "asc") filtered.sortedBy { (it as FeedOrderItem).ordered_quantity } else filtered.sortedByDescending { (it as FeedOrderItem).ordered_quantity }
                                    else -> filtered
                                }
                                sorted
                            } else list
                        }
                        if (itemsList.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(if (selectedTab == SuppliesTab.ORDERS) "Нет заказов" else "Нет данных", color = TropicTurquoise)
                                }
                            }
                        } else {
                            items(itemsList) { item ->
                                if (selectedTab == SuppliesTab.SUPPLIERS) {
                                    val supplier = item as? SuppliesItem
                                    SupplyUnifiedCard(item) {
                                        supplier?.let {
                                            filterSupplierId = it.id
                                            selectedTab = SuppliesTab.ORDERS
                                        }
                                    }
                                } else {
                                    SupplyUnifiedCard(item)
                                }
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SupplyUnifiedCard(item: Any, onClick: (() -> Unit)? = null) {
    val isSupplier = item is SuppliesItem
    val isOrder = item is FeedOrderItem
    val decimalFormat = remember { DecimalFormat("#") }
    val supplies = LocalSupplies.current
    val feedItems = LocalFeedItems.current

    // --- DEBUG LOGS ---
    if (isOrder) {
        val order = item as FeedOrderItem
        Log.d("SupplyDebug", "supplies: ${supplies.map { it.id to it.name }}")
        Log.d("SupplyDebug", "feedItems: ${feedItems.map { it.id to it.name }}")
        Log.d("SupplyDebug", "order.feed_supplier_id: ${order.feed_supplier_id}, order.feed_item_id: ${order.feed_item_id}")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .let { if (onClick != null) it.clickable { onClick() } else it },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box {
            Row(
                Modifier
                    .height(IntrinsicSize.Min)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.White, Color(0xFFfffcf6), Color(0xFFfdf8ed)),
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
                    if (isSupplier) {
                        val supplier = item as SuppliesItem
                        Text(supplier.name, fontWeight = FontWeight.Bold, color = TropicOnBackground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Телефон: ${supplier.phone ?: "-"}", color = TropicOnBackground)
                        Text("Адрес: ${supplier.address ?: "-"}", color = TropicOnBackground)
                        Text("Кол-во заказов: ${supplier.order_count ?: "-"}", color = TropicOnBackground)
                        Text("Общий объем: ${supplier.total_ordered_quantity?.let { decimalFormat.format(it) } ?: "-"} кг", color = TropicOnBackground)
                        Text("Средняя цена: ${supplier.avg_price?.let { decimalFormat.format(it) } ?: "-"}", color = TropicGreen)
                    } else if (isOrder) {
                        val order = item as FeedOrderItem
                        val supplierName = supplies.firstOrNull { it.id == order.feed_supplier_id }?.name ?: order.feed_supplier_id.toString()
                        val feedName = feedItems.firstOrNull { it.id == order.feed_item_id }?.name ?: order.feed_item_id.toString()
                        Text(feedName, fontWeight = FontWeight.Bold, color = TropicOnBackground, maxLines = 1)
                        Text("Поставщик: $supplierName", color = TropicOnBackground)
                        Text("Объем: ${decimalFormat.format(order.ordered_quantity)} кг", color = TropicOnBackground)
                        Text("Дата заказа: ${order.order_date}", color = TropicOnBackground)
                        Text("Дата поставки: ${order.delivery_date ?: "-"}", color = TropicOnBackground)
                        Text("Цена: ${decimalFormat.format(order.price)}", color = TropicGreen)
                        Text("Статус: ${order.status}", color = TropicTurquoise)
                    }
                }
            }
            Icon(
                imageVector = Icons.Filled.Handshake,
                contentDescription = null,
                tint = Color(0xFFece2cb),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 14.dp)
            )
        }
    }
} 