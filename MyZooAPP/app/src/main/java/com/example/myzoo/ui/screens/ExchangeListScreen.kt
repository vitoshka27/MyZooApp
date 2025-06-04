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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.key
import kotlin.Comparator

enum class ExchangeTab { EXCHANGE, PARTNERS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeListScreen(viewModel: ExchangeListViewModel = viewModel()) {
    val exchanges by viewModel.exchanges.collectAsState()
    val species by viewModel.species.collectAsState()
    var selectedTab by remember { mutableStateOf(ExchangeTab.PARTNERS) }
    var burgerMenuPopup by remember { mutableStateOf(false) }
    var burgerButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var burgerButtonHeight by remember { mutableStateOf(0) }
    var burgerButtonWidth by remember { mutableStateOf(0) }
    var filterSheetOpen by remember { mutableStateOf(false) }
    var sortMenuOpen by remember { mutableStateOf(false) }
    var sortButtonOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var sortButtonHeight by remember { mutableStateOf(0) }
    var sortButtonWidth by remember { mutableStateOf(0) }
    var zooExchanges by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoadingZooExchanges by remember { mutableStateOf(false) }
    var animalShorts by remember { mutableStateOf<List<com.example.myzoo.data.remote.AnimalShortDto>>(emptyList()) }
    var isLoadingAnimals by remember { mutableStateOf(false) }
    var animalIdToSpeciesId by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var isLoadingAnimalFull by remember { mutableStateOf(false) }
    // --- Сортировка и фильтры для Партнеров ---
    var partnerSortField by remember { mutableStateOf(viewModel.getSortField()) }
    var partnerSortDir by remember { mutableStateOf(viewModel.getSortDir()) }
    var partnerFilterSpecies by remember { mutableStateOf(viewModel.getFilterSpecies()) }
    var tmpPartnerFilterSpecies by remember { mutableStateOf(partnerFilterSpecies) }
    // --- Сортировка и фильтры для Обменов ---
    var exchangeSortField by remember { mutableStateOf("animal_name") }
    var exchangeSortDir by remember { mutableStateOf("asc") }
    var filterSpeciesId by remember { mutableStateOf<Int?>(null) }
    var tmpFilterSpeciesId by remember { mutableStateOf(filterSpeciesId) }
    var filterPartnerZoo by remember { mutableStateOf<String?>(null) }
    var tmpFilterPartnerZoo by remember { mutableStateOf(filterPartnerZoo) }

    LaunchedEffect(selectedTab) {
        if (selectedTab == ExchangeTab.EXCHANGE) {
            isLoadingZooExchanges = true
            isLoadingAnimals = true
            isLoadingAnimalFull = true
            try {
                zooExchanges = com.example.myzoo.data.remote.ApiModule.getZooExchanges()
                animalShorts = com.example.myzoo.data.remote.ApiModule.getAnimalsShort()
                val animalFull = com.example.myzoo.data.remote.ApiModule.zooApi.getAnimals().data
                animalIdToSpeciesId = animalFull.associate { it.id to it.species_id }
            } catch (_: Exception) {
                zooExchanges = emptyList()
                animalShorts = emptyList()
                animalIdToSpeciesId = emptyMap()
            }
            isLoadingZooExchanges = false
            isLoadingAnimals = false
            isLoadingAnimalFull = false
        }
    }

    // Для Партнеров
    LaunchedEffect(partnerFilterSpecies, partnerSortField, partnerSortDir) {
        if (selectedTab == ExchangeTab.PARTNERS) {
            viewModel.setFilterSpecies(partnerFilterSpecies)
            viewModel.setSortField(partnerSortField)
            viewModel.setSortDir(partnerSortDir)
            viewModel.loadExchanges(partnerFilterSpecies, partnerSortField, partnerSortDir)
        }
    }

    // --- Фильтрация zooExchanges ---
    val filteredZooExchanges = zooExchanges.filter { item ->
        val animalId = (item["animal_id"] as? Number)?.toInt()
        val speciesOk = filterSpeciesId == null || (animalId != null && animalIdToSpeciesId[animalId] == filterSpeciesId)
        val partnerOk = filterPartnerZoo == null || (item["partner_zoo"] as? String == filterPartnerZoo)
        speciesOk && partnerOk
    }
    // --- Сортировка zooExchanges ---
    val sortedZooExchanges = remember(filteredZooExchanges, exchangeSortField, exchangeSortDir, animalShorts) {
        val base = filteredZooExchanges.sortedWith(
            Comparator { a, b ->
                val v1 = when (exchangeSortField) {
                    "animal_name" -> animalShorts.firstOrNull { s -> s.id == (a["animal_id"] as? Number)?.toInt() }?.name
                    "exchange_date" -> (a["date"] as? String ?: a["exchange_date"] as? String)
                    else -> a[exchangeSortField]
                } as? Comparable<Any>
                val v2 = when (exchangeSortField) {
                    "animal_name" -> animalShorts.firstOrNull { s -> s.id == (b["animal_id"] as? Number)?.toInt() }?.name
                    "exchange_date" -> (b["date"] as? String ?: b["exchange_date"] as? String)
                    else -> b[exchangeSortField]
                } as? Comparable<Any>
                when {
                    v1 == null && v2 == null -> 0
                    v1 == null -> -1
                    v2 == null -> 1
                    else -> v1.compareTo(v2)
                }
            }
        )
        if (exchangeSortDir == "desc") base.reversed() else base
    }

