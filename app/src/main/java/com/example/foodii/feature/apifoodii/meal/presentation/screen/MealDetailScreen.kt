package com.example.foodii.feature.apifoodii.meal.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.compose.*
import com.example.foodii.core.utils.toFullImageUrl
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import com.example.ui.theme.TypographyFoodii
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(
    viewModel: MealFoodiiViewModel,
    mealId: String,
    userId: String,
    onBackPressed: () -> Unit
) {
    val meal by viewModel.selectedMeal.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(mealId) {
        viewModel.loadMealDetail(mealId, userId)
    }

    val imageUrl = remember(meal?.image) { meal?.image.toFullImageUrl() }

    Scaffold(
        containerColor = backgroundLight,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Detalle del Platillo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            if (meal != null) {
                MealScheduleComponent(
                    shakeDetector = viewModel.shakeDetector,
                    onDateSelected = { dateMillis ->
                        viewModel.scheduleMealReminder(meal!!, dateMillis)
                        scope.launch {
                            snackbarHostState.showSnackbar("Comida agendada correctamente")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = primaryLight
                )
            } else if (meal == null) {
                Text(
                    text = uiState.error ?: "No se pudo cargar la información",
                    modifier = Modifier.align(Alignment.Center),
                    color = errorLight
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            if (imageUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Meal Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    onState = { state ->
                                        if (state is AsyncImagePainter.State.Error) {
                                            Log.e("IMAGE_DEBUG", "Fallo al cargar: $imageUrl")
                                        }
                                    }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = primaryLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = meal!!.name,
                                    style = TypographyFoodii.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = onPrimaryLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = null, tint = onPrimaryLight, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    val timeText = meal!!.mealTime.name.lowercase().replaceFirstChar { it.uppercase() }
                                    Text(
                                        text = timeText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = onPrimaryLight
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceVariantLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = primaryLight,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Restaurant,
                                        contentDescription = null,
                                        modifier = Modifier.padding(12.dp),
                                        tint = onPrimaryLight
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        "Aporte Energético Total",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = onSurfaceVariantLight
                                    )
                                    Text(
                                        "${meal!!.totalCalories.toInt()} kcal",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = primaryLight
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Ingredientes usados",
                            style = TypographyFoodii.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp),
                            color = onBackgroundLight
                        )
                    }

                    items(meal!!.ingredients) { ingredient ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = surfaceContainerLight),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ingredient.name,
                                        style = TypographyFoodii.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = onSurfaceLight
                                    )
                                    Text(
                                        text = "Cantidad: ${ingredient.amount}g",
                                        style = TypographyFoodii.bodySmall,
                                        color = outlineLight
                                    )
                                }
                                Text(
                                    text = "${ingredient.calories.toInt()} kcal",
                                    style = TypographyFoodii.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = secondaryLight
                                )
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
