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


        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        rvProductsGrid = findViewById(R.id.rvProductsGrid);


        rvProductsGrid.setLayoutManager(new GridLayoutManager(this, 2));

        String category = getIntent().getStringExtra("CATEGORY_NAME");

        if (category != null && !category.isEmpty()) {
            tvCategoryTitle.setText(category); 
        } else {
            tvCategoryTitle.setText("All Products");
        }


    }
}
