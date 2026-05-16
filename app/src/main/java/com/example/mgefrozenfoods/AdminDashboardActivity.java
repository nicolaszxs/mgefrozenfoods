package com.example.mgefrozenfoods;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class AdminDashboardActivity extends AppCompatActivity {


   private Button btnManageProducts, btnManageOrders, btnRegisteredUsers, btnPrintReport, btnLogout;
   private TextView tvTotalSales, tvTotalOrders;
   private WebView printWebView;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_admin_dashboard);


       tvTotalSales = findViewById(R.id.tvTotalSales);
       tvTotalOrders = findViewById(R.id.tvTotalOrders);
       btnManageProducts = findViewById(R.id.btnManageProducts);
       btnManageOrders = findViewById(R.id.btnManageOrders);
       btnRegisteredUsers = findViewById(R.id.btnRegisteredUsers);
       btnPrintReport = findViewById(R.id.btnPrintReport);
       btnLogout = findViewById(R.id.btnLogout);


       btnManageProducts.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageProductsActivity.class)));
       btnManageOrders.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class)));
       btnRegisteredUsers.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, RegisteredUsersActivity.class)));
       btnPrintReport.setOnClickListener(v -> printSalesReport());


       btnLogout.setOnClickListener(v -> {
           startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
           finish();
       });
   }


   @Override
   protected void onResume() {
       super.onResume();
       updateDashboardData();
   }


   private void updateDashboardData() {
       SharedPreferences prefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
       String allOrders = prefs.getString("ALL_ORDERS", "");


       int count = 0;
       float calculatedTotalSales = 0.0f;


       if (!allOrders.isEmpty()) {
           String[] ordersArray = allOrders.split("##");
           for (String o : ordersArray) {
               if (o.trim().isEmpty()) continue;


               String[] p = o.split("\\|\\|");
               if (p.length >= 9) {
                   count++;
                   if (p[5].equalsIgnoreCase("Completed")) {
                       try {
                           calculatedTotalSales += Float.parseFloat(p[4]);
                       } catch (Exception e) {}
                   }
               }
           }
       }


       prefs.edit().putFloat("DAILY_TOTAL_SALES", calculatedTotalSales).apply();


       tvTotalOrders.setText(String.valueOf(count));
       tvTotalSales.setText(String.format("₱%.2f", calculatedTotalSales));
   }


   private void printSalesReport() {
       WebView webView = new WebView(this);
       webView.setWebViewClient(new WebViewClient() {
           @Override
           public void onPageFinished(WebView view, String url) {
               createWebPrintJob(view);
               printWebView = null;
           }
       });


       SharedPreferences prefs = getSharedPreferences("MGE_ADMIN_DATA", MODE_PRIVATE);
       float totalSales = prefs.getFloat("DAILY_TOTAL_SALES", 0.0f);
       String formattedTotal = String.format("₱%.2f", totalSales);


       String htmlDocument = "<html><body>" +
               "<h1 style='text-align:center;'>MGE FROZEN FOODS</h1>" +
               "<h3 style='text-align:center;'>Official Sales Summary Report</h3>" +
               "<hr><table style='width:100%; border-collapse: collapse;' border='1'>" +
               "<tr style='background-color:#f2f2f2;'><th>Note</th><th>Description</th><th>Status</th></tr>" +
               "<tr><td>Sales Data</td><td>All recorded valid orders</td><td>Completed</td></tr>" +
               "</table><h2 style='text-align:right;'>TOTAL REVENUE: " + formattedTotal + "</h2></body></html>";


       webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
       printWebView = webView;
   }


   private void createWebPrintJob(WebView webView) {
       PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
       String jobName = getString(R.string.app_name) + " Sales Report";
       PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
       printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
   }
}
