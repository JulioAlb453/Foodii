package com.example.foodii.feature.foods.data.datasource.remote.mapper

import com.example.foodii.feature.foods.data.datasource.remote.model.CategoryDTO
import com.example.foodii.feature.foods.domain.entity.Category


fun CategoryDTO.toDomain(): Category{
    return Category(
        name = strCategory

    )
}
