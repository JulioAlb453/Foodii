package com.example.foodii.feature.foods.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.foodii.feature.foods.presentation.viewmodel.MealDetailsViewModel
import com.example.foodii.feature.foods.presentation.viewmodel.MealDetailsViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    factory: MealDetailsViewModelFactory
) {
    val viewModel: MealDetailsViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Recetas", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                uiState.error != null -> Text("Error: ${uiState.error}", Modifier.align(Alignment.Center), color = Color.Red)
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(uiState.meals) { meal ->
                            AsyncImage(
                                model = meal.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth().height(200.dp).clip(
                                    RoundedCornerShape(12.dp)
                                ),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = meal.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(text = "Instrucciones", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = meal.instructions, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

