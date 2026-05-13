package com.example.mgefrozenfoods;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView rvAdminUsers;
    private AdminUserAdapter adapter;
    private List<AdminUser> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        rvAdminUsers = findViewById(R.id.rvAdminUsers);
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();

        SharedPreferences adminPrefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
        String existingOrders = adminPrefs.getString("ALL_ORDERS", "");

        if (!existingOrders.isEmpty()) {
            String[] orders = existingOrders.split("##");
            for (String o : orders) {
                if (o.trim().isEmpty()) continue;
                String[] p = o.split("\\|\\|");
                if (p.length == 6) {
                    userList.add(new AdminUser(p[0], p[1], p[2], p[3], p[4], p[5]));
                }
            }
        }

        adapter = new AdminUserAdapter(userList);
        rvAdminUsers.setAdapter(adapter);
    }
}