package com.example.data.mapper

import com.example.domain.model.Gender
import com.example.domain.model.Goal
import com.example.domain.model.User
import com.example.domain.model.UserActivityLevel
import java.time.LocalDate

object UserModelMapper {
    private const val GOAL_KEY = "goal"
    private const val TARGET_CALORIES_KEY = "targetCalories"
    private const val TARGET_WEIGHT_KEY = "targetWeight"
    private const val CURRENT_WEIGHT_KEY = "currentWeight"
    private const val GENDER_KEY = "gender"
    private const val USER_ACTIVITY_LEVEL_KEY = "userActivityLevel"
    private const val HEIGHT_KEY = "height"
    private const val BIRTH_DATE_KEY = "birthDate"
    private const val NAME_KEY = "name"
    private const val EMAIL_KEY = "email"
    private const val PHOTO_URL_KEY = "photoUrl"
    private const val IS_NEW_KEY = "isNew"

    // User в Map для Firebase
    fun userToMap(user: User): Map<String, Any?> {
        return mapOf(
            GOAL_KEY to user.goal.name,
            TARGET_CALORIES_KEY to user.targetCalories,
            TARGET_WEIGHT_KEY to user.targetWeight,
            CURRENT_WEIGHT_KEY to user.currentWeight,
            GENDER_KEY to user.gender.name,
            USER_ACTIVITY_LEVEL_KEY to user.userActivityLevel.name,
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
        return User(
            id = userId,
            goal = data[GOAL_KEY]?.toString()?.let { Goal.valueOf(it) } ?: Goal.MAINTAIN,
            targetCalories = (data[TARGET_CALORIES_KEY] as? Number)?.toInt() ?: 0,
            targetWeight = (data[TARGET_WEIGHT_KEY] as? Number)?.toFloat(),
            currentWeight = (data[CURRENT_WEIGHT_KEY] as? Number)?.toFloat(),
            gender = data[GENDER_KEY]?.toString()?.let { Gender.valueOf(it) } ?: Gender.MALE,
            userActivityLevel = data[USER_ACTIVITY_LEVEL_KEY]?.toString()?.let {
                UserActivityLevel.valueOf(it)
            } ?: UserActivityLevel.SEDENTARY,
            height = (data[HEIGHT_KEY] as? Number)?.toInt(),
            birthDate = data[BIRTH_DATE_KEY]?.toString()?.let { LocalDate.parse(it) },
            name = data[NAME_KEY] as? String,
            email = data[EMAIL_KEY] as? String,
            photoUrl = data[PHOTO_URL_KEY] as? String,
            isNew = data[IS_NEW_KEY] as? Boolean ?: false
        )
    }
}