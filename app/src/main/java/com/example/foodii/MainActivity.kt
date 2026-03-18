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
import com.example.foodii.feature.apifoodii.meal.presentation.screen.AddMealScreen
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
import com.example.foodii.feature.mealdb.presentation.screen.PlannerScreen
import com.example.foodii.feature.mealdb.presentation.viewmodel.PlannerViewModel
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
                        navController.navigate("meals_list") {
                            popUpTo("login") { inclusive = true }
                        }
                        navController.navigate("meals_summary")
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
                                onAddMealClick = { navController.navigate("add_meal") },
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

                    composable("add_meal") {
                        val user = currentUser
                        if (user != null) {
                            val mealViewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.mealModule.provideMealViewModelFactory()
                            )
                            val ingredientViewModel: IngredientViewModel = viewModel(
                                factory = appContainer.ingredientModule.provideIngredientViewModelFactory()
                            )
                            AddMealScreen(
                                viewModel = mealViewModel,
                                ingredientViewModel = ingredientViewModel,
                                userId = user.id,
                                onBackPressed = { navController.popBackStack() }
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
                                factory = appContainer.mealModule.provideMealViewModelFactory()
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

                    composable("planner") {
                        PlannerScreen(
                            onBackPressed = { navController.navigateUp() }
                        )
                    }

                    composable("ingredients") {
                        val viewModel: IngredientViewModel = viewModel(
                            factory = appContainer.ingredientModule.provideIngredientViewModelFactory()
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
