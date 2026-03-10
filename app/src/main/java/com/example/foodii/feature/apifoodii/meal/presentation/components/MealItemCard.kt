package com.example.foodii.feature.apifoodii.meal.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.outlineDark
import com.example.compose.outlineLight
import com.example.compose.primaryDark
import com.example.compose.primaryLight
import com.example.compose.surfaceVariantLight
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.ui.theme.TypographyFoodii

@Composable
fun MealItemCard(
    meal: FoodiiMeal,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceVariantLight
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = primaryDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = primaryLight
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = meal.name,
                        style = TypographyFoodii.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = meal.mealTime.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = TypographyFoodii.bodySmall,
                        color = outlineDark
                    )
                }
            }
            
            Text(
                text = "${meal.totalCalories} kcal",
                style = TypographyFoodii.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = primaryLight,
                fontSize = 14.sp
            )
        }
    }
}
