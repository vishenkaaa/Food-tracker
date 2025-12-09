package com.example.data.mapper

import com.example.domain.model.user.Gender
import com.example.domain.model.user.Goal
import com.example.domain.model.user.User
import com.example.domain.model.user.UserActivityLevel
import java.time.LocalDate
import kotlin.math.abs

object UserModelMapper {
     const val GOAL_KEY = "goal"
     const val TARGET_CALORIES_KEY = "targetCalories"
     const val WEIGHT_CHANGE_KEY = "weightChange" // старий ключ
     const val TARGET_WEIGHT_KEY = "targetWeight" // новий ключ
     const val CURRENT_WEIGHT_KEY = "currentWeight"
     const val GENDER_KEY = "gender"
     const val USER_ACTIVITY_LEVEL_KEY = "userActivityLevel"
     const val HEIGHT_KEY = "height"
     const val BIRTH_DATE_KEY = "birthDate"
     const val NAME_KEY = "name"
     const val EMAIL_KEY = "email"
     const val PHOTO_URL_KEY = "photoUrl"
     const val IS_NEW_KEY = "isNew"

    // User info в Map для Firebase
    fun userInfoToMap(user: User): Map<String, Any?> {
        return mapOf(
            NAME_KEY to user.name,
            GOAL_KEY to user.goal.value,
            TARGET_CALORIES_KEY to user.targetCalories,
            TARGET_WEIGHT_KEY to user.targetWeight,
            CURRENT_WEIGHT_KEY to user.currentWeight,
            GENDER_KEY to user.gender.value,
            USER_ACTIVITY_LEVEL_KEY to user.userActivityLevel.value,
            HEIGHT_KEY to user.height,
            BIRTH_DATE_KEY to user.birthDate?.toString(),
            IS_NEW_KEY to user.isNew
        )
    }

    // User в Map для Firebase
    fun userToMap(user: User): Map<String, Any?> {
        return mapOf(
            GOAL_KEY to user.goal.value,
            TARGET_CALORIES_KEY to user.targetCalories,
            TARGET_WEIGHT_KEY to user.targetWeight,
            CURRENT_WEIGHT_KEY to user.currentWeight,
            GENDER_KEY to user.gender.value,
            USER_ACTIVITY_LEVEL_KEY to user.userActivityLevel.value,
            HEIGHT_KEY to user.height,
            BIRTH_DATE_KEY to user.birthDate?.toString(),
            NAME_KEY to user.name,
            EMAIL_KEY to user.email,
            PHOTO_URL_KEY to user.photoUrl,
            IS_NEW_KEY to user.isNew
        )
    }

    // Map з Firebase в User
    fun mapToUser(data: Map<String, Any>, userId: String): User {
        val currentWeight = (data[CURRENT_WEIGHT_KEY] as? Number)?.toFloat()
        val goal = data[GOAL_KEY]?.toString()?.let { Goal.fromValue(it) } ?: Goal.MAINTAIN

        val targetWeight = when{
            data.containsKey(TARGET_WEIGHT_KEY) -> (data[TARGET_WEIGHT_KEY] as? Number)?.toFloat()

            data.containsKey(WEIGHT_CHANGE_KEY) -> {
                val weightChange = (data[WEIGHT_CHANGE_KEY] as? Number)?.toFloat()

                if (weightChange != null && currentWeight != null) {
                    when (goal) {
                        Goal.LOSE -> currentWeight - abs(weightChange)
                        Goal.GAIN -> currentWeight +  abs(weightChange)
                        Goal.MAINTAIN -> currentWeight
                    }
                } else
                    currentWeight
            }

            else -> currentWeight
        }

        return User(
            id = userId,
            goal = goal,
            targetCalories = (data[TARGET_CALORIES_KEY] as? Number)?.toInt() ?: 0,
            targetWeight = targetWeight,
            currentWeight = currentWeight,
            gender = data[GENDER_KEY]?.toString()?.let { Gender.fromValue(it) } ?: Gender.MALE,
            userActivityLevel = data[USER_ACTIVITY_LEVEL_KEY]?.toString()?.let {
                UserActivityLevel.fromValue(it)
            } ?: UserActivityLevel.SEDENTARY,
            height = (data[HEIGHT_KEY] as? Number)?.toInt(),
            birthDate = data[BIRTH_DATE_KEY]?.toString()?.let { LocalDate.parse(it) },
            name = data[NAME_KEY] as? String,
            email = data[EMAIL_KEY] as? String,
            photoUrl = data[PHOTO_URL_KEY] as? String,
            isNew = data[IS_NEW_KEY] as? Boolean ?: true
        )
    }
}