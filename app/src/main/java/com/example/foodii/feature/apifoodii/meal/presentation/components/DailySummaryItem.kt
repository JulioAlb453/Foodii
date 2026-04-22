package com.example.foodii.feature.apifoodii.meal.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.ui.theme.TypographyFoodii

@Composable
fun DailySummaryItem(
    summary: DailySummary,
    onMealClick: (FoodiiMeal) -> Unit = {},
    onRescheduleClick: (FoodiiMeal) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = summary.date,
                style = TypographyFoodii.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = "Total: ${summary.totalCalories} kcal",
                style = TypographyFoodii.labelMedium,
                color = MaterialTheme.colorScheme.primary, // Cambiado a color primario del tema para mejor visibilidad
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) // Cambiado para que sea más visible
        )

        summary.meals.forEach { meal ->
            MealItemCard(
                meal = meal,
                onClick = { onMealClick(meal) },
                onRescheduleClick = { onRescheduleClick(meal) }
            )
        }
    }
}
