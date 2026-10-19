package com.example.fareplansa

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Handles the full Google Sign-In flow:
 *  1. Launches the Google account picker
 *  2. Receives the Google ID token
 *  3. Exchanges it for a Firebase credential
 *  4. Signs into Firebase
 */
class GoogleSignInHelper(
    private val activity: Activity,
    private val onSuccess: (isNewUser: Boolean) -> Unit,
    private val onFailure: (message: String) -> Unit
) {

    companion object {
        private const val TAG = "GoogleSignInHelper"
        const val RC_SIGN_IN = 9001
    }

    private val auth = FirebaseAuth.getInstance()

    private val client: GoogleSignInClient by lazy {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // Request the ID token so Firebase can verify the user
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(activity, options)
    }

    /**
     * Launch the Google account picker.
     * The activity must call [handleActivityResult] in its onActivityResult.
     */
    fun launch() {
        val signInIntent = client.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Call this from your Activity's onActivityResult with the same request code.
     */
    fun handleActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode != RC_SIGN_IN) return

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken)
            } else {
                onFailure("No ID token returned from Google")
            }
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign-in failed with code ${e.statusCode}", e)
            if (e.statusCode == 12501) {
                // User cancelled
                onFailure(activity.getString(R.string.auth_google_cancelled))
            } else {
                onFailure(activity.getString(R.string.auth_google_failed))
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                    Log.d(TAG, "Firebase auth success. New user: $isNewUser")
                    Log.d(TAG, "User: ${user?.email}")
                    onSuccess(isNewUser)
                } else {
                    Log.w(TAG, "Firebase credential sign-in failed", task.exception)
                    onFailure(task.exception?.message ?: activity.getString(R.string.auth_google_failed))
                }
            }
    }
}