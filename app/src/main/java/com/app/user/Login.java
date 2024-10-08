package com.app.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.app.ead.MainActivity;
import com.app.ead.MainBottomActivity;
import com.app.ead.R;
import com.app.service.NetworkRequest;

import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton, registerButton;
    private SharedPreferences sharedPreferences; // Declare SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        NetworkRequest networkRequest = new NetworkRequest(this);
        // API URL for login
        String URL = getString(R.string.backend_api) + "customer/login"; // Append login endpoint

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);

        // Initialize UI elements
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        // Set login button click listener
        loginButton.setOnClickListener(v -> {
            String usernameInput = username.getText().toString();
            String passwordInput = password.getText().toString();

            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(Login.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    // Prepare JSON request body
                    JSONObject postData = new JSONObject();
                    postData.put("username", usernameInput);
                    postData.put("password", passwordInput);

                    String response = networkRequest.sendPostRequest(URL, postData);

                    if (response != null) {
                        // Assuming the response is a JSON string
                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.has("token")) {
                            String token = jsonResponse.getString("token");
                            String cus_id = jsonResponse.getString("customerId");
                            boolean isVerified = jsonResponse.getBoolean("isVerified"); // Check verification status
                            boolean isDeactivated = jsonResponse.getBoolean("isDeactivated"); // Check deactivation status

                            if (isDeactivated) {
                                Toast.makeText(Login.this, "Your account is deactivated.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (!isVerified) {
                                Toast.makeText(Login.this, "Your account is not verified.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Save token in SharedPreferences
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("auth_token", token);
                            editor.putString("cus_id", cus_id);
                            editor.apply(); // Don't forget to commit the changes

                            // Optionally, start a new activity
                            Intent intent = new Intent(Login.this, MainBottomActivity.class); // Change to your main activity
                            startActivity(intent);
                            finish(); // Finish the Login activity
                        } else {
                            Toast.makeText(Login.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "Login failed. No response from server.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Login.this, "An error occurred.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set register button click listener
        registerButton.setOnClickListener(v -> {
            // Redirect to the Register activity
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }
}
