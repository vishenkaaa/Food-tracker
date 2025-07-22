package com.example.data.repository

import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore
) : UserRepository {

    companion object{
        private const val USERS_KEY = "users"
        private const val TARGET_CALORIES_KEY = "targetCalories"
    }

    private val usersCollection = firestore.collection(USERS_KEY)

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            val snapshot = usersCollection.document(userId).get().await()
            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
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