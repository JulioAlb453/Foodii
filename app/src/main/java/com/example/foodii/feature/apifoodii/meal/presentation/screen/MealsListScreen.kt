package com.example.foodii.feature.apifoodii.meal.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.FoodiiTheme
import com.example.compose.backgroundLight
import com.example.compose.onPrimaryDark
import com.example.compose.onPrimaryLight
import com.example.compose.outlineLight
import com.example.compose.primaryDark
import com.example.compose.primaryLight
import com.example.foodii.feature.apifoodii.meal.presentation.components.MealItemCard
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsListScreen(
    viewModel: MealFoodiiViewModel,
    userId: String,
    onViewSummaryClick: () -> Unit,
    onIngredientsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMealClick: (String) -> Unit
) {
    FoodiiTheme( dynamicColor = false) {
        val uiState by viewModel.uiState.collectAsState()
        val meals by viewModel.allMeals.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadAllMeals(userId)
        }

        Scaffold(
            containerColor = backgroundLight,
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = primaryDark,
                        titleContentColor = onPrimaryDark,
                        navigationIconContentColor = onPrimaryLight
                    ),
                    title = { Text("Platillos Disponibles", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onIngredientsClick) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBasket,
                                contentDescription = "Ingredientes",
                                tint = onPrimaryDark
                            )
                        }
                        IconButton(onClick = onViewSummaryClick) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Ver Agenda",
                                tint = onPrimaryDark
                            )
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar Sesión",
                                tint = onPrimaryDark
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                when {
                    uiState.isLoading && meals.isEmpty() -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = primaryLight
                        )
                    }
                    meals.isEmpty() -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = outlineLight
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay platillos creados",
                                color = outlineLight
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(meals) { meal ->
                                MealItemCard(
                                    meal = meal,
                                    onClick = { onMealClick(meal.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
