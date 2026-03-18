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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.compose.*
import com.example.foodii.feature.apifoodii.meal.presentation.components.MealItemCard
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealsListScreen(
    viewModel: MealFoodiiViewModel,
    userId: String,
    onViewSummaryClick: () -> Unit,
    onIngredientsClick: () -> Unit,
    onAddMealClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onMealClick: (String) -> Unit,
    onRandomDishClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val meals by viewModel.allMeals.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        viewModel.loadAllMeals(userId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    FoodiiTheme( dynamicColor = false) {
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
                            Icon(Icons.Default.ShoppingBasket, contentDescription = "Ingredientes", tint = onPrimaryDark)
                        }
                        IconButton(onClick = onViewSummaryClick) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Ver Agenda", tint = onPrimaryDark)
                        }
                        IconButton(onClick = onLogoutClick) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar Sesión", tint = onPrimaryDark)
                        }
                    }
                )
            },
            floatingActionButton = {
                Column(horizontalAlignment = Alignment.End) {
                    SmallFloatingActionButton(
                        onClick = { viewModel.sendTestNotification(userId) },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = "Probar Notificación")
                    }

                    FloatingActionButton(
                        onClick = onRandomDishClick,
                        containerColor = secondaryDark,
                        contentColor = onSecondaryDark,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.Casino, contentDescription = "Platillo Aleatorio")
                    }

                    FloatingActionButton(
                        onClick = onAddMealClick,
                        containerColor = primaryDark,
                        contentColor = onPrimaryDark
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Platillo")
                    }
                }
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        onClick = onRandomDishClick,
                        colors = CardDefaults.cardColors(containerColor = primaryLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = onPrimaryLight)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("¿No sabes qué comer? ¡Elige al azar!", color = onPrimaryLight, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (meals.isEmpty() && !uiState.isLoading) {
                        Column(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Restaurant,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = outlineLight
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No hay platillos creados", color = outlineLight)
                        }
                    } else {
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
                
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryLight
                    )
                }
            }
        }
    }
}
