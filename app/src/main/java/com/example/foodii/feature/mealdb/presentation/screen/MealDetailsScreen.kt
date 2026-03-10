package com.example.foodii.feature.mealdb.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.compose.*
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.foodii.feature.mealdb.presentation.components.MealCard
import com.example.foodii.feature.mealdb.presentation.components.ScheduleMealDialog
import com.example.foodii.feature.mealdb.presentation.viewmodel.MealDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    onBackPressed: () -> Unit,
    onNavigateToPlanner: () -> Unit
) {
    val viewModel: MealDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var selectedMeal by remember { mutableStateOf<MealDetail?>(null) }
    var showDetails by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Explorar Recetas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToPlanner) {
                        Icon(Icons.Default.EventNote, contentDescription = "Ver Agenda")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryLight
                )
            } else if (uiState.error != null) {
                Text(
                    text = "Error: ${uiState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    color = errorLight
                )
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
                                showDetails = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de Detalles
    if (showDetails && selectedMeal != null) {
        Dialog(onDismissRequest = { showDetails = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = surfaceLight)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = selectedMeal!!.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = selectedMeal!!.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Instrucciones:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = onSurfaceVariantLight
                        )
                        Text(
                            text = selectedMeal!!.instructions,
                            style = MaterialTheme.typography.bodyMedium,
                            color = onSurfaceLight
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showDetails = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = primaryLight)
                        ) {
                            Text("Cerrar")
                        }
                        Button(
                            onClick = {
                                showDetails = false
                                showDatePicker = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryLight,
                                contentColor = onPrimaryLight
                            )
                        ) {
                            Text("Agendar Comida")
                        }
                    }
                }
            }
        }
    }

    // Diálogo de Calendario
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
