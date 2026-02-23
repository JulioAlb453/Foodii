package com.example.foodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.mealdb.presentation.screen.MealDetailsScreen
import com.example.foodii.feature.mealdb.presentation.screen.PlannerScreen
import dagger.hilt.android.AndroidEntryPoint

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

                NavHost(navController = navController, startDestination = "meal_details") {
                    composable("meal_details") {
                        MealDetailsScreen(
                            onBackPressed = { finish() },
                            onNavigateToPlanner = { navController.navigate("planner") }
                        )
                    }
                    composable("planner") {
                        PlannerScreen(onBackPressed = { navController.popBackStack() })
                    }
                }

                /*


                FoodiiTheme( dynamicColor = false) {
                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope()

                    val currentUser by appContainer.authRepository.authState.collectAsState(initial = null)

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
                                if (currentUser != null) {
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
                                    factory = appContainer.foodiiFeatureModule.provideMealViewModelFactory()
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
                                    factory = appContainer.foodiiFeatureModule.provideMealViewModelFactory()
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
                                    factory = appContainer.foodiiFeatureModule.provideMealViewModelFactory()
                                )
                                MealsSummaryScreen(
                                    viewModel = viewModel,
                                    userId = user.id,
                                    onBackPressed = { navController.navigateUp() }
                                )
                            }
                        }

                        composable("ingredients") {
                            val viewModel: IngredientViewModel = viewModel(
                                factory = appContainer.foodiiFeatureModule.provideIngredientViewModelFactory()
                            )
                            IngredientsScreen(
                                viewModel = viewModel,
                                onBackPressed = { navController.navigateUp() }
                            )
                        }
                    }
                }
                */
            }
        }
    }
}
