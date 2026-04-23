package com.example.foodii

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.foodii.feature.apifoodii.meal.presentation.screen.RandomMealScreen
import com.example.foodii.feature.apifoodii.meal.presentation.viewmodel.MealFoodiiViewModel
import com.example.foodii.feature.auth.domain.usecase.LoginUseCase
import com.example.foodii.feature.auth.domain.usecase.LogoutUseCase
import com.example.foodii.feature.auth.domain.usecase.RegisterUseCase
import com.example.foodii.feature.auth.presentation.AuthViewModel
import com.example.foodii.feature.auth.presentation.AuthViewModelFactory
import com.example.foodii.feature.auth.presentation.LoginScreen
import com.example.foodii.feature.auth.presentation.RegisterScreen
import com.example.foodii.feature.food_preferences.presentation.FoodPreferencesScreen
import com.example.foodii.feature.food_preferences.domain.model.NotificationCategory
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.foodii.feature.auth.data.datasource.local.AuthLocalDataSource
import com.example.foodii.core.service.worker.WidgetUpdateWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authLocalDataSource: AuthLocalDataSource

    lateinit var appContainer: AppContainer

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones concedido")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)

        askNotificationPermission()

        WidgetUpdateWorker.schedule(this)
        val testRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
        WorkManager.getInstance(this).enqueue(testRequest)

        enableEdgeToEdge()
        setContent {
            FoodiiTheme(dynamicColor = false, darkTheme = false) {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                
                val widgetMealId = remember { intent?.getStringExtra("mealId") }

                val currentUser by appContainer.authRepository.authState.collectAsStateWithLifecycle(
                    initialValue = null
                )
                LaunchedEffect(currentUser?.notificationCategoryPreferences) {
                    currentUser?.notificationCategoryPreferences?.let { prefs ->
                        Log.d("FCM_SYNC", "Sincronizando suscripciones para: ${currentUser?.username}")

                        prefs.forEach { slug ->
                            FirebaseMessaging.getInstance().subscribeToTopic(slug)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) Log.d("FCM_SYNC", "Suscrito a tópico: $slug")
                                }
                        }
                    }
                }

                LaunchedEffect(widgetMealId, currentUser) {
                    if (currentUser != null && !widgetMealId.isNullOrEmpty()) {
                        navController.navigate("meal_detail/$widgetMealId") {
                            popUpTo("meals_list") { inclusive = false }
                        }
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
                            val user = currentUser
                            if (user != null && widgetMealId.isNullOrEmpty()) {
                                if (user.notificationCategoryPreferences == null) {
                                    navController.navigate("food_preferences_screen") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("meals_list") {
                                        popUpTo("login") { inclusive = true }
                                    }
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
                            onRegisterSuccess = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.navigateUp() }
                        )
                    }

                    composable("food_preferences_screen") {
                        FoodPreferencesScreen(
                            onNavigateToHome = {
                                navController.navigate("meals_list") {
                                    popUpTo("food_preferences_screen") { inclusive = true }
                                }
                            }
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
                                onRandomMealClick = { navController.navigate("random_meal") },
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
                                },
                                onAddMealClick = { navController.navigate("add_meal") }
                            )
                        }
                    }

                    composable("random_meal") {
                        val user = currentUser
                        if (user != null) {
                            val viewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.mealModule.provideMealViewModelFactory()
                            )
                            RandomMealScreen(
                                viewModel = viewModel,
                                userId = user.id,
                                onBackPressed = { navController.popBackStack() }
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

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
