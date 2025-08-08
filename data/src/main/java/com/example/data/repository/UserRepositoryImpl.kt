package com.example.data.repository

import com.example.data.mapper.UserModelMapper.mapToUser
import com.example.data.mapper.UserModelMapper.userInfoToMap
import com.example.data.mapper.UserModelMapper.userToMap
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
) : UserRepository {

    companion object {
        private const val USERS_KEY = "users"
        private const val TARGET_CALORIES_KEY = "targetCalories"
    }

    private val usersCollection = firestore.collection(USERS_KEY)

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            if (!snapshot.exists()) {
                return Result.failure(Exception("User not found"))
            }

            val user = mapToUser(snapshot.data ?: emptyMap(), userId)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserInfo(user: User): Result<Unit> {
        return try {
            val userMap = userInfoToMap(user)
            usersCollection.document(user.id).update(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTargetCalories(userId: String, targetCalories: Int): Result<Unit> {
        return try {
            usersCollection.document(userId).update(TARGET_CALORIES_KEY, targetCalories).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}