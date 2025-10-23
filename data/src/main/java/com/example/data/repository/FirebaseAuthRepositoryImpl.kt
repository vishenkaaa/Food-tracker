package com.example.data.repository

import com.example.data.auth.LocalAuthStateManager
import com.example.data.mapper.UserModelMapper.mapToUser
import com.example.data.util.safeCall
import com.example.domain.logger.ErrorLogger
import com.example.domain.model.user.User
import com.example.domain.repository.FirebaseAuthRepository
import com.example.domain.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val localAuthStateManager: LocalAuthStateManager,
    private val errorLogger: ErrorLogger
) : FirebaseAuthRepository {

    companion object{
        private const val USERS_KEY = "users"
    }

    private val usersCollection = firestore.collection(USERS_KEY)

    override suspend fun signInWithGoogle(idToken: String): Result<User> = safeCall(errorLogger) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        val firebaseUser = authResult.user ?: throw Exception("User is null")

        val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
        val userId = firebaseUser.uid
        val userDocument = usersCollection.document(userId)

        if (isNewUser) {
            val newUser = User(
                id = userId,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isNew = true
            )
            userRepository.createUser(newUser).getOrThrow()
            localAuthStateManager.saveAuthState(userId, firebaseUser.email, true)
            newUser
        } else {
            val snapshot = userDocument.get().await()

            val existingUser = snapshot.data?.let { mapToUser(it, userId) }
                ?: throw Exception("User data not found")

            localAuthStateManager.saveAuthState(userId, firebaseUser.email, true)
            existingUser
        }
    }

    override suspend fun signOut() {
        localAuthStateManager.clearAuthState()
        auth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> = safeCall(errorLogger) {
        val currentUser = auth.currentUser ?: throw Exception("No authenticated user")
        val userId = currentUser.uid

        currentUser.delete().await()

        usersCollection.document(userId).delete().await()
        localAuthStateManager.clearAuthState()
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return localAuthStateManager.isUserLoggedIn()
    }

    override suspend fun getCurrentUserId(): String? {
        return localAuthStateManager.getCurrentUserId()
    }

    override suspend fun getCurrentUserEmail(): String? {
        return  Firebase.auth.currentUser?.email
    }
}