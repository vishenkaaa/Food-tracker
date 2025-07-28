package com.example.data.repository

import com.example.data.mapper.UserModelMapper.mapToUser
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

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        val firebaseUser = authResult.user ?: return Result.failure(Exception("User is null"))

        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
        val userId = firebaseUser.uid

        val userDocument = usersCollection.document(userId)
        val targetCalories: Int

        if (isNewUser) {
            targetCalories = 0
            val newUser = User(
                id = userId,
                name = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isNew = true,
                targetCalories = targetCalories
            )
            try {
                userDocument.set(newUser).await()
            } catch (e: Exception) {
                return Result.failure(e)
            }
        } else {
            try {
                val snapshot = userDocument.get().await()
                val existingUser = snapshot.data?.let { mapToUser(it, userId) }

                targetCalories = existingUser?.targetCalories ?: 0
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        val user = User(
            id = userId,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isNew = isNewUser,
            targetCalories = targetCalories
        )

        return Result.success(user)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private val usersCollection = firestore.collection(USERS_KEY)

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
