package com.example.foodii.feature.food_preferences.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun FoodCategoryGrid(
    categories: List<String>,
    selectedCategories: Set<String>,
    onCategoryToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val columns = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        3
    } else {
        2
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 80.dp
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        items(categories, key = { it }) { category ->
            FoodCategoryCard(
                name = category,
                isSelected = selectedCategories.contains(category),
                onClick = { onCategoryToggled(category) }
            )
        }
    }
}