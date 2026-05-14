package com.example.mgefrozenfoods;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegUsername, etRegPassword, etRegConfirmPassword;
    private EditText etRegLastName, etRegFirstName, etRegMI;
    private EditText etRegMobile, etRegEmail;
    private Spinner spinRegCity, spinRegBrgy;
    private EditText etRegZip, etRegHouseStreet;
    private Button btnSubmitRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegUsername = findViewById(R.id.etRegUsername);
        etRegPassword = findViewById(R.id.etRegPassword);
        etRegConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        etRegLastName = findViewById(R.id.etRegLastName);
        etRegFirstName = findViewById(R.id.etRegFirstName);
        etRegMI = findViewById(R.id.etRegMI);
        etRegMobile = findViewById(R.id.etRegMobile);
        etRegEmail = findViewById(R.id.etRegEmail);
        spinRegCity = findViewById(R.id.spinRegCity);
        spinRegBrgy = findViewById(R.id.spinRegBrgy);
        etRegZip = findViewById(R.id.etRegZip);
        etRegHouseStreet = findViewById(R.id.etRegHouseStreet);
        btnSubmitRegister = findViewById(R.id.btnSubmitRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        String[] cities = {"Select City", "Makati"};

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(android.graphics.Color.GRAY);
                } else {
                    tv.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
        };
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRegCity.setAdapter(cityAdapter);

        spinRegCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                updateBarangaySpinner(selectedCity);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSubmitRegister.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirm = etRegConfirmPassword.getText().toString().trim();
            String email = etRegEmail.getText().toString().trim();
            String mobile = etRegMobile.getText().toString().trim();

            if (spinRegCity.getSelectedItemPosition() == 0 || spinRegBrgy.getSelectedItemPosition() == 0) {
                Toast.makeText(RegisterActivity.this, "Please select City and Barangay", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || mobile.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SAVED_USERNAME", username);
            editor.putString("SAVED_PASSWORD", password);
            editor.putString("SAVED_EMAIL", email);
            editor.putString("SAVED_MOBILE", mobile);

            String existingUsers = sharedPreferences.getString("ALL_USERS", "");
            String newUserStr = username + "||" + email + "||" + mobile;
            editor.putString("ALL_USERS", newUserStr + "##" + existingUsers);
            editor.apply();

            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void updateBarangaySpinner(String city) {
        List<String> barangays = new ArrayList<>();
        barangays.add("Select Barangay");

        switch (city) {
            case "Makati":
                barangays.addAll(Arrays.asList(
                        "Bangkal", "Bel-Air", "Carmona", "Dasmariñas",
                        "Guadalupe Nuevo", "Guadalupe Viejo", "Kasilawan", "La Paz",
                        "Magallanes", "Olympia", "Palanan", "Pinagkaisahan",
                        "Pio del Pilar", "Poblacion", "Post Proper Northside",
                        "Post Proper Southside", "San Antonio", "San Lorenzo",
                        "Santa Cruz", "Singkamas", "Tejeros", "Urdaneta",
                        "Valenzuela"
                ));
                break;
        }

        ArrayAdapter<String> brgyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, barangays) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(android.graphics.Color.GRAY);
                } else {
                    tv.setTextColor(android.graphics.Color.BLACK);
                }
                return view;
            }
        };
        brgyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRegBrgy.setAdapter(brgyAdapter);
    }
}
