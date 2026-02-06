import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.ingredient.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.ingredient.domain.entity.Ingredient
import com.example.foodii.feature.apifoodii.ingredient.domain.repository.IngredientRepository

import javax.inject.Inject

class IngredientFoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI
) : IngredientRepository {

    override suspend fun getAllIngredients(): List<Ingredient> {

        return try {
            val response = api.getAllIngredientsAPI(userId = "default_user")
            response.ingredients.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun findById(id: String): Ingredient? {
        return try {
            api.getIngredientByIdAPI(id).toDomain()
        } catch (e: Exception) {
            null
        }
    }

}