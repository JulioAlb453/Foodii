package com.example.foodii.feature.foods.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.presentation.screen.components.MealCard
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModel
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    factory: MealDetailsViewModelFactory,
    onBackPressed: () -> Unit,
    onMealClick: (MealDetail) -> Unit,
    onViewPlannerClick: () -> Unit // Nueva acción para ir a la agenda
) {
    val viewModel: MealDetailsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Seleccionar Comida", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para ir a ver las comidas agendadas
                    IconButton(onClick = onViewPlannerClick) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Ver Agenda",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> Text(
                    "Error: ${uiState.error}",
                    Modifier.align(Alignment.Center),
                    color = Color.Red
                )
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.meals) { meal ->
                            MealCard(
                                name = meal.name,
                                imageUrl = meal.imageUrl,
                                onClick = { onMealClick(meal) }
                            )
                        }
                    }
                }
            }
        }
    }
}
