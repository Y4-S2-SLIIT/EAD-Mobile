package com.app.user;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.ead.R;

public class Register extends AppCompatActivity {

    private EditText firstName, lastName, email, phone, address, username, password;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);

        // Set onClickListener for the Register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture input data
                String firstNameInput = firstName.getText().toString();
                String lastNameInput = lastName.getText().toString();
                String emailInput = email.getText().toString();
                String phoneInput = phone.getText().toString();
                String addressInput = address.getText().toString();
                String usernameInput = username.getText().toString();
                String passwordInput = password.getText().toString();

                // Implement your registration logic here
                // For now, display a Toast message as feedback
                if (!firstNameInput.isEmpty() && !lastNameInput.isEmpty() && !emailInput.isEmpty() &&
                        !phoneInput.isEmpty() && !addressInput.isEmpty() && !usernameInput.isEmpty() &&
                        !passwordInput.isEmpty()) {
                    // Example: Display the inputs in a Toast message (just for testing)
                    Toast.makeText(Register.this, "Registration successful!", Toast.LENGTH_SHORT).show();

                    // Here you would typically send the data to the server for registration
                } else {
                    Toast.makeText(Register.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
