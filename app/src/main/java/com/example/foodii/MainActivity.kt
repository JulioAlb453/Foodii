package com.example.foodii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.FoodiiTheme
import com.example.foodii.core.di.AppContainer
import com.example.foodii.feature.foods.presentation.screen.MealDetailsScreen
import com.example.foodii.feature.foods.presentation.screen.MealInfoScreen
import com.example.foodii.feature.planner.di.FoodDetailsModule
import com.example.foodii.feature.planner.domain.entity.MealDetail
import com.example.foodii.feature.planner.presentation.screen.components.MealDetailsScreen
import com.example.foodii.feature.planner.presentation.screen.components.PlannerScreen
import com.example.foodii.feature.planner.presentation.viewmodel.MealDetailsViewModel
import com.example.foodii.feature.planner.presentation.viewmodel.PlannerViewModel
import com.example.foodii.feature.planner.presentation.viewmodel.PlannerViewModelFactory

class MainActivity : ComponentActivity() {

    lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)
        val foodDetailsModule = FoodDetailsModule(appContainer)

        enableEdgeToEdge()
        setContent {
            FoodiiTheme {
                val navController = rememberNavController()
                var selectedMealForInfo by remember { mutableStateOf<MealDetail?>(null) }

                NavHost(
                    navController = navController,
                    startDestination = "details/a"
                ) {
                    composable(
                        route = "details/{letter}",
                        arguments = listOf(navArgument("letter") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val letter = backStackEntry.arguments?.getString("letter") ?: "a"
                        val factory = foodDetailsModule.provideMealDetailsViewModelFactory(letter)

                        MealDetailsScreen(
                            factory = factory,
                            onBackPressed = { navController.popBackStack() },
                            onMealClick = { meal ->
                                selectedMealForInfo = meal
                                navController.navigate("meal_info")
                            },
                            onViewPlannerClick = {
                                navController.navigate("planner")
                            }
                        )
                    }

                    composable("meal_info") {
                        selectedMealForInfo?.let { meal ->
                            val factory = foodDetailsModule.provideMealDetailsViewModelFactory("a")
                            val viewModel: MealDetailsViewModel = viewModel(factory = factory)

                            MealInfoScreen(
                                meal = meal,
                                onBackPressed = { navController.popBackStack() },
                                onScheduleMeal = { millis ->
                                    viewModel.onPlanMealSelected(meal, millis)
                                    navController.navigate("planner") {
                                        popUpTo("details/a")
                                    }
                                }
                            )
                        }
                    }

                    composable("planner") {
                        val plannerRepository = appContainer.plannerRepository
                        val viewModel: PlannerViewModel = viewModel(
                            factory = PlannerViewModelFactory(plannerRepository)
                        )

                        PlannerScreen(
                            viewModel = viewModel,
                            onBackPressed = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
