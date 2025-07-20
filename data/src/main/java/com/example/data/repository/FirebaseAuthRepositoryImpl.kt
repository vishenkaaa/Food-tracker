package com.example.data.repository

import com.example.domain.model.User
import com.example.domain.repository.FirebaseAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseAuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        val firebaseUser = authResult.user ?: return Result.failure(Exception("User is null"))
        val user =  User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName,
            email = firebaseUser.email,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isNew = authResult.additionalUserInfo?.isNewUser ?: false
        )

        if (user.isNew) {
            try {
                usersCollection.document(user.id).set(user).await()
            } catch (e: Exception) {
                return Result.failure(e)
            }
        }

        return Result.success(user)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    private val usersCollection = firestore.collection("users")

    override suspend fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    override suspend fun isUserFullyRegistered(userId: String): Boolean {
        val snapshot = usersCollection.document(userId).get().await()
        val user = snapshot.toObject(User::class.java)
        return user?.targetCalories != 0
    }
}
