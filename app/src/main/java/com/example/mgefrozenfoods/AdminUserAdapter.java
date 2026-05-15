package com.example.mgefrozenfoods;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
   private List<AdminUser> userList;
   private String[] statusOptions = {"Pending", "Preparing", "For Delivery", "Completed"};


   public AdminUserAdapter(List<AdminUser> userList) { this.userList = userList; }


   @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false));
   }


   @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       AdminUser order = userList.get(position);
       holder.tvOrderId.setText(order.getOrderId());
       holder.tvCustomerName.setText(order.getCustomerName());
       holder.tvContactAddress.setText(order.getContactAddress());
       holder.tvPaymentTotal.setText("₱" + order.getPaymentTotal());
       holder.tvRefNum.setText("GCash Ref: " + order.getRefNumber());
       holder.tvCurrentStatus.setText(order.getStatus());


       Context context = holder.itemView.getContext();


       if (order.getImagePath() != null && !order.getImagePath().equals("NO_IMAGE") && !order.getImagePath().trim().isEmpty()) {
           holder.btnViewReceipt.setVisibility(View.VISIBLE);
           holder.btnViewReceipt.setOnClickListener(v -> {
               try {
                   Intent intent = new Intent(Intent.ACTION_VIEW);
                   intent.setDataAndType(Uri.parse(order.getImagePath()), "image/*");
                   intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                   context.startActivity(intent);
               } catch (Exception e) {
                   try {
                       Intent fallbackIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(order.getImagePath()));
                       fallbackIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                       context.startActivity(fallbackIntent);
                   } catch (Exception ex) {
                       Toast.makeText(context, "Cannot open image. Please check app permissions or make a new order.", Toast.LENGTH_LONG).show();
                   }
               }
           });
       } else {
           holder.btnViewReceipt.setVisibility(View.GONE);
       }


       ArrayAdapter<String> sAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusOptions);
       sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       holder.spinStatus.setAdapter(sAdapter);
       int pos = sAdapter.getPosition(order.getStatus());
       if (pos >= 0) holder.spinStatus.setSelection(pos);


       holder.btnSaveStatus.setOnClickListener(v -> {
           String selectedStatus = holder.spinStatus.getSelectedItem().toString();
           order.setStatus(selectedStatus);
           holder.tvCurrentStatus.setText(selectedStatus);


           SharedPreferences prefs = context.getSharedPreferences("MGE_ADMIN_DATA", Context.MODE_PRIVATE);
           String existingOrders = prefs.getString("ALL_ORDERS", "");
           if (!existingOrders.isEmpty()) {
               StringBuilder updatedOrders = new StringBuilder();
               String[] ordersArray = existingOrders.split("##");
               for (String o : ordersArray) {
                   if (o.trim().isEmpty()) continue;
                   String[] p = o.split("\\|\\|");
                   if (p.length >= 9 && p[0].equals(order.getOrderId())) {
                       p[5] = selectedStatus;
                       o = p[0] + "||" + p[1] + "||" + p[2] + "||" + p[3] + "||" + p[4] + "||" + p[5] + "||" + p[6] + "||" + p[7] + "||" + p[8];
                   }
                   updatedOrders.append(o).append("##");
               }
               prefs.edit().putString("ALL_ORDERS", updatedOrders.toString()).apply();
               Toast.makeText(context, "Order Status Updated", Toast.LENGTH_SHORT).show();
           }
       });
   }


   @Override public int getItemCount() { return userList.size(); }


   public static class ViewHolder extends RecyclerView.ViewHolder {
       TextView tvOrderId, tvCurrentStatus, tvCustomerName, tvContactAddress, tvPaymentTotal, tvRefNum;
       Spinner spinStatus; Button btnSaveStatus, btnViewReceipt;
       public ViewHolder(@NonNull View v) {
           super(v);
           tvOrderId = v.findViewById(R.id.tvOrderId);
           tvCurrentStatus = v.findViewById(R.id.tvCurrentStatus);
           tvCustomerName = v.findViewById(R.id.tvCustomerName);
           tvContactAddress = v.findViewById(R.id.tvContactAddress);
           tvPaymentTotal = v.findViewById(R.id.tvPaymentTotal);
           tvRefNum = v.findViewById(R.id.tvRefNum);
           btnViewReceipt = v.findViewById(R.id.btnViewReceipt);
           spinStatus = v.findViewById(R.id.spinStatus);
           btnSaveStatus = v.findViewById(R.id.btnSaveStatus);
       }
   }
}
