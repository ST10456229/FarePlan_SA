package com.example.fareplansa

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.common.SignInButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToSignUp: TextView
    private lateinit var btnGoogleSignIn: SignInButton
    private lateinit var googleHelper: GoogleSignInHelper

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etLoginEmail)
        etPassword = findViewById(R.id.etLoginPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Input validation
            if (email.isEmpty() || password.isEmpty()) {
                showToast(getString(R.string.login_fill_all))
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast(getString(R.string.auth_error_invalid_email))
                return@setOnClickListener
            }

            // 2. Network check
            if (!isOnline()) {
                showToast(getString(R.string.auth_error_network))
                return@setOnClickListener
            }

            // 3. Disable button to prevent double-tap
            btnLogin.isEnabled = false
            btnLogin.text = getString(R.string.login_signing_in)

            signIn(email, password)
        }

        tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        btnGoogleSignIn.setSize(SignInButton.SIZE_WIDE)

        googleHelper = GoogleSignInHelper(
            activity = this,
            onSuccess = { isNewUser ->
                // Ensure Firestore profile exists
                ensureUserProfile()
                Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            },
            onFailure = { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        )

        btnGoogleSignIn.setOnClickListener {
            googleHelper.launch()
        }

    }

    private fun signIn(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // Re-enable button in every outcome
                    btnLogin.isEnabled = true
                    btnLogin.text = getString(R.string.login_button)

                    if (task.isSuccessful) {
                        showToast(getString(R.string.login_success))
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    } else {
                        // Translate the exception into a friendly message
                        val message = AuthErrorMapper.toMessage(this, task.exception)
                        showToast(message)
                    }
                }
        } catch (e: Exception) {
            btnLogin.isEnabled = true
            btnLogin.text = getString(R.string.login_button)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleHelper.handleActivityResult(requestCode, data)
    }

    private fun ensureUserProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val userId = user.uid

        val prefs = getSharedPreferences("fareplan_prefs", MODE_PRIVATE)
        val profile = mapOf(
            "userId" to userId,
            "displayName" to (user.displayName ?: user.email ?: "Traveller"),
            "email" to (user.email ?: ""),
            "homeCurrency" to (prefs.getString("home_currency", "ZAR") ?: "ZAR"),
            "language" to (prefs.getString("language", "en") ?: "en"),
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        FirebaseFirestore.getInstance()
            .collection("users").document(userId)
            .set(profile, SetOptions.merge())
    }
}