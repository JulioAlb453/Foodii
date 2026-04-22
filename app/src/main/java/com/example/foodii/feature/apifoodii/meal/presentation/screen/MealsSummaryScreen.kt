package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.FoodiiTheme
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.presentation.components.DailySummaryItem
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsSummaryScreen(
    viewModel: MealFoodiiViewModel,
    userId: String,
    onBackPressed: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var mealToProcess by remember { mutableStateOf<FoodiiMeal?>(null) }
    val datePickerState = rememberDatePickerState()

    FoodiiTheme(darkTheme = false, dynamicColor = false) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val summaries by viewModel.summaries.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            val start = LocalDate.now().minusMonths(1).toString()
            val end = LocalDate.now().plusYears(1).toString()
            viewModel.loadMealsRange(userId, start, end)
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { 
                    showDatePicker = false
                    mealToProcess = null
                },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            mealToProcess?.let { meal ->
                                viewModel.scheduleMealReminder(meal, millis)
                            }
                        }
                        showDatePicker = false
                        mealToProcess = null
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDatePicker = false
                        mealToProcess = null
                    }) {
                        Text("Cancelar")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    mealToProcess = null
                },
                title = { Text("Eliminar agendación") },
                text = { Text("¿Estás seguro de que deseas eliminar esta comida de tu agenda? Esta acción no eliminará el platillo de tu lista de creados.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mealToProcess?.let { viewModel.deletePlannedMeal(it) }
                            showDeleteDialog = false
                            mealToProcess = null
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showDeleteDialog = false
                        mealToProcess = null
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = { Text("Mi Menú Semanal", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (uiState.isLoading && summaries.isEmpty()) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else if (summaries.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No hay comidas agendadas",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(summaries) { summary ->
                            DailySummaryItem(
                                summary = summary,
                                onMealClick = { meal ->
                                    onNavigateToDetail(meal.id)
                                },
                                onRescheduleClick = { meal ->
                                    mealToProcess = meal
                                    showDatePicker = true
                                },
                                onDeleteClick = { meal ->
                                    mealToProcess = meal
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }

                if (uiState.isLoading && summaries.isNotEmpty()) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.error?.let { error ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                        action = {
                            TextButton(onClick = { viewModel.clearError() }) {
                                Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    ) {
                        Text(text = error)
                    }
                }
            }
        }
    }
}
