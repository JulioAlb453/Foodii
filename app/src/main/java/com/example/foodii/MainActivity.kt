package com.example.foodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.apifoodii.ingredient.presentation.screen.IngredientsScreen
import com.example.foodii.feature.apifoodii.ingredient.presentation.viemodel.IngredientViewModel
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealDetailScreen
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealsListScreen
import com.example.foodii.feature.apifoodii.meal.presentation.screen.MealsSummaryScreen
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import com.example.foodii.feature.auth.domain.usecase.LoginUseCase
import com.example.foodii.feature.auth.domain.usecase.LogoutUseCase
import com.example.foodii.feature.auth.domain.usecase.RegisterUseCase
import com.example.foodii.feature.auth.presentation.AuthViewModel
import com.example.foodii.feature.auth.presentation.AuthViewModelFactory
import com.example.foodii.feature.auth.presentation.LoginScreen
import com.example.foodii.feature.auth.presentation.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)

        enableEdgeToEdge()
        setContent {
            FoodiiTheme(dynamicColor = false) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                
                val widgetMealId = remember { intent?.getStringExtra("mealId") }

                val currentUser by appContainer.authRepository.authState.collectAsState(initial = null)

                LaunchedEffect(widgetMealId, currentUser) {
                    if (currentUser != null && !widgetMealId.isNullOrEmpty()) {
                        // 1. Establecemos la lista principal como base y limpiamos el login
                        navController.navigate("meals_list") {
                            popUpTo("login") { inclusive = true }
                        }
                        // 2. Agregamos el Menú Semanal (comidas agendadas) a la pila
                        navController.navigate("meals_summary")
                        
                        // 3. Abrimos el detalle. Al volver atrás, irá a meals_summary
                        navController.navigate("meal_detail/$widgetMealId")
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        val viewModel: AuthViewModel = viewModel(
                            factory = AuthViewModelFactory(
                                loginUseCase = LoginUseCase(appContainer.authRepository),
                                registerUseCase = RegisterUseCase(appContainer.authRepository),
                                logoutUseCase = LogoutUseCase(appContainer.authRepository)
                            )
                        )

                        LaunchedEffect(currentUser) {
                            if (currentUser != null && widgetMealId.isNullOrEmpty()) {
                                navController.navigate("meals_list") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }

                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { },
                            onNavigateToRegister = { navController.navigate("register") }
                        )
                    }

                    composable("register") {
                        val viewModel: AuthViewModel = viewModel(
                            factory = AuthViewModelFactory(
                                loginUseCase = LoginUseCase(appContainer.authRepository),
                                registerUseCase = RegisterUseCase(appContainer.authRepository),
                                logoutUseCase = LogoutUseCase(appContainer.authRepository)
                            )
                        )
                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = { navController.navigateUp() },
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }

                    composable("meals_list") {
                        val user = currentUser
                        if (user != null) {
                            val viewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.mealModule.provideMealViewModelFactory()
                            )

                            MealsListScreen(
                                viewModel = viewModel,
                                userId = user.id,
                                onViewSummaryClick = { navController.navigate("meals_summary") },
                                onIngredientsClick = { navController.navigate("ingredients") },
                                onLogoutClick = {
                                    scope.launch {
                                        appContainer.authRepository.logout()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                },
                                onMealClick = { mealId ->
                                    navController.navigate("meal_detail/$mealId")
                                }
                            )
                        }
                    }

                    composable(
                        "meal_detail/{mealId}",
                        arguments = listOf(navArgument("mealId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
                        val user = currentUser
                        if (user != null) {
                            val viewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.mealModule.provideMealViewModelFactory()
                            )
                            MealDetailScreen(
                                viewModel = viewModel,
                                mealId = mealId,
                                userId = user.id,
                                onBackPressed = { navController.navigateUp() }
                            )
                        }
                    }

                    composable("meals_summary") {
                        val user = currentUser
                        if (user != null) {
                            val viewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.
                                mealModule.provideMealViewModelFactory()
                            )
                            MealsSummaryScreen(
                                viewModel = viewModel,
                                userId = user.id,
                                onBackPressed = { navController.popBackStack() },
                                onNavigateToDetail = { mealId ->
                                    navController.navigate("meal_detail/$mealId")
                                }
                            )
                        }
                    }

                    composable("ingredients") {
                        val viewModel: IngredientViewModel = viewModel(
                            factory = appContainer.
                            ingredientModule.
                            provideIngredientViewModelFactory()
                        )
                        IngredientsScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}
