package com.example.foodii.feature.apifoodii.meal.presentation.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.compose.FoodiiTheme
import com.example.compose.backgroundDark
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
    onRandomMealClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMealClick: (String) -> Unit,
    onAddMealClick: () -> Unit
) {
    val context = LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
        }
    }

    FoodiiTheme( dynamicColor = false, darkTheme = false) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val meals by viewModel.allMeals.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            viewModel.loadAllMeals(userId)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
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
                        IconButton(onClick = onAddMealClick) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nuevo platillo",
                                tint = onPrimaryDark
                            )
                        }
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
            },
            floatingActionButton = {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = onRandomMealClick,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = "Sugerencia aleatoria")
                    }

                    FloatingActionButton(
                        onClick = { viewModel.sendTestNotification(userId) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Probar Notificación")
                    }
                }
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
