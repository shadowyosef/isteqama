package com.example.cloud

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.example.util.AppStrings
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object GoogleAuthHelper {

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val auth = Firebase.auth
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun attemptAutoSignIn(context: Context): Boolean {
        if (Firebase.auth.currentUser != null) return true
        return try {
            val cm = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build()
            val req = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
            val result = cm.getCredential(request = req, context = context)
            val cred = result.credential
            if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val token = GoogleIdTokenCredential.createFrom(cred.data)
                Firebase.auth.signInWithCredential(GoogleAuthProvider.getCredential(token.idToken, null)).await()
                true
            } else false
        } catch (_: NoCredentialException) {
            false
        } catch (e: Exception) {
            Log.d("GoogleAuthHelper", "Auto sign-in skipped: ${e.message}")
            false
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser?> {
        return try {
            val cm = CredentialManager.create(context)
            val option = GetSignInWithGoogleOption.Builder(
                context.getString(R.string.default_web_client_id)
            ).build()
            val req = GetCredentialRequest.Builder().addCredentialOption(option).build()
            val result = cm.getCredential(request = req, context = context)
            val cred = result.credential
            if (cred is CustomCredential && cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleCred = GoogleIdTokenCredential.createFrom(cred.data)
                val authRes = Firebase.auth.signInWithCredential(
                    GoogleAuthProvider.getCredential(googleCred.idToken, null)
                ).await()
                Result.success(authRes.user)
            } else {
                Result.failure(IllegalStateException("Unexpected credential type"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut(context: Context) {
        try {
            Firebase.auth.signOut()
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            Log.w("GoogleAuthHelper", "Sign out cleanup warning: ${e.message}")
        }
    }
}

@Composable
fun GoogleSignInButton(
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }

    Button(
        onClick = {
            if (!loading) {
                loading = true
                scope.launch {
                    val res = GoogleAuthHelper.signInWithGoogle(context)
                    loading = false
                    res.onSuccess { onSuccess() }.onFailure { onError(it.message ?: "Sign-in failed") }
                }
            }
        },
        enabled = !loading,
        modifier = modifier.fillMaxWidth().heightIn(min = 48.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
            Spacer(Modifier.width(8.dp))
        }
        Text(AppStrings.get("تسجيل الدخول عبر Google للمزامنة والحلقات", "Sign in with Google for Sync & Circles"))
    }
}
