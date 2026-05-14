package com.example.mgefrozenfoods;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ScrollView homeContent, cartView, checkoutView, profileView;
    private LinearLayout productView, ordersView, sectionLearn, layoutGCashDetails;
    private TextView tvSelectedCategory, tvCartTotalItems, tvCartGrandTotal, tvCheckoutTotal;
    private RecyclerView rvProducts, rvCartItems, rvOrders;
    private ViewPager2 viewPagerCarousel;

    private TextView tvProfileWelcome, tvProfileUsername, tvProfileEmail, tvProfileMobile;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCOD, rbGCash;
    private EditText etCheckoutAddress, etCheckoutContactNumber, etCheckoutMobile, etCheckoutReference, etSearch;

    private Handler sliderHandler = new Handler();
    private String currentCategory = "";
    private double currentGrandTotal = 0;

    private ProductAdapter productAdapter;
    private List<Product> allProductsList;

    private CartAdapter cartAdapter;
    private List<CartItem> cartList;

    private OrderAdapter orderAdapter;
    private List<Order> orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeContent = findViewById(R.id.homeContent);
        productView = findViewById(R.id.productView);
        ordersView = findViewById(R.id.ordersView);
        profileView = findViewById(R.id.profileView);
        cartView = findViewById(R.id.cartView);
        checkoutView = findViewById(R.id.checkoutView);
        sectionLearn = findViewById(R.id.sectionLearn);
        tvSelectedCategory = findViewById(R.id.tvSelectedCategory);
        rvProducts = findViewById(R.id.rvProducts);
        viewPagerCarousel = findViewById(R.id.viewPagerCarousel);

        tvProfileWelcome = findViewById(R.id.tvProfileWelcome);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileMobile = findViewById(R.id.tvProfileMobile);

        tvCartTotalItems = findViewById(R.id.tvCartTotalItems);
        tvCartGrandTotal = findViewById(R.id.tvCartGrandTotal);
        rvCartItems = findViewById(R.id.rvCartItems);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCOD = findViewById(R.id.rbCOD);
        rbGCash = findViewById(R.id.rbGCash);
        layoutGCashDetails = findViewById(R.id.layoutGCashDetails);
        etCheckoutAddress = findViewById(R.id.etCheckoutAddress);
        etCheckoutContactNumber = findViewById(R.id.etCheckoutContactNumber);
        etCheckoutMobile = findViewById(R.id.etCheckoutMobile);
        etCheckoutReference = findViewById(R.id.etCheckoutReference);
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal);

        etSearch = findViewById(R.id.etSearch);
        rvOrders = findViewById(R.id.rvOrders);

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(orderAdapter);

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartList, position -> {
            cartList.remove(position);
            cartAdapter.notifyDataSetChanged();
            calculateCartTotal();
            Toast.makeText(this, "Item removed from cart", Toast.LENGTH_SHORT).show();
        });
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        loadAllProducts();
        productAdapter = new ProductAdapter(allProductsList, product -> {
            boolean exists = false;
            for (CartItem item : cartList) {
                if (item.getProduct().getName().equals(product.getName())) {
                    item.setQuantity(item.getQuantity() + 1);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                cartList.add(new CartItem(product, 1));
            }
            calculateCartTotal();
            showCart();
            Toast.makeText(this, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
        rvProducts.setAdapter(productAdapter);

        setupCarousel();
        loadUserData();

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String query = s.toString().toLowerCase().trim();
                    if (query.isEmpty()) {
                        if (currentCategory.equals("Search Results")) {
                            showHome();
                        } else {
                            filterProducts(currentCategory.isEmpty() ? "All Brands" : currentCategory);
                        }
                        return;
                    }

                    if (productView.getVisibility() != View.VISIBLE) {
                        showProducts("Search Results");
                    } else {
                        tvSelectedCategory.setText("Search Results");
                        currentCategory = "Search Results";
                    }

                    List<Product> searchResults = new ArrayList<>();
                    for (Product p : allProductsList) {
                        if (p.getName().toLowerCase().contains(query) || p.getBrand().toLowerCase().contains(query)) {
                            searchResults.add(p);
                        }
                    }
                    productAdapter.updateList(searchResults);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        Button btnLearnMore = findViewById(R.id.btnLearnMore);
        btnLearnMore.setOnClickListener(v -> homeContent.smoothScrollTo(0, sectionLearn.getTop()));

        setupCategoryToggle(R.id.btnAll, "All Brands");
        setupCategoryToggle(R.id.btnPurefoods, "PUREFOODS");
        setupCategoryToggle(R.id.btnFoodCrafters, "FOOD CRAFTERS");
        setupCategoryToggle(R.id.btnPromeat, "PROMEAT");
        setupCategoryToggle(R.id.btnCdo, "CDO");
        setupCategoryToggle(R.id.btnBountyFresh, "BOUNTY FRESH");
        setupCategoryToggle(R.id.btnTopMeat, "TOP MEAT");
        setupCategoryToggle(R.id.btnUnbranded, "UNBRANDED");

        findViewById(R.id.btnCartHeader).setOnClickListener(v -> showCart());

        findViewById(R.id.btnUpdateCart).setOnClickListener(v -> {
            cartAdapter.notifyDataSetChanged();
            calculateCartTotal();
            Toast.makeText(this, "Cart updated!", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnContinueShopping).setOnClickListener(v -> showHome());

        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                showCheckout();
            }
        });

        findViewById(R.id.btnCancelCheckout).setOnClickListener(v -> showCart());

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbGCash) {
                layoutGCashDetails.setVisibility(View.VISIBLE);
            } else {
                layoutGCashDetails.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> {
            String address = etCheckoutAddress.getText().toString().trim();
            String contact = etCheckoutContactNumber.getText().toString().trim();

            if (address.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "Please enter your delivery address and contact number.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rbGCash.isChecked()) {
                String mobile = etCheckoutMobile.getText().toString().trim();
                String refNum = etCheckoutReference.getText().toString().trim();

                if (mobile.isEmpty() || refNum.isEmpty()) {
                    Toast.makeText(this, "Please complete GCash details.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            StringBuilder itemsSummary = new StringBuilder();
            for (int i = 0; i < cartList.size(); i++) {
                CartItem item = cartList.get(i);
                itemsSummary.append(item.getProduct().getName()).append(" (Qty: ").append(item.getQuantity()).append(")");
                if (i < cartList.size() - 1) {
                    itemsSummary.append(", ");
                }
            }

            String newOrderId = "ORD-" + System.currentTimeMillis();
            Order newOrder = new Order(newOrderId, currentDate, "Pending", itemsSummary.toString(), currentGrandTotal);
            orderList.add(0, newOrder);
            orderAdapter.notifyDataSetChanged();

            saveUserOrder(newOrder);

            SharedPreferences adminPrefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
            String existingAdminOrders = adminPrefs.getString("ALL_ORDERS", "");

            String customerName = tvProfileUsername.getText().toString();
            String contactAddress = address + " / " + contact;
            String paymentTotal = (rbGCash.isChecked() ? "GCash" : "COD") + " - Total: ₱" + String.format("%.2f", currentGrandTotal);

            String adminEntry = newOrderId + "||" + customerName + "||" + contactAddress + "||" + itemsSummary.toString() + "||" + paymentTotal + "||Pending";
            adminPrefs.edit().putString("ALL_ORDERS", adminEntry + "##" + existingAdminOrders).apply();

            cartList.clear();
            cartAdapter.notifyDataSetChanged();
            calculateCartTotal();

            etCheckoutAddress.setText("");
            etCheckoutContactNumber.setText("");
            etCheckoutMobile.setText("");
            etCheckoutReference.setText("");
            rbCOD.setChecked(true);

            Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
            showOrders();
        });

        findViewById(R.id.navHome).setOnClickListener(v -> showHome());

        findViewById(R.id.navProductsTab).setOnClickListener(v -> {
            if (productView.getVisibility() != View.VISIBLE || !currentCategory.equals("All Brands")) {
                showProducts("All Brands");
            }
        });

        findViewById(R.id.navOrders).setOnClickListener(v -> showOrders());
        findViewById(R.id.navProfile).setOnClickListener(v -> showProfile());

        findViewById(R.id.btnEditDetails).setOnClickListener(v -> {
            Toast.makeText(this, "Edit Details Clicked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnUserLogout).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void calculateCartTotal() {
        int totalItems = 0;
        currentGrandTotal = 0;
        for (CartItem item : cartList) {
            totalItems += item.getQuantity();
            currentGrandTotal += item.getSubtotal();
        }
        tvCartTotalItems.setText(String.valueOf(totalItems));
        tvCartGrandTotal.setText(String.format("₱%.2f", currentGrandTotal));
        tvCheckoutTotal.setText(String.format("₱%.2f", currentGrandTotal));
    }

    private void loadUserData() {
        String username = getIntent().getStringExtra("USERNAME");
        String email = getIntent().getStringExtra("EMAIL");
        String mobile = getIntent().getStringExtra("MOBILE");

        if (username != null) {
            tvProfileWelcome.setText("Welcome, " + username + "!");
            tvProfileUsername.setText(username);
            tvProfileEmail.setText(email != null ? email : "No email linked");
            tvProfileMobile.setText(mobile != null ? mobile : "No mobile linked");
        } else {
            tvProfileWelcome.setText("Welcome, Guest!");
            tvProfileUsername.setText("Guest");
            tvProfileEmail.setText("Not available");
            tvProfileMobile.setText("Not available");
        }
    }

    private void loadAllProducts() {
        allProductsList = new ArrayList<>();
        allProductsList.add(new Product("Bacon", "₱350.00", "FOOD CRAFTERS", R.drawable.baconn));
        allProductsList.add(new Product("Promeat Longgadog", "₱270.00", "PROMEAT", R.drawable.longg));
        allProductsList.add(new Product("Mozza Bomb", "₱240.00", "UNBRANDED", R.drawable.moz));
        allProductsList.add(new Product("PopCorn Chicken", "₱180.00", "PUREFOODS", R.drawable.papi));
        allProductsList.add(new Product("Pork Tonkatsu", "₱350.00", "PUREFOODS", R.drawable.idk));
        allProductsList.add(new Product("Squid Rings", "₱220.00", "PUREFOODS", R.drawable.rings));
        allProductsList.add(new Product("Promeat Hungarian", "₱350.00", "PROMEAT", R.drawable.promeathungadog));
        allProductsList.add(new Product("Promeat Ham", "₱320.00", "PROMEAT", R.drawable.ham));
        allProductsList.add(new Product("Luncheon Meat", "₱280.00", "UNBRANDED", R.drawable.lunch));
        allProductsList.add(new Product("Hotdog Bacon Wrap", "₱210.00", "PUREFOODS", R.drawable.rap));
        allProductsList.add(new Product("Beef Tapa", "₱280.00", "PUREFOODS", R.drawable.bip));
        allProductsList.add(new Product("Lechon Roll", "₱320.00", "PUREFOODS", R.drawable.laman));
        allProductsList.add(new Product("Bangus", "₱190.00", "PUREFOODS", R.drawable.daing));
        allProductsList.add(new Product("CDO FUNTASTYK PORK TOCINO", "₱190.00", "CDO", R.drawable.cdo_funtastyk_pork_tocino));
        allProductsList.add(new Product("Carneval Spicy Hungarian Sausage", "₱350.00", "UNBRANDED", R.drawable.spicysausage));
        allProductsList.add(new Product("Nuggets", "₱420.00", "UNBRANDED", R.drawable.nuggets));
        allProductsList.add(new Product("Black Pepper Chicken Nuggets", "₱270.00", "UNBRANDED", R.drawable.black));
        allProductsList.add(new Product("Crispy Chicken Cop", "₱250.00", "UNBRANDED", R.drawable.cop));
        allProductsList.add(new Product("Rib Steak", "₱540.00", "UNBRANDED", R.drawable.ribcage));
        allProductsList.add(new Product("Garlic Longganisang Calumpit", "₱180.00", "UNBRANDED", R.drawable.calumpit));
        allProductsList.add(new Product("Chicken Fingers", "₱190.00", "UNBRANDED", R.drawable.fingers));
        allProductsList.add(new Product("Tempura Ebi", "₱295.00", "UNBRANDED", R.drawable.temp));
        allProductsList.add(new Product("Purefoods TenderJuicy Hotdog", "₱270.00", "PUREFOODS", R.drawable.reddog));
        allProductsList.add(new Product("Topmeat Pork Tocino", "₱290.00", "TOP MEAT", R.drawable.toptocino));
        allProductsList.add(new Product("Beef Trimming", "₱290.00", "UNBRANDED", R.drawable.trim));
        allProductsList.add(new Product("Japanese Sausage Bacon Wrap", "₱240.00", "UNBRANDED", R.drawable.japwrap));
        allProductsList.add(new Product("Big Siomai", "₱170.00", "UNBRANDED", R.drawable.big));
        allProductsList.add(new Product("Purefoods TenderJuicy Hotdog Chicken Cheese", "₱270.00", "PUREFOODS", R.drawable.browndog));
        allProductsList.add(new Product("Mozzarella Cheese", "₱180.00", "UNBRANDED", R.drawable.cheesee));
        allProductsList.add(new Product("Denorado Mindoro Rice Sack", "₱1450.00", "UNBRANDED", R.drawable.denorado));
    }

    private void filterProducts(String brand) {
        if (brand.equals("All Brands")) {
            productAdapter.updateList(allProductsList);
            return;
        }

        List<Product> filteredList = new ArrayList<>();
        for (Product item : allProductsList) {
            if (item.getBrand().equalsIgnoreCase(brand)) {
                filteredList.add(item);
            }
        }
        productAdapter.updateList(filteredList);
    }

    private void setupCarousel() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.mge_banner1);
        images.add(R.drawable.mge_promo2);
        images.add(R.drawable.summer_sale);

        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPagerCarousel.setAdapter(adapter);

        viewPagerCarousel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentPos = viewPagerCarousel.getCurrentItem();
            int nextPos = currentPos + 1;
            if (nextPos >= viewPagerCarousel.getAdapter().getItemCount()) {
                nextPos = 0;
            }
            viewPagerCarousel.setCurrentItem(nextPos, true);
        }
    };

    private void setupCategoryToggle(int buttonId, String brandName) {
        findViewById(buttonId).setOnClickListener(v -> {
            if (currentCategory.equals(brandName) && productView.getVisibility() == View.VISIBLE) {
                showHome();
            } else {
                showProducts(brandName);
            }
        });
    }

    private void showHome() {
        homeContent.setVisibility(View.VISIBLE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.GONE);
        currentCategory = "";
        if (etSearch != null) etSearch.setText("");
    }

    private void showProducts(String brand) {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.VISIBLE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.GONE);

        tvSelectedCategory.setText(brand.equals("Search Results") ? "Search Results" : brand + " Items");
        currentCategory = brand;

        if (!brand.equals("Search Results")) {
            filterProducts(brand);
        }
    }

    private void showOrders() {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.VISIBLE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.GONE);
    }

    private void showProfile() {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.VISIBLE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.GONE);
    }

    private void showCart() {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.VISIBLE);
        checkoutView.setVisibility(View.GONE);
        cartAdapter.notifyDataSetChanged();
    }

    private void showCheckout() {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserOrders();
    }

    private void saveUserOrder(Order newOrder) {
        String username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "Guest";

        SharedPreferences userPrefs = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);
        String userOrdersKey = "ORDERS_" + username;
        String existingUserOrders = userPrefs.getString(userOrdersKey, "");

        String orderEntry = newOrder.getOrderId() + "|~|" + newOrder.getDate() + "|~|" + newOrder.getStatus() + "|~|" + newOrder.getItemsSummary() + "|~|" + newOrder.getTotal();

        userPrefs.edit().putString(userOrdersKey, orderEntry + "@@" + existingUserOrders).apply();
    }

    private void loadUserOrders() {
        orderList.clear();
        String username = getIntent().getStringExtra("USERNAME");
        if (username == null) username = "Guest";

        SharedPreferences userPrefs = getSharedPreferences("MGE_USER_DATA", MODE_PRIVATE);
        String userOrdersKey = "ORDERS_" + username;
        String savedOrders = userPrefs.getString(userOrdersKey, "");

        if (!savedOrders.isEmpty()) {
            String[] ordersArray = savedOrders.split("@@");
            for (String orderStr : ordersArray) {
                if (orderStr.trim().isEmpty()) continue;
                String[] parts = orderStr.split("\\|~\\|");
                if (parts.length == 5) {
                    String oId = parts[0];
                    String oDate = parts[1];
                    String oStatus = parts[2];
                    String oItems = parts[3];
                    double oTotal = 0;
                    try { oTotal = Double.parseDouble(parts[4]); } catch (Exception e) {}

                    oStatus = getUpdatedStatusFromAdmin(oId, oStatus);

                    orderList.add(new Order(oId, oDate, oStatus, oItems, oTotal));
                }
            }
        }
        orderAdapter.notifyDataSetChanged();
    }

    private String getUpdatedStatusFromAdmin(String targetOrderId, String currentStatus) {
        SharedPreferences adminPrefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
        String existingOrders = adminPrefs.getString("ALL_ORDERS", "");
        if (!existingOrders.isEmpty()) {
            String[] orders = existingOrders.split("##");
            for (String o : orders) {
                if (o.trim().isEmpty()) continue;
                String[] p = o.split("\\|\\|");
                if (p.length == 6) {
                    if (p[0].equals(targetOrderId)) {
                        return p[5];
                    }
                }
            }
        }
        return currentStatus;
    }
}
