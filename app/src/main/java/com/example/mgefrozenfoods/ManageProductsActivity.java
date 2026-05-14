package com.example.mgefrozenfoods;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ManageProductsActivity extends AppCompatActivity {

    private RecyclerView rvAdminProducts;
    private AdminProductAdapter adapter;
    private List<AdminProduct> adminProductList;
    private TextView tvLowStockAlert;
    private EditText etName, etPrice, etStock;
    private Spinner spinCategory, spinBrand;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_products);

        rvAdminProducts = findViewById(R.id.rvAdminProducts);
        tvLowStockAlert = findViewById(R.id.tvLowStockAlert);
        etName = findViewById(R.id.etNewProductName);
        etPrice = findViewById(R.id.etNewProductPrice);
        etStock = findViewById(R.id.etNewProductStock);
        spinCategory = findViewById(R.id.spinCategory);
        spinBrand = findViewById(R.id.spinBrand);
        btnAdd = findViewById(R.id.btnAddProduct);

        String[] categories = {"Meat", "Seafood", "Snacks", "Dairy", "Others"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCategory.setAdapter(catAdapter);

        String[] brands = {"UNBRANDED", "PUREFOODS", "FOOD CRAFTERS", "PROMEAT", "CDO", "BOUNTY FRESH", "TOP MEAT"};
        ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, brands);
        brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinBrand.setAdapter(brandAdapter);

        rvAdminProducts.setLayoutManager(new LinearLayoutManager(this));

        adminProductList = new ArrayList<>();
        adminProductList.add(new AdminProduct("Bacon", "₱350.00", "FOOD CRAFTERS", 12, R.drawable.baconn));
        adminProductList.add(new AdminProduct("Promeat Longgadog", "₱270.00", "PROMEAT", 3, R.drawable.longg));
        adminProductList.add(new AdminProduct("Mozza Bomb", "₱240.00", "UNBRANDED", 15, R.drawable.moz));
        adminProductList.add(new AdminProduct("PopCorn Chicken", "₱180.00", "UNBRANDED", 8, R.drawable.papi));
        adminProductList.add(new AdminProduct("Pork Tonkatsu", "₱350.00", "UNBRANDED", 2, R.drawable.idk));
        adminProductList.add(new AdminProduct("Squid Rings", "₱220.00", "UNBRANDED", 20, R.drawable.rings));
        adminProductList.add(new AdminProduct("Promeat Hungarian", "₱350.00", "PROMEAT", 5, R.drawable.promeathungadog));
        adminProductList.add(new AdminProduct("Promeat Ham", "₱320.00", "PROMEAT", 4, R.drawable.ham));
        adminProductList.add(new AdminProduct("Luncheon Meat", "₱280.00", "UNBRANDED", 18, R.drawable.lunch));
        adminProductList.add(new AdminProduct("Hotdog Bacon Wrap", "₱210.00", "UNBRANDED", 10, R.drawable.rap));
        adminProductList.add(new AdminProduct("Beef Tapa", "₱280.00", "PUREFOODS", 15, R.drawable.bip));
        adminProductList.add(new AdminProduct("Lechon Roll", "₱320.00", "UNBRANDED", 7, R.drawable.laman));
        adminProductList.add(new AdminProduct("Bangus", "₱190.00", "UNBRANDED", 25, R.drawable.daing));
        adminProductList.add(new AdminProduct("CDO FUNTASTYK PORK TOCINO", "₱190.00", "CDO", 2, R.drawable.cdo_funtastyk_pork_tocino));
        adminProductList.add(new AdminProduct("Carneval Spicy Hungarian Sausage", "₱350.00", "UNBRANDED", 6, R.drawable.spicysausage));
        adminProductList.add(new AdminProduct("Nuggets", "₱420.00", "BOUNTY FRESH", 11, R.drawable.nuggets));
        adminProductList.add(new AdminProduct("Black Pepper Chicken Nuggets", "₱270.00", "BOUNTY FRESH", 1, R.drawable.black));
        adminProductList.add(new AdminProduct("Crispy Chicken Cop", "₱250.00", "UNBRANDED", 14, R.drawable.cop));
        adminProductList.add(new AdminProduct("Rib Steak", "₱540.00", "UNBRANDED", 4, R.drawable.ribcage));
        adminProductList.add(new AdminProduct("Garlic Longganisang Calumpit", "₱180.00", "UNBRANDED", 22, R.drawable.calumpit));
        adminProductList.add(new AdminProduct("Chicken Fingers", "₱190.00", "BOUNTY FRESH", 9, R.drawable.fingers));
        adminProductList.add(new AdminProduct("Tempura Ebi", "₱295.00", "UNBRANDED", 3, R.drawable.temp));
        adminProductList.add(new AdminProduct("Purefoods TenderJuicy Hotdog", "₱270.00", "PUREFOODS", 30, R.drawable.reddog));
        adminProductList.add(new AdminProduct("Topmeat Pork Tocino", "₱290.00", "TOP MEAT", 16, R.drawable.toptocino));
        adminProductList.add(new AdminProduct("Beef Trimming", "₱290.00", "UNBRANDED", 8, R.drawable.trim));
        adminProductList.add(new AdminProduct("Japanese Sausage Bacon Wrap", "₱240.00", "UNBRANDED", 5, R.drawable.japwrap));
        adminProductList.add(new AdminProduct("Big Siomai", "₱170.00", "UNBRANDED", 19, R.drawable.big));
        adminProductList.add(new AdminProduct("Purefoods TenderJuicy Hotdog Chicken Cheese", "₱270.00", "PUREFOODS", 12, R.drawable.browndog));
        adminProductList.add(new AdminProduct("Mozzarella Cheese", "₱180.00", "UNBRANDED", 4, R.drawable.cheesee));
        adminProductList.add(new AdminProduct("Denorado Mindoro Rice Sack", "₱1450.00", "UNBRANDED", 10, R.drawable.denorado));

        adapter = new AdminProductAdapter(adminProductList, position -> {
            adminProductList.remove(position);
            updateDashboard();
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
        });
        rvAdminProducts.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String stock = etStock.getText().toString().trim();
            String brand = spinBrand.getSelectedItem().toString();

            if (!name.isEmpty() && !price.isEmpty() && !stock.isEmpty()) {
                adminProductList.add(0, new AdminProduct(
                        name,
                        "₱" + price,
                        brand,
                        Integer.parseInt(stock),
                        R.drawable.lunch
                ));
                updateDashboard();
                etName.setText(""); etPrice.setText(""); etStock.setText("");
                Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        updateDashboard();
    }

    private void updateDashboard() {
        adapter.notifyDataSetChanged();
        int lowStockCount = 0;
        for (AdminProduct p : adminProductList) {
            if (p.getStock() < 5) {
                lowStockCount++;
            }
        }
        tvLowStockAlert.setText("Low Stock Alerts: " + lowStockCount + " products");
    }
}
