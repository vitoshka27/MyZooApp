package com.example.myzoo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myzoo.data.remote.AnimalMenuItem
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalMenuScreen(viewModel: AnimalMenuViewModel = viewModel(), onAnimalClick: (AnimalMenuItem) -> Unit = {}) {
    val animalMenu by viewModel.animalMenu.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAnimalMenu() // Можно передать параметры фильтрации
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Меню кормления животных") })
        }
    ) { padding ->
        if (animalMenu.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Нет данных")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(animalMenu) { item ->
                    AnimalMenuItemCard(item, onClick = { onAnimalClick(item) })
                }
            }
        }
    }
}

@Composable
fun AnimalMenuItemCard(item: AnimalMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Имя: ${item.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Вид: ${item.species ?: "-"}")
            Text(text = "Клетка: ${item.enclosure ?: "-"}")
            Text(text = "Возраст: ${item.age ?: "-"}")
            Text(text = "Тип корма: ${item.feed_type ?: "-"}")
            Text(text = "Сезон: ${item.season ?: "-"}")
            Text(text = "Группа: ${item.age_group ?: "-"}")
        }
    }
} 
 
 
 
 
 
 
 
 
 
 
 
 