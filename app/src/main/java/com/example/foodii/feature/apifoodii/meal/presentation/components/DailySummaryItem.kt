package com.example.foodii.feature.apifoodii.meal.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.onSurfaceLight
import com.example.compose.primaryLight
import com.example.foodii.feature.apifoodii.meal.domain.entity.DailySummary
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal

@Composable
fun DailySummaryItem(
    summary: DailySummary,
    onMealClick: (FoodiiMeal) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        // Cabecera del día
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = summary.date, // Formato yyyy-MM-dd
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = onSurfaceLight
            )
            
            Text(
                text = "Total: ${summary.totalCalories} kcal",
                style = MaterialTheme.typography.labelMedium,
                color = primaryLight,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

        // Lista de comidas del día
        summary.meals.forEach { meal ->
            MealItemCard(
                meal = meal,
                onClick = { onMealClick(meal) }
            )
        }
    }
}
