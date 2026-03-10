package com.example.foodii.feature.mealdb.presentation.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.compose.backgroundLight
import com.example.compose.onPrimaryLight
import com.example.compose.onSurfaceLight
import com.example.compose.outlineLight
import com.example.compose.primaryLight
import com.example.compose.secondaryLight
import com.example.compose.surfaceLight
import com.example.foodii.feature.mealdb.data.local.entity.PlannedMealEntity
import com.example.foodii.feature.mealdb.presentation.components.MealCard
import com.example.foodii.feature.mealdb.presentation.viewmodel.PlannerViewModel
import com.example.ui.theme.TypographyFoodii
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel = hiltViewModel(),
    onBackPressed: () -> Unit
) {
    val meals by viewModel.plannedMeals.collectAsState()

    Scaffold(
        containerColor = backgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryLight,
                    titleContentColor = onPrimaryLight,
                    navigationIconContentColor = onPrimaryLight
                ),
                title = { Text("Mi Agenda de Comidas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, 
                            contentDescription = "Volver",
                            tint = onPrimaryLight
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (meals.isEmpty()) {
            EmptyPlannerView(modifier = Modifier.padding(padding))
        } else {
            val sortedMeals = meals.sortedBy { it.date }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedMeals) { planned ->
                    PlannedMealItem(planned)
                }
            }
        }
    }
}

@Composable
fun PlannedMealItem(planned: PlannedMealEntity) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        DateHeader(planned.date)
        
        Card(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = CardDefaults.cardColors(
                containerColor = surfaceLight
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                MealCard(
                    name = planned.name,
                    imageUrl = planned.imageUrl,
                    onClick = { expanded = !expanded }
                )

                AnimatedVisibility(visible = expanded) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                        Text(
                            text = "Instrucciones:",
                            style = TypographyFoodii.bodySmall,
                            color = primaryLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = planned.instructions,
                            style = TypographyFoodii.bodyMedium,
                            color = onSurfaceLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(millis: Long) {
    val dateStr = formatMillisToDate(millis)
    Surface(
        color = secondaryLight,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .padding(start = 160.dp, bottom = 20.dp)
            .wrapContentSize()
    ) {
        Text(
            text = dateStr,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            style = TypographyFoodii.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = onPrimaryLight,
        )
    }
}

@Composable
fun EmptyPlannerView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.RestaurantMenu,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = outlineLight
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Tu agenda está vacía",
            style = TypographyFoodii.headlineMedium,
            color = outlineLight
        )
    }
}

fun formatMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
    return formatter.format(Date(millis)).replaceFirstChar { it.uppercase() }
}
