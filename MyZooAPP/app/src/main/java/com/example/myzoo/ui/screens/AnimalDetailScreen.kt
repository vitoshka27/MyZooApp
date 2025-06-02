package com.example.myzoo.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailScreen(
    animalId: Int,
    viewModel: AnimalDetailViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val animal by viewModel.animal.collectAsState()

    LaunchedEffect(animalId) {
        viewModel.loadAnimal(animalId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали животного") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.Pets, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            if (animal == null) {
                Text("Нет данных")
            } else {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Имя: ${animal!!.name}", style = MaterialTheme.typography.titleLarge)
                    Text(text = "ID: ${animal!!.id}")
                    Text(text = "Вид: ${animal!!.species_id}")
                    Text(text = "Пол: ${animal!!.gender ?: "-"}")
                    Text(text = "Дата рождения: ${animal!!.birth_date ?: "-"}")
                    // ... другие поля по необходимости
                }
            }
        }
    }
} 
 
 
 
 
 
 
 
 
 
 
 
 