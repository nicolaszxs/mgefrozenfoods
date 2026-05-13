package com.example.mgefrozenfoods;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RegisteredUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_users);

        ListView lvRegisteredUsers = findViewById(R.id.lvRegisteredUsers);
        ArrayList<String> usersList = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);
        String existingUsers = prefs.getString("ALL_USERS", "");

        String oldUsername = prefs.getString("SAVED_USERNAME", "");
        String oldEmail = prefs.getString("SAVED_EMAIL", "No email linked");
        String oldMobile = prefs.getString("SAVED_MOBILE", "No mobile linked");

        if (!oldUsername.isEmpty() && !existingUsers.contains(oldUsername)) {
            usersList.add("👤 Username: @" + oldUsername + "\n📧 Email: " + oldEmail + "\n📱 Mobile: " + oldMobile);
        }

        if (!existingUsers.isEmpty()) {
            String[] users = existingUsers.split("##");
            for (String u : users) {
                if (u.trim().isEmpty()) continue;
                String[] parts = u.split("\\|\\|");
                if (parts.length == 3) {
                    if (!parts[0].equals(oldUsername)) {
                        usersList.add("👤 Username: @" + parts[0] + "\n📧 Email: " + parts[1] + "\n📱 Mobile: " + parts[2]);
                    }
                }
            }
        }

        if (usersList.isEmpty()) {
            usersList.add("No registered users yet.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        lvRegisteredUsers.setAdapter(adapter);
    }
}