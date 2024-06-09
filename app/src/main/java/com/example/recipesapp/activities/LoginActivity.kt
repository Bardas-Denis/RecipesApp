package com.example.recipesapp.activities


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.recipesapp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {

    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpTextView: TextView
    private lateinit var auth: FirebaseAuth

    private val THEME_PREF_KEY = "themePref"
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupViews()
    }

    public override fun onStart() {
        super.onStart()
        verifyUserState()
    }

    private fun setupViews()
    {
        setTheme()
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        usernameLayout = findViewById(R.id.etUsernameLayout)
        passwordLayout = findViewById(R.id.etPasswordLayout)
        loginButton = findViewById(R.id.loginButton)
        signUpTextView = findViewById(R.id.signUpTextView)
        auth = Firebase.auth
        loginButton.setOnClickListener {
            login()

        }
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setTheme()
    {
        sharedPrefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPrefs.getBoolean(THEME_PREF_KEY, false)
        if(isDarkModeEnabled)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun login()
    {
        //get username and password from edit text
        val username= usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if(username.isEmpty())
           usernameLayout .error = getString(R.string.error_required)
        else
            usernameLayout.error = null
        if(password.isEmpty())
            passwordLayout.error = getString(R.string.error_required)
        else
            passwordLayout.error = null

        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun verifyUserState()
    {
        val currentUser = auth.currentUser
        if (currentUser != null)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}