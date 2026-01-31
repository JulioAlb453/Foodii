package com.example.foodii.feature.planner.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.primaryLight
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.presentation.components.MealCard
import com.example.foodii.feature.planner.presentation.components.ScheduleMealDialog
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModel
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    factory: MealDetailsViewModelFactory,
    onBackPressed: () -> Unit
) {
    val viewModel: MealDetailsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var selectedMeal by remember { mutableStateOf<MealDetail?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = primaryLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seleccionar Comida") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.meals) { meal ->
                        MealCard(
                            name = meal.name,
                            imageUrl = meal.imageUrl,
                            onClick = {
                                selectedMeal = meal
                                showDatePicker = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker && selectedMeal != null) {
        ScheduleMealDialog(
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                selectedMeal?.let { viewModel.onPlanMealSelected(it, millis) }
                showDatePicker = false
            }
        )
    }
}
