package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.backgroundLight
import com.example.compose.onPrimaryLight
import com.example.compose.primaryLight
import com.example.foodii.feature.apifoodii.meal.presentation.components.DailySummaryItem
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsSummaryScreen(
    viewModel: MealFoodiiViewModel,
    userId: String,
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val summaries by viewModel.summaries.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMealsRange(userId, "2023-01-01", "2025-12-31")
    }

    Scaffold(
        containerColor = backgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Mi Menú Semanal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading && summaries.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (summaries.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No hay comidas agendadas", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(summaries) { summary ->
                        DailySummaryItem(
                            summary = summary,
                            onMealClick = { /* Navegar al detalle si es necesario */ }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Cerrar", color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                ) {
                    Text(text = error)
                }
            }
        }
    }
}
