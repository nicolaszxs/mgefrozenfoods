package com.example.mgefrozenfoods;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
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

        btnLogin.setOnClickListener(v -> {
            String inputUsername = etUsername.getText().toString().trim();
            String inputPassword = etPassword.getText().toString().trim();

            if (inputUsername.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);
            String savedUsername = sharedPreferences.getString("SAVED_USERNAME", "");
            String savedPassword = sharedPreferences.getString("SAVED_PASSWORD", "");
            String savedEmail = sharedPreferences.getString("SAVED_EMAIL", "No email linked");
            String savedMobile = sharedPreferences.getString("SAVED_MOBILE", "No mobile linked");

            if (inputUsername.equals(savedUsername) && inputPassword.equals(savedPassword)) {
                Toast.makeText(MainActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("USERNAME", savedUsername);
                intent.putExtra("EMAIL", savedEmail);
                intent.putExtra("MOBILE", savedMobile);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