    // --- Сортировка exchanges для Партнеров ---
    val sortedExchanges = remember(exchanges, partnerSortField, partnerSortDir) {
        val base = exchanges.sortedWith(
            Comparator { a, b ->
                val v1 = when (partnerSortField) {
                    "partner_zoo" -> a.partner_zoo
                    "exchange_count" -> a.exchange_count
                    else -> null
                } as? Comparable<Any>
                val v2 = when (partnerSortField) {
                    "partner_zoo" -> b.partner_zoo
                    "exchange_count" -> b.exchange_count
                    else -> null
                } as? Comparable<Any>
                when {
                    v1 == null && v2 == null -> 0
                    v1 == null -> -1
                    v2 == null -> 1
                    else -> v1.compareTo(v2)
                }
            }
        )
        if (partnerSortDir == "desc") base.reversed() else base
    }

    // --- закрывать Popup при смене вкладки ---
    LaunchedEffect(selectedTab) {
        sortMenuOpen = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(TropicBackground)
            .onGloballyPositioned { coords ->
                val windowPos = coords.localToWindow(androidx.compose.ui.geometry.Offset.Zero)
            }
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
                                            listOf(
                                                ExchangeTab.EXCHANGE to "Обмен",
                                                ExchangeTab.PARTNERS to "Партнеры"
                                            ).forEach { (tab, label) ->
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
                        text = "Обмен с зоопарками",
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
                    ExchangeTab.PARTNERS -> exchanges.firstOrNull()?.total_zoos ?: 0
                    ExchangeTab.EXCHANGE -> zooExchanges.firstOrNull()?.get("total") as? Int ?: filteredZooExchanges.size
                }
                Text(
                    text = when (selectedTab) {
                        ExchangeTab.PARTNERS -> "Всего зоопарков: $total"
                        ExchangeTab.EXCHANGE -> "Всего обменов: $total"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TropicGreen
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    text = when (selectedTab) {
                        ExchangeTab.PARTNERS -> "Партнеры"
                        ExchangeTab.EXCHANGE -> "Обмен"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TropicTurquoise
                )
            }
            // --- Полоска фильтров/сортировки ---
            key(selectedTab) {
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
                                    sortButtonWidth = coords.size.width
                                }
                        ) {
                            Text(
                                when (selectedTab) {
                                    ExchangeTab.EXCHANGE -> when (exchangeSortField) {
                                        "animal_name" -> "Имя"
                                        "exchange_date" -> "Дата"
                                        else -> exchangeSortField
                                    }
                                    ExchangeTab.PARTNERS -> when (partnerSortField) {
                                        "partner_zoo" -> "Имя"
                                        "exchange_count" -> "Кол-во обменов"
                                        else -> partnerSortField
                                    }
                                },
                                color = TropicTurquoise
                            )
                        }
                        IconButton(
                            onClick = {
                                if (selectedTab == ExchangeTab.PARTNERS) {
                                    partnerSortDir = if (partnerSortDir == "asc") "desc" else "asc"
                                } else {
                                    exchangeSortDir = if (exchangeSortDir == "asc") "desc" else "asc"
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, shape = RoundedCornerShape(50))
                        ) {
                            Icon(
                                if ((if (selectedTab == ExchangeTab.PARTNERS) partnerSortDir else exchangeSortDir) == "asc") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = "Сменить направление сортировки",
                                tint = TropicTurquoise,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    if (sortMenuOpen && selectedTab == ExchangeTab.EXCHANGE) {
                        Popup(
                            alignment = Alignment.TopStart,
                            offset = IntOffset(
                                sortButtonOffset.x.roundToInt(),
                                (sortButtonOffset.y + sortButtonHeight).roundToInt()
                            ),
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
                                    listOf("animal_name" to "Имя животного", "exchange_date" to "Дата").forEach { (field, label) ->
                                        val isSelected = exchangeSortField == field
                                        Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                                .clickable {
                                                    exchangeSortField = field
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
            }
            // --- Список обменов ---
            Box(Modifier.weight(1f)) {
                when (selectedTab) {
                    ExchangeTab.PARTNERS -> {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(top = 0.dp),
                            contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                        ) {
                            if (sortedExchanges.isEmpty()) {
                                item {
                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Нет данных")
                                    }
                                }
                            } else {
                                items(sortedExchanges) { item ->
                                    ExchangeCard(item) {
                                        selectedTab = ExchangeTab.EXCHANGE
                                        filterPartnerZoo = item.partner_zoo
                                    }
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                    ExchangeTab.EXCHANGE -> {
                        if (isLoadingZooExchanges || isLoadingAnimals || isLoadingAnimalFull) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = TropicTurquoise)
                            }
                        } else if (filteredZooExchanges.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Нет обменов", color = TropicTurquoise)
                            }
                        } else {
                            LazyColumn(
                                Modifier.fillMaxSize().padding(top = 0.dp),
                                contentPadding = PaddingValues(top = 0.dp, bottom = 16.dp)
                            ) {
                                items(sortedZooExchanges) { item ->
                                    val animalId = (item["animal_id"] as? Number)?.toInt()
                                    val animalName = animalShorts.firstOrNull { it.id == animalId }?.name
                                    ExchangeZooCard(item, animalName)
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        // Показываем фильтры только если filterSheetOpen == true
        if (filterSheetOpen && selectedTab == ExchangeTab.EXCHANGE) {
            ModalBottomSheet(
                onDismissRequest = { filterSheetOpen = false },
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
                    FilterRow(label = "Вид животного", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = species.map { it.id to it.type_name },
                            selected = tmpFilterSpeciesId,
                            onSelected = { tmpFilterSpeciesId = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(12.dp))
                    FilterRow(label = "Партнер", content = {
                        val partnerOptions = zooExchanges.mapNotNull { it["partner_zoo"] as? String }.distinct().sorted()
                        DropdownSelector(
                            label = "Не выбрано",
                            options = partnerOptions.map { it to it },
                            selected = tmpFilterPartnerZoo,
                            onSelected = { tmpFilterPartnerZoo = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            tmpFilterSpeciesId = null
                            tmpFilterPartnerZoo = null
                            filterSpeciesId = null
                            filterPartnerZoo = null
                            filterSheetOpen = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = {
                                filterSpeciesId = tmpFilterSpeciesId
                                filterPartnerZoo = tmpFilterPartnerZoo
                                filterSheetOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
        // Фильтры и сортировка для Партнеров
        if (filterSheetOpen && selectedTab == ExchangeTab.PARTNERS) {
            ModalBottomSheet(
                onDismissRequest = { filterSheetOpen = false },
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
                    FilterRow(label = "Вид", content = {
                        DropdownSelector(
                            label = "Не выбрано",
                            options = species.map { it.id to it.type_name },
                            selected = tmpPartnerFilterSpecies,
                            onSelected = { tmpPartnerFilterSpecies = it },
                            width = 220.dp
                        )
                    })
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            tmpPartnerFilterSpecies = null
                            partnerFilterSpecies = null
                            filterSheetOpen = false
                        }) { Text("Сбросить фильтры", color = TropicGreen) }
                        Button(
                            onClick = {
                                partnerFilterSpecies = tmpPartnerFilterSpecies
                                filterSheetOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TropicTurquoise)
                        ) { Text("Применить", color = Color.White) }
                    }
                }
            }
        }
        if (sortMenuOpen && selectedTab == ExchangeTab.PARTNERS) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(
                    sortButtonOffset.x.roundToInt(),
                    (sortButtonOffset.y + sortButtonHeight).roundToInt()
                ),
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
                            val isSelected = partnerSortField == field
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) TropicLime.copy(alpha = 0.18f) else Color.Transparent)
                                    .clickable {
                                        partnerSortField = field
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
}

@Composable
fun ExchangeCard(item: ExchangeItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
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

@Composable
fun ExchangeZooCard(item: Map<String, Any?>, animalName: String?) {
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
                Text(animalName ?: (item["animal_id"] ?: "-").toString(), fontWeight = FontWeight.Bold, color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Партнер: ${item["partner_zoo"] ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                Text("Дата: ${item["date"] ?: item["exchange_date"] ?: "-"}", color = TropicOnBackground, style = MaterialTheme.typography.bodyLarge)
                if (item["exchange_type"] != null) {
                    Text("Тип: ${item["exchange_type"]}", color = TropicGreen, style = MaterialTheme.typography.bodyMedium)
                }
                if (item["status"] != null) {
                    Text("Статус: ${item["status"]}", color = TropicTurquoise, style = MaterialTheme.typography.bodyMedium)
                }
            }
            // Правая часть — иконка, как у ExchangeCard
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(80.dp)
            ) {
                Image(
                    painter = painterResource(id = com.example.myzoo.R.drawable.ic_business_messages),
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