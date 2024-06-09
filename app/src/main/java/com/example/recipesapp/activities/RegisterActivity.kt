package com.example.recipesapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipesapp.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity: AppCompatActivity(){

    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var verifyPasswordLayout: TextInputLayout
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var verifyPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupViews()
    }

    private fun setupViews()
    {
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        verifyPasswordEditText = findViewById(R.id.verifyPasswordEditText)
        firstNameLayout = findViewById(R.id.etFirstNameLayout)
        lastNameLayout = findViewById(R.id.etLastNameLayout)
        usernameLayout = findViewById(R.id.etUsernameLayout)
        passwordLayout = findViewById(R.id.etPasswordLayout)
        verifyPasswordLayout = findViewById(R.id.etVerifyPasswordLayout)
        registerButton = findViewById(R.id.registerButton)
        auth = Firebase.auth

        registerButton.setOnClickListener {
            register()
        }
    }

    private fun register()
    {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val username= usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val verifyPassword = verifyPasswordEditText.text.toString().trim()
        var bool = true

        if(firstName.isEmpty())
        {
            firstNameLayout.error = getString(R.string.error_required)
            bool = false
        }
        else
            firstNameLayout.error = null
        if(lastName.isEmpty())
        {
            lastNameLayout.error = getString(R.string.error_required)
            bool = false
        }
        else
            lastNameLayout.error = null
        if(username.isEmpty())
        {
            usernameLayout .error = getString(R.string.error_required)
            bool = false
        }
        else
            usernameLayout.error = null
        if(password.isEmpty())
        {
            passwordLayout.error = getString(R.string.error_required)
            bool = false
        }
            else
            passwordLayout.error = null
        if(verifyPassword.isEmpty())
        {
            verifyPasswordLayout.error = getString(R.string.error_required)
            bool = false
        }
        else
            verifyPasswordLayout.error = null
        if(bool)
        {
            if(password==verifyPassword)
            {
                auth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful)
                        {
                            Log.d("Task", "createUserWithEmail:success")
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else
                        {
                            Log.w("Task", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Registration failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else
                verifyPasswordLayout.error= getString(R.string.error_pass_do_not_match)
        }
    }
}