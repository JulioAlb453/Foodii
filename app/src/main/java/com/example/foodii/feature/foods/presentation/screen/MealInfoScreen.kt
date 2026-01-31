package com.example.foodii.feature.foods.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.backgroundLight
import com.example.compose.onPrimaryContainerLight
import com.example.compose.onPrimaryLight
import com.example.compose.primaryContainerDark
import com.example.compose.primaryContainerLight
import com.example.compose.primaryLight
import com.example.compose.secondaryLight
import com.example.compose.tertiaryDark
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
        containerColor = backgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight
                ),
                title = { Text("Detalle de Receta", fontWeight = FontWeight.Bold, color = onPrimaryLight) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = onPrimaryLight)
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Agendar", tint = onPrimaryLight)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDatePicker = true },
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                text = { Text("Agendar Comida") },
                containerColor = primaryContainerLight,
                contentColor = onPrimaryContainerLight,
                modifier = Modifier.padding(15.dp)
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
