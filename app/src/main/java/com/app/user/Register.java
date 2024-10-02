package com.app.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.ead.R;
import com.app.service.NetworkRequest;

import org.json.JSONObject;

public class Register extends AppCompatActivity {

    private EditText firstName, lastName, email, phone, address, username, password;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        NetworkRequest networkRequest = new NetworkRequest(this);
        String URL = getString(R.string.backend_api);

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

                // For now, display a Toast message as feedback
                if (!firstNameInput.isEmpty() && !lastNameInput.isEmpty() && !emailInput.isEmpty() &&
                        !phoneInput.isEmpty() && !addressInput.isEmpty() && !usernameInput.isEmpty() &&
                        !passwordInput.isEmpty()) {
                    try {
                        // Prepare the JSON object with user data
                        JSONObject postData = new JSONObject();
                        postData.put("firstName", firstNameInput);
                        postData.put("lastName", lastNameInput);
                        postData.put("email", emailInput);
                        postData.put("phone", phoneInput);
                        postData.put("address", addressInput);
                        postData.put("username", usernameInput);
                        postData.put("password", passwordInput);

                        // Make a POST request
                        String jsonResponse = networkRequest.sendPostRequest(URL + "customer/register", postData);

                        if (jsonResponse != null) {
                            // Parse the JSON response
                            JSONObject responseJson = new JSONObject(jsonResponse);
                            if((int)responseJson.get("status") == 200){
                                Toast.makeText(Register.this, "Customer registered successfully.", Toast.LENGTH_SHORT).show();
                                // Optionally, start a new activity
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Handle the case where the response was null (error)
                            Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(Register.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Register.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
