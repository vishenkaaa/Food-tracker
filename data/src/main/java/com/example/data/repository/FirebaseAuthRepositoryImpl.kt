package com.example.data.repository

import com.example.data.mapper.UserModelMapper.mapToUser
import com.example.data.mapper.UserModelMapper.userToMap
import com.example.domain.model.User
import com.example.domain.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore
) : FirebaseAuthRepository {

    companion object{
        private const val USERS_KEY = "users"
    }

    private val usersCollection = firestore.collection(USERS_KEY)

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()

            val firebaseUser = authResult.user ?: return Result.failure(Exception("User is null"))

            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            val userId = firebaseUser.uid

            val userDocument = usersCollection.document(userId)

            if (isNewUser) {
                val newUser = User(
                    id = userId,
                    name = firebaseUser.displayName,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isNew = true
                )
                val createResult = createUser(newUser)
                if (createResult.isFailure)
                    return Result.failure(createResult.exceptionOrNull() ?: Exception("Failed to create user"))

                Result.success(newUser)
            } else {
                val snapshot = userDocument.get().await()

                val existingUser = snapshot.data?.let { mapToUser(it, userId) }
                    ?: return Result.failure(Exception("User data not found"))

                Result.success(existingUser)
            }
        }
        catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUser(user: User): Result<Unit> {
        return try {
            val userMap = userToMap(user)
            usersCollection.document(user.id).set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isUserFullyRegistered(userId: String): Boolean {
        val snapshot = usersCollection.document(userId).get().await()
        val user = snapshot.data?.let { mapToUser(it, userId) }
        return user?.targetCalories != 0
    }
}
