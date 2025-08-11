package com.example.data.repository

import com.example.data.mapper.UserModelMapper.mapToUser
import com.example.data.mapper.UserModelMapper.userInfoToMap
import com.example.data.util.safeCall
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.user.User
import com.example.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val errorLogger: ErrorLogger
) : UserRepository {

    companion object {
        private const val USERS_KEY = "users"
        private const val TARGET_CALORIES_KEY = "targetCalories"
    }

    private val usersCollection = firestore.collection(USERS_KEY)

    override suspend fun getUser(userId: String): Result<User> = safeCall(errorLogger) {
        val snapshot = usersCollection.document(userId).get().await()
        if (!snapshot.exists()) {
            throw Exception("User not found")
        }

        val user = mapToUser(snapshot.data ?: emptyMap(), userId)
        user
    }

    override suspend fun updateUserInfo(user: User): Result<Unit> = safeCall(errorLogger){
        val userMap = userInfoToMap(user)
        usersCollection.document(user.id).update(userMap).await()
    }

    override suspend fun updateTargetCalories(userId: String, targetCalories: Int): Result<Unit> = safeCall(errorLogger) {
        usersCollection.document(userId).update(TARGET_CALORIES_KEY, targetCalories).await()
    }
}