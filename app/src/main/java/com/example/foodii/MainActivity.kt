package com.example.foodii

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
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
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)

        enableEdgeToEdge()
        setContent {
            FoodiiTheme {
                val navController = rememberNavController()
                val scope = rememberCoroutineScope()
                
                val currentUser by appContainer.authRepository.authState.collectAsState(initial = null)

                // Log Crítico para ver el estado del usuario
                LaunchedEffect(currentUser) {
                    Log.e("AWS_MAIN", "Estado del usuario cambiado: ${currentUser?.username ?: "NADIE LOGUEADO"}")
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
                            if (currentUser != null) {
                                Log.e("AWS_MAIN", "Usuario detectado, navegando a meals_list")
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

                    composable("meals_list") {
                        val user = currentUser
                        if (user != null) {
                            Log.e("AWS_MAIN", "Mostrando MealsListScreen para el usuario: ${user.id}")
                            val viewModel: MealFoodiiViewModel = viewModel(
                                factory = appContainer.foodiiFeatureModule.provideMealViewModelFactory()
                            )
                            
                            MealsListScreen(
                                viewModel = viewModel,
                                userId = user.id,
                                onViewSummaryClick = { navController.navigate("meals_summary") },
                                onLogoutClick = {
                                    scope.launch {
                                        appContainer.authRepository.logout()
                                        navController.navigate("login") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                },
                                onMealClick = { /* Navegar a detalles */ }
                            )
                        } else {
                            Log.e("AWS_MAIN", " MealsListScreen: currentUser es NULL")
                        }
                    }

                    // ... resto de composables
                }
            }
        }
    }
}
