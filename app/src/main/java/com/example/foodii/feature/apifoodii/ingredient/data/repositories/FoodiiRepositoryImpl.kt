import com.example.foodii.core.network.FoodiiAPI
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDomain
import com.example.foodii.feature.apifoodii.meal.data.datasource.remote.mapper.toDto
import com.example.foodii.feature.apifoodii.meal.domain.entity.FoodiiMeal
import com.example.foodii.feature.apifoodii.meal.domain.repository.FoodiiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FoodiiRepositoryImpl @Inject constructor(
    private val api: FoodiiAPI
) : FoodiiRepository {

    override fun getMeals(): Flow<List<FoodiiMeal>> = flow {
        try {
            val response = api.getMealsAPI()
            emit(response.meals.map { it.toDomain() })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
    override suspend fun saveMeal(meal: FoodiiMeal) {
        val dto = meal.toDto()
        api.saveMealAPI(dto)
    }

    override suspend fun deleteMeal(id: String) {
        api.deleteMealAPI(id)
    }

    override suspend fun getMealById(id: String): FoodiiMeal? {
        return try {
            val response = api.getMealByIdAPI(id)
            response.toDomain()
        } catch (e: Exception) {
            null
        }
    }

}