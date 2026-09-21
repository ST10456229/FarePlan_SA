package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvGoToLogin: TextView
    private lateinit var btnSignUp: Button

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        etName = findViewById(R.id.etSignUpName)
        etEmail = findViewById(R.id.etSignUpEmail)
        etPassword = findViewById(R.id.etSignUpPassword)
        etConfirmPassword = findViewById(R.id.etSignUpConfirmPassword)
        tvGoToLogin = findViewById(R.id.tvGoToLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        tvGoToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirmPassword.text.toString().trim()

            // 1. Validation
            if (name.isEmpty()) {
                showToast(getString(R.string.signup_enter_name))
                return@setOnClickListener
            }
            if (email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.signup_fill_all))
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.auth_error_invalid_email))
                return@setOnClickListener
            }
            if (password != confirm) {
                showToast(getString(R.string.signup_password_mismatch))
                return@setOnClickListener
            }
            if (password.length < 6) {
                showToast(getString(R.string.signup_password_short))
                return@setOnClickListener
            }

            // 2. Network check
            if (!isOnline()) {
                showToast(getString(R.string.auth_error_network))
                return@setOnClickListener
            }

            // 3. Prevent double-tap
            btnSignUp.isEnabled = false
            btnSignUp.text = getString(R.string.signup_creating)

            createUser(name, email, password)
        }
    }

    private fun createUser(name: String, email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    btnSignUp.isEnabled = true
                    btnSignUp.text = getString(R.string.signup_button)

                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = auth.currentUser?.uid

                        if (userId != null) {
                            val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
                            val userProfile = mapOf(
                                "userId" to userId,
                                "displayName" to name,
                                "email" to email,
                                "homeCurrency" to (prefs.getString("home_currency", "ZAR") ?: "ZAR"),
                                "language" to (prefs.getString("language", "en") ?: "en"),
                                "createdAt" to com.google.firebase.Timestamp.now()
                            )

                            db.collection("users").document(userId)
                                .set(userProfile)
                                .addOnSuccessListener { Log.d(TAG, "Profile saved") }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Profile save failed", e)
                                    // Don't block the user — account exists, profile is secondary
                                }
                        }

                        showToast(getString(R.string.signup_success))
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        val message = AuthErrorMapper.toMessage(this, task.exception)
                        showToast(message)
                    }
                }
        } catch (e: Exception) {
            btnSignUp.isEnabled = true
            btnSignUp.text = getString(R.string.signup_button)
            Log.e(TAG, "Unexpected error during signup", e)
            showToast(getString(R.string.auth_error_generic))
        }
    }

    private fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}