package com.neelesh.todolist.user

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.neelesh.todolist.R
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException

class GoogleAuthUIClient(
    private val context: Context,
    private val oneTapClient: SignInClient,
) {
    private val auth = Firebase.auth

    suspend fun signIn(): IntentSender? {

        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }

        return result?.pendingIntent?.intentSender

    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_id_client))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }


    suspend fun signInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredential).await().user

            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        userName = user.displayName,
                        pfpUrl = user.photoUrl.toString()
                    )
                },
                error = null
            )

        } catch (e: Exception) {
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                error = e.message
            )
        }


    }

    fun getSignedInUserData(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            userName = displayName,
            pfpUrl = photoUrl.toString()
        )
    }

    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}