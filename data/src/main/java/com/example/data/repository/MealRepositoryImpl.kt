package com.example.data.repository

import com.example.data.mapper.DishModelMapper
import com.example.data.util.safeCall
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.diary.DailyMeals
import com.example.domain.model.diary.Dish
import com.example.domain.model.diary.MealType
import com.example.domain.repository.MealRepository
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val errorLogger: ErrorLogger
): MealRepository {
    companion object {
        private const val USERS_KEY = "users"
        private const val DIARY_KEY = "diary"
    }

    override suspend fun addDishToMeal(
        userId: String,
        date: String,
        mealType: MealType,
        dish: Dish
    ): Result<Unit> = safeCall(errorLogger) {
        val dishMap = DishModelMapper.dishToMap(dish)
        firestore.collection(USERS_KEY)
            .document(userId)
            .collection(DIARY_KEY)
            .document(date)
            .collection(mealType.value)
            .document(dish.id)
            .set(dishMap)
            .await()
    }

    override suspend fun removeDishFromMeal(
        userId: String,
        date: String,
        mealType: MealType,
        dishId: String
    ): Result<Unit> = safeCall(errorLogger){
        firestore.collection(USERS_KEY)
            .document(userId)
            .collection(DIARY_KEY)
            .document(date)
            .collection(mealType.value)
            .document(dishId)
            .delete()
            .await()
    }

    override suspend fun updateDishInMeal(
        userId: String,
        date: String,
        mealType: MealType,
        dish: Dish
    ): Result<Unit> = safeCall(errorLogger) {
        val dishMap = DishModelMapper.dishToMap(dish)
        firestore.collection(USERS_KEY)
            .document(userId)
            .collection(DIARY_KEY)
            .document(date)
            .collection(mealType.value)
            .document(dish.id)
            .update(dishMap)
            .await()
    }

    override suspend fun getMealsByDate(userId: String, date: String): Result<DailyMeals> = safeCall(errorLogger) {
        getDailyMealsForDate(userId, date)
    }

    override suspend fun getDishesForMeal(
        userId: String,
        date: String,
        mealType: MealType
    ): Result<List<Dish>> = safeCall(errorLogger){
        getDishesForMealType(userId, date, mealType)
    }

    override suspend fun getMealsForDateRange(
        userId: String,
        startDate: String,
        endDate: String
    ): Result<Map<String, DailyMeals>> = safeCall(errorLogger) {
        val diaryRef = firestore.collection(USERS_KEY)
            .document(userId)
            .collection(DIARY_KEY)

        val datesSnapshot = diaryRef
            .whereGreaterThanOrEqualTo(FieldPath.documentId(), startDate)
            .whereLessThanOrEqualTo(FieldPath.documentId(), endDate)
            .get()
            .await()

        val mealsMap = mutableMapOf<String, DailyMeals>()

        for (dateDocument in datesSnapshot.documents) {
            val date = dateDocument.id
            val dailyMeals = getDailyMealsForDate(userId, date)
            mealsMap[date] = dailyMeals
        }

        mealsMap
    }

    private suspend fun getDailyMealsForDate(userId: String, date: String): DailyMeals {
        val breakfast = getDishesForMealType(userId, date, MealType.BREAKFAST)
        val lunch = getDishesForMealType(userId, date, MealType.LUNCH)
        val dinner = getDishesForMealType(userId, date, MealType.DINNER)
        val snacks = getDishesForMealType(userId, date, MealType.SNACKS)

        return DailyMeals(date = LocalDate.parse(date), breakfast, lunch, dinner, snacks)
    }

    private suspend fun getDishesForMealType(
        userId: String,
        date: String,
        mealType: MealType
    ): List<Dish>{
        val snapshot = firestore.collection(USERS_KEY)
            .document(userId)
            .collection(DIARY_KEY)
            .document(date)
            .collection(mealType.value)
            .get()
            .await()

        return snapshot.documents.mapNotNull { document ->
            document.data?.let { data ->
                DishModelMapper.mapToDish(data, document.id)
            }
        }
    }
}