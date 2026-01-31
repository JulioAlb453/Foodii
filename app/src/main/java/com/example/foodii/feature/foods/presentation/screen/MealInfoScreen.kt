package com.example.foodii.feature.foods.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.presentation.screen.components.MealInfoCard
import com.example.foodii.feature.planner.presentation.screen.components.ScheduleMealDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealInfoScreen(
    meal: MealDetail,
    onBackPressed: () -> Unit,
    onScheduleMeal: (Long) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle de Receta", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón para agendar directamente desde el detalle
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Agendar")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDatePicker = true },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                text = { Text("Agendar Comida") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { padding ->
        MealInfoCard(
            meal = meal,
            modifier = Modifier.padding(padding)
        )
    }

    if (showDatePicker) {
        ScheduleMealDialog(
            onDismiss = { showDatePicker = false },
            onConfirm = { millis ->
                onScheduleMeal(millis)
                showDatePicker = false
            }
        )
    }
}
