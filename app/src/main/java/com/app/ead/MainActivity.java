package com.app.ead;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Allow network operations in the main thread (not recommended for production)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Start Login Activity
        Intent intent = new Intent(MainActivity.this, com.app.user.Login.class);
        startActivity(intent);
        finish();  // Close MainActivity so the user can't return to it
    }
}
