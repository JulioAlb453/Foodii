package com.example.foodii.feature.mealdb.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.compose.onSurfaceLight
import com.example.compose.onSurfaceLightMediumContrast
import com.example.compose.outlineLight
import com.example.compose.primaryLight
import com.example.compose.surfaceContainerLight
import com.example.foodii.feature.mealdb.domain.entity.MealDetail
import com.example.ui.theme.TypographyFoodii

@Composable
fun MealInfoCard(
    meal: MealDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceContainerLight,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AsyncImage(
                model = meal.imageUrl,
                contentDescription = meal.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = meal.name,
                style = TypographyFoodii.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = onSurfaceLight
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = outlineLight
            )

            Text(
                text = "Instrucciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = primaryLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = meal.instructions,
                style = TypographyFoodii.bodyLarge,
                lineHeight = TypographyFoodii.bodyLarge.lineHeight *1.2,
                color = onSurfaceLightMediumContrast
            )
        }
    }
}
