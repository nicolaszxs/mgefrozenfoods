package com.example.mgefrozenfoods;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.equals("admin") && password.equals("admin")) {
                    Toast.makeText(MainActivity.this, "Welcome to Admin Panel!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

                SharedPreferences sharedPreferences = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);

                String savedUser = sharedPreferences.getString("SAVED_USERNAME", "");
                String savedPass = sharedPreferences.getString("SAVED_PASSWORD", "");

                if (username.equals(savedUser) && password.equals(savedPass)) {
                    loginSuccess(username);
                    return;
                }

                String allUsers = sharedPreferences.getString("ALL_USERS", "");
                if (!allUsers.isEmpty()) {
                    String[] userEntries = allUsers.split("##");
                    for (String entry : userEntries) {
                        if (entry.trim().isEmpty()) continue;

                        String[] parts = entry.split("\\|\\|");
                        if (parts.length >= 2) {
                            String registeredUser = parts[0];
                            if (username.equals(registeredUser) && password.equals(savedPass)) {
                                loginSuccess(username);
                                return;
                            }
                        }
                    }
                }

                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginSuccess(String username) {
        Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
        finish();
    }
}
