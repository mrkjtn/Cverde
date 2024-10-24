package com.cv.cverde;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    private EditText txtFname, txtLname, txtEmail, txtpass, txtconpass;
    private Button SignUpbtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private TextView textViewLoginNow;

    @Override
    public void onStart() {
        super.onStart();
        // Initialize Firebase Auth instance
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to MainActivity
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find Views
        txtFname = findViewById(R.id.txtFname);
        txtLname = findViewById(R.id.txtLname);
        txtEmail = findViewById(R.id.txtEmail);
        txtpass = findViewById(R.id.txtpass);
        txtconpass = findViewById(R.id.txtconpass);
        SignUpbtn = findViewById(R.id.SignUpbtn);
        progressBar = findViewById(R.id.progressBar); // Initialize progressBar
        textViewLoginNow = findViewById(R.id.loginNow);

        // Set Sign-Up button click listener
        SignUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        // Redirect to login page if user clicks "Login Now"
        textViewLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUpUser() {
        // Get user input
        String firstName = txtFname.getText().toString().trim();
        String lastName = txtLname.getText().toString().trim();
        String email = txtEmail.getText().toString().trim();
        String password = txtpass.getText().toString().trim();
        String confirmPassword = txtconpass.getText().toString().trim();

        // Input validation
        if (TextUtils.isEmpty(firstName)) {
            txtFname.setError("First name is required.");
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            txtLname.setError("Last name is required.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            txtpass.setError("Password is required.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            txtconpass.setError("Passwords do not match.");
            return;
        }

        // Show progressBar while signing up
        progressBar.setVisibility(View.VISIBLE);

        // Create a new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide progressBar after task completion
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign-up successful, update the user profile
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(firstName + " " + lastName)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(Register.this, "User registered successfully.", Toast.LENGTH_SHORT).show();
                                            // Redirect to MainActivity or home screen
                                            Intent intent = new Intent(Register.this, MainActivity.class);
                                            startActivity(intent);
                                            finish(); // Close the register activity
                                        }
                                    });
                        }
                    } else {
                        // If sign-up fails, display a message
                        Toast.makeText(Register.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}