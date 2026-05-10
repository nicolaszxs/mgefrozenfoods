package com.example.mgefrozenfoods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.TextView;

public class ProductsActivity extends AppCompatActivity {

    private RecyclerView rvProductsGrid;
    private TextView tvCategoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Link the XML IDs to Java
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProductsGrid = findViewById(R.id.rvProductsGrid);

        // Set the Grid (2 columns)
        rvProductsGrid.setLayoutManager(new GridLayoutManager(this, 2));

        // Kunin yung pinasang Category Name galing HomeActivity
        String category = getIntent().getStringExtra("CATEGORY_NAME");

        if (category != null && !category.isEmpty()) {
            tvCategoryTitle.setText(category); // Halimbawa: "Pork", "Beef", etc.
        } else {
            tvCategoryTitle.setText("All Products");
        }

        // Dito natin ikakabit yung Adapter para sa listahan ng frozen foods soon!
    }
}