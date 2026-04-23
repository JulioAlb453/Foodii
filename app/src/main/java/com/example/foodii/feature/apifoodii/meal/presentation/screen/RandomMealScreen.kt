package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.foodii.core.utils.toFullImageUrl
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomMealScreen(
    viewModel: MealFoodiiViewModel,
    userId: String,
    onBackPressed: () -> Unit
) {
    val allMeals by viewModel.allMeals.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(Unit) {
        viewModel.loadAllMeals(userId)
    }

    LaunchedEffect(allMeals) {
        if (allMeals.isNotEmpty()) {
            viewModel.loadRandomMeal(userId)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        uiState.randomMeal?.let { meal ->
                            viewModel.scheduleMealReminder(meal, millis)
                        }
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sugerencia del Día", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.randomMeal != null -> {
                    val meal = uiState.randomMeal!!
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = meal.image.toFullImageUrl(),
                                contentDescription = meal.name,
                                modifier = Modifier.fillMaxWidth().height(220.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(meal.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${meal.totalCalories.toInt()} kcal", style = MaterialTheme.typography.bodyLarge)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Button(
                                    onClick = { showDatePicker = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agendar esta comida")
                                }
                                
                                OutlinedButton(
                                    onClick = { viewModel.loadRandomMeal(userId) },
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("Sugerir otra")
                                }
                            }
                        }
                    }
                }
                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error ?: "No tienes comidas registradas")
                        Button(onClick = onBackPressed, modifier = Modifier.padding(top = 16.dp)) {
                            Text("Volver")
                        }
                    }
                }
            }
        }
    }
}
