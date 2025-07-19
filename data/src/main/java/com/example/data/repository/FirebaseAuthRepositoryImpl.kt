package com.example.data.repository

import android.content.Context
import android.credentials.CredentialManager
import android.credentials.GetCredentialException
import android.credentials.GetCredentialRequest
import android.util.Base64
import com.example.domain.model.User
import com.example.domain.repository.FirebaseAuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : FirebaseAuthRepository {

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = auth.signInWithCredential(credential).await()

        val user = authResult.user ?: return Result.failure(Exception("User is null"))

        return Result.success(
            User(
                id = user.uid,
                name = user.displayName,
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
                isNew = authResult.additionalUserInfo?.isNewUser ?: false
            )
        )
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
