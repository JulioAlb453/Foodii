package com.example.foodii.feature.foods.domain.usecases

import com.example.foodii.feature.foods.domain.entity.Category
import com.example.foodii.feature.foods.domain.repositories.MelCategoryRepository

class GetMelCategoryUseCase (
    private val repository: MelCategoryRepository
){
    suspend operator fun invoke(): Result<List<Category>> {
        return try {
            val categories = repository.getCategories()
            val filteredCategories = categories.filter { it.name.isNotBlank() }
            if (filteredCategories.isEmpty()){
                Result.failure(Exception("No se encontraron categorias validas"))
            } else {
                Result.success(filteredCategories)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}