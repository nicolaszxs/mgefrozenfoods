package com.example.mgefrozenfoods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ScrollView homeContent, cartView, checkoutView;
    private LinearLayout productView, ordersView, profileView, sectionLearn, layoutGCashDetails;
    private TextView tvSelectedCategory, tvCartTotalItems, tvCartGrandTotal, tvCheckoutTotal;
    private RecyclerView rvProducts, rvCartItems, rvOrders;
    private ViewPager2 viewPagerCarousel;
    private TextView tvProfileWelcome, tvProfileUsername, tvProfileEmail, tvProfileMobile;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCOD, rbGCash;
    private EditText etCheckoutAddress, etCheckoutContactNumber, etCheckoutMobile, etCheckoutReference;
    private EditText etSearch;
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
        try {
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
            rvOrders = findViewById(R.id.rvOrders);
            etSearch = findViewById(R.id.etSearch);

            orderList = new ArrayList<>();
            orderAdapter = new OrderAdapter(orderList);
            rvOrders.setLayoutManager(new LinearLayoutManager(this));
            rvOrders.setAdapter(orderAdapter);

            cartList = new ArrayList<>();
            cartAdapter = new CartAdapter(cartList, position -> {
                cartList.remove(position);
                cartAdapter.notifyDataSetChanged();
                calculateCartTotal();
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
                if (!exists) cartList.add(new CartItem(product, 1));
                calculateCartTotal();
                showCart();
            });
            rvProducts.setAdapter(productAdapter);

            setupCarousel();
            loadUserData();

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterSearch(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            findViewById(R.id.btnLogoutHeader).setOnClickListener(v -> performLogout());
            findViewById(R.id.btnUserLogout).setOnClickListener(v -> performLogout());
            findViewById(R.id.btnCartHeader).setOnClickListener(v -> showCart());
            findViewById(R.id.btnLearnMore).setOnClickListener(v -> homeContent.smoothScrollTo(0, sectionLearn.getTop()));

            setupCategoryToggle(R.id.btnAll, "All Brands");
            setupCategoryToggle(R.id.btnPurefoods, "PUREFOODS");
            setupCategoryToggle(R.id.btnFoodCrafters, "FOOD CRAFTERS");
            setupCategoryToggle(R.id.btnPromeat, "PROMEAT");
            setupCategoryToggle(R.id.btnCdo, "CDO");
            setupCategoryToggle(R.id.btnBountyFresh, "BOUNTY FRESH");
            setupCategoryToggle(R.id.btnTopMeat, "TOP MEAT");
            setupCategoryToggle(R.id.btnUnbranded, "UNBRANDED");

            findViewById(R.id.btnUpdateCart).setOnClickListener(v -> {
                cartAdapter.notifyDataSetChanged();
                calculateCartTotal();
            });

            findViewById(R.id.btnContinueShopping).setOnClickListener(v -> showHome());
            findViewById(R.id.btnCheckout).setOnClickListener(v -> {
                if (cartList.isEmpty()) Toast.makeText(this, "Empty cart", Toast.LENGTH_SHORT).show();
                else showCheckout();
            });
            findViewById(R.id.btnCancelCheckout).setOnClickListener(v -> showCart());

            rgPaymentMethod.setOnCheckedChangeListener((group, id) -> {
                layoutGCashDetails.setVisibility(id == R.id.rbGCash ? View.VISIBLE : View.GONE);
            });

            findViewById(R.id.btnPlaceOrder).setOnClickListener(v -> processOrder());

            findViewById(R.id.navHome).setOnClickListener(v -> showHome());
            findViewById(R.id.navProductsTab).setOnClickListener(v -> showProducts("All Brands"));
            findViewById(R.id.navOrders).setOnClickListener(v -> showOrders());
            findViewById(R.id.navProfile).setOnClickListener(v -> showProfile());

            showHome();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void filterSearch(String text) {
        List<Product> filtered = new ArrayList<>();
        for (Product item : allProductsList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getBrand().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(item);
            }
        }
        productAdapter.updateList(filtered);

        if (!text.isEmpty()) {
            setViewVisibility(productView);
            tvSelectedCategory.setText("Search Results");
            updateNavHighlights(R.id.navProductsTab);
        } else if (productView.getVisibility() == View.VISIBLE && tvSelectedCategory.getText().toString().equals("Search Results")) {
            showProducts("All Brands");
        }
    }

    private void updateNavHighlights(int activeId) {
        int[] ids = {R.id.navHome, R.id.navProductsTab, R.id.navOrders, R.id.navProfile};
        for (int id : ids) {
            LinearLayout layout = findViewById(id);
            if (layout == null) continue;
            ImageView icon = (ImageView) layout.getChildAt(0);
            TextView text = (TextView) layout.getChildAt(1);
            if (id == activeId) {
                icon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#0D6EFD")));
                text.setTextColor(Color.parseColor("#0D6EFD"));
            } else {
                icon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#6C757D")));
                text.setTextColor(Color.parseColor("#6C757D"));
            }
        }
    }

    private void performLogout() {
        startActivity(new Intent(HomeActivity.this, MainActivity.class));
        finish();
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
        if (username != null && !username.isEmpty()) {
            tvProfileWelcome.setText("Welcome, " + username + "!");
            tvProfileUsername.setText(username);
            tvProfileEmail.setText(getIntent().getStringExtra("EMAIL"));
            tvProfileMobile.setText(getIntent().getStringExtra("MOBILE"));
        }
    }

    private void processOrder() {
        String address = etCheckoutAddress.getText().toString().trim();
        String contact = etCheckoutContactNumber.getText().toString().trim();
        if (address.isEmpty() || contact.isEmpty()) return;

        SharedPreferences prefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
        String existing = prefs.getString("ALL_ORDERS", "");
        String order = "Order#" + (System.currentTimeMillis() % 100000) + "||" + tvProfileUsername.getText() + "||" + address + "||Items||Total: ₱" + currentGrandTotal + "||Pending";
        prefs.edit().putString("ALL_ORDERS", order + "##" + existing).apply();

        cartList.clear();
        cartAdapter.notifyDataSetChanged();
        calculateCartTotal();
        showOrders();
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
        List<Product> filtered = new ArrayList<>();
        for (Product item : allProductsList) {
            if (item.getBrand().equalsIgnoreCase(brand)) filtered.add(item);
        }
        productAdapter.updateList(filtered);
    }

    private void setupCarousel() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.mge_banner1);
        images.add(R.drawable.mge_promo2);
        images.add(R.drawable.summer_sale);
        viewPagerCarousel.setAdapter(new CarouselAdapter(images));
    }

    private void setupCategoryToggle(int id, String brand) {
        findViewById(id).setOnClickListener(v -> {
            etSearch.setText("");
            showProducts(brand);
        });
    }

    private void showHome() {
        setViewVisibility(homeContent);
        updateNavHighlights(R.id.navHome);
        currentCategory = "";
    }

    private void showProducts(String brand) {
        setViewVisibility(productView);
        tvSelectedCategory.setText(brand + " Items");
        currentCategory = brand;
        filterProducts(brand);
        updateNavHighlights(R.id.navProductsTab);
    }

    private void showOrders() {
        setViewVisibility(ordersView);
        loadUserOrders();
        updateNavHighlights(R.id.navOrders);
    }

    private void showProfile() {
        setViewVisibility(profileView);
        updateNavHighlights(R.id.navProfile);
    }

    private void showCart() {
        setViewVisibility(cartView);
        cartAdapter.notifyDataSetChanged();
        updateNavHighlights(-1);
    }

    private void showCheckout() {
        setViewVisibility(checkoutView);
        updateNavHighlights(-1);
    }

    private void setViewVisibility(View activeView) {
        homeContent.setVisibility(View.GONE);
        productView.setVisibility(View.GONE);
        ordersView.setVisibility(View.GONE);
        profileView.setVisibility(View.GONE);
        cartView.setVisibility(View.GONE);
        checkoutView.setVisibility(View.GONE);
        activeView.setVisibility(View.VISIBLE);
    }

    private void loadUserOrders() {
        orderList.clear();
        SharedPreferences prefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
        String data = prefs.getString("ALL_ORDERS", "");
        String user = tvProfileUsername.getText().toString();
        if (!data.isEmpty()) {
            for (String o : data.split("##")) {
                if (o.trim().isEmpty()) continue;
                String[] p = o.split("\\|\\|");
                if (p.length >= 6 && p[1].equals(user)) {
                    orderList.add(new Order(p[0], "Status: " + p[5], p[5], p[3], 0.0));
                }
            }
        }
        orderAdapter.notifyDataSetChanged();
    }

    private class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {
        private List<Integer> images;
        public CarouselAdapter(List<Integer> images) { this.images = images; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_carousel, p, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int p) { h.iv.setImageResource(images.get(p)); }
        @Override public int getItemCount() { return images.size(); }
        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView iv;
            ViewHolder(View v) { super(v); iv = v.findViewById(R.id.ivCarouselImage); }
        }
    }
}
