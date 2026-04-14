package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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


    LaunchedEffect(Unit) {
        viewModel.loadAllMeals(userId)
    }

    LaunchedEffect(allMeals) {
        if (allMeals.isNotEmpty()) {
            viewModel.loadRandomMeal(userId)
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
                            }
                        }
                    }
                }
                else -> Text(uiState.error ?: "No tienes comidas registradas")
            }
        }
    }
}
