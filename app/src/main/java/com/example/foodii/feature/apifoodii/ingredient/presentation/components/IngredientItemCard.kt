package com.example.foodii.feature.apifoodii.ingredient.presentation.components

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
import com.example.compose.onSurfaceLight
import com.example.compose.outlineLight
import com.example.compose.surfaceContainerLight
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.ui.theme.TypographyFoodii

@Composable
fun IngredientItemCard(
    ingredient: Ingredient,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name,
                    style = TypographyFoodii.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceLight
                )
                Text(
                    text = "Valor nutricional base",
                    style = TypographyFoodii.labelMedium,
                    color = outlineLight
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${ingredient.caloriesPer100g.toInt()}",
                    style = TypographyFoodii.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "kcal / 100g",
                    style = TypographyFoodii.labelSmall,
                    color = outlineLight,
                    fontSize = 10.sp
                )
            }
        }
    }
}
