package com.example.foodii.feature.foods.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.backgroundLight
import com.example.compose.onPrimaryLight
import com.example.compose.onSecondaryLight
import com.example.compose.primaryLight
import com.example.compose.secondaryLight
import com.example.compose.tertiaryLight
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
    onViewPlannerClick: () -> Unit
) {
    val viewModel: MealDetailsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = backgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight
                ),
                title = { Text("Seleccionar Comida", fontWeight = FontWeight.Bold, color = onPrimaryLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = onPrimaryLight)
                    }
                },
                actions = {
                    IconButton(onClick = onViewPlannerClick, colors = IconButtonDefaults.iconButtonColors(
                        contentColor = onPrimaryLight
                    )   ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Ver Agenda",

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
                        verticalArrangement = Arrangement.spacedBy(8.dp),

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
