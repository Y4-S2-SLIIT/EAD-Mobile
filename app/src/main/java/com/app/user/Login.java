package com.app.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.ead.R;

public class Login extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(Login.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Handle login logic here
                Toast.makeText(Login.this, "Login clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Set register button click listener
        registerButton.setOnClickListener(v -> {
            // Redirect to the Register activity
            Intent intent = new Intent(Login.this, com.app.user.Register.class);
            startActivity(intent);
        });
    }
}
