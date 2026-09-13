package com.example.fareplansa

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FarePlanMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM"
    }

    /**
     * Called when a new FCM token is generated.
     * This happens:
     *  - When the app is first installed
     *  - When the token is refreshed by Firebase
     *  - When the user restores the app on a new device
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        saveTokenToFirestore(token)
    }

    /**
     * Saves the FCM token under users/{uid}.fcmToken in Firestore
     * so the Cloud Function can send push notifications to this device.
     */
    private fun saveTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.w(TAG, "Cannot save token — user not logged in")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnSuccessListener { Log.d(TAG, "FCM token saved for user $userId") }
            .addOnFailureListener { e -> Log.w(TAG, "Failed to save FCM token", e) }
    }

    /**
     * Called when a data or notification message is received while the app is in the foreground.
     * Background messages are handled by the system tray automatically.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message from: ${message.from}")
        message.notification?.let {
            Log.d(TAG, "Notification title: ${it.title}")
            Log.d(TAG, "Notification body: ${it.body}")
        }
    }
}