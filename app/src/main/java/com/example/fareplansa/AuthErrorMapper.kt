package com.example.fareplansa

import android.content.Context
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException

/**
 * Translates Firebase Authentication exceptions into user-friendly,
 * localized messages.
 */
object AuthErrorMapper {

    fun toMessage(context: Context, exception: Exception?): String {
        return when (exception) {

            is FirebaseAuthWeakPasswordException ->
                context.getString(R.string.auth_error_weak_password)

            is FirebaseAuthInvalidCredentialsException ->
                context.getString(R.string.auth_error_invalid_credentials)

            is FirebaseAuthInvalidUserException ->
                context.getString(R.string.auth_error_user_not_found)

            is FirebaseAuthUserCollisionException ->
                context.getString(R.string.auth_error_email_in_use)

            is FirebaseAuthRecentLoginRequiredException ->
                context.getString(R.string.auth_error_recent_login)

            is FirebaseAuthException -> {
                when (exception.errorCode) {
                    "ERROR_NETWORK_REQUEST_FAILED" ->
                        context.getString(R.string.auth_error_network)
                    "ERROR_INVALID_EMAIL" ->
                        context.getString(R.string.auth_error_invalid_email)
                    "ERROR_USER_DISABLED" ->
                        context.getString(R.string.auth_error_user_disabled)
                    "ERROR_TOO_MANY_REQUESTS" ->
                        context.getString(R.string.auth_error_too_many_requests)
                    else ->
                        context.getString(R.string.auth_error_generic)
                }
            }

            else -> context.getString(R.string.auth_error_generic)
        }
    }
}