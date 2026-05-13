package com.example.mgefrozenfoods;

import android.content.Context;
import android.content.SharedPreferences;
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

    public AdminUserAdapter(List<AdminUser> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminUser order = userList.get(position);
        holder.tvOrderId.setText(order.getOrderId());
        holder.tvCurrentStatus.setText(order.getStatus());
        holder.tvCustomerName.setText(order.getCustomerName());
        holder.tvContactAddress.setText(order.getContactAddress());
        holder.tvItemsOrdered.setText(order.getItemsOrdered());
        holder.tvPaymentTotal.setText(order.getPaymentTotal());

        Context context = holder.itemView.getContext();
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, statusOptions);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinStatus.setAdapter(spinAdapter);

        int spinnerPosition = spinAdapter.getPosition(order.getStatus());
        if (spinnerPosition >= 0) {
            holder.spinStatus.setSelection(spinnerPosition);
        }

        holder.btnSaveStatus.setOnClickListener(v -> {
            String selectedStatus = holder.spinStatus.getSelectedItem().toString().trim();
            order.setStatus(selectedStatus);
            holder.tvCurrentStatus.setText(selectedStatus);

            SharedPreferences adminPrefs = context.getSharedPreferences("MGE_ADMIN_DATA", Context.MODE_PRIVATE);
            StringBuilder sb = new StringBuilder();
            double totalSales = 0;

            for (AdminUser u : userList) {
                sb.append(u.getOrderId()).append("||")
                        .append(u.getCustomerName()).append("||")
                        .append(u.getContactAddress()).append("||")
                        .append(u.getItemsOrdered()).append("||")
                        .append(u.getPaymentTotal()).append("||")
                        .append(u.getStatus()).append("##");

                if (u.getStatus().equals("Completed")) {
                    totalSales += parseAmount(u.getPaymentTotal());
                }
            }

            adminPrefs.edit()
                    .putString("ALL_ORDERS", sb.toString())
                    .putFloat("DAILY_TOTAL_SALES", (float) totalSales)
                    .commit();

            Toast.makeText(context, "Saved & Sales Updated!", Toast.LENGTH_SHORT).show();
        });
    }

    private double parseAmount(String paymentText) {
        try {
            int index = paymentText.lastIndexOf(":");
            if (index != -1) {
                String amountStr = paymentText.substring(index + 1).replaceAll("[^\\d.]", "");
                return Double.parseDouble(amountStr);
            }
            String clean = paymentText.replaceAll("[^\\d.]", "");
            return clean.isEmpty() ? 0.0 : Double.parseDouble(clean);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCurrentStatus, tvCustomerName, tvContactAddress, tvItemsOrdered, tvPaymentTotal;
        Spinner spinStatus;
        Button btnSaveStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCurrentStatus = itemView.findViewById(R.id.tvCurrentStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvContactAddress = itemView.findViewById(R.id.tvContactAddress);
            tvItemsOrdered = itemView.findViewById(R.id.tvItemsOrdered);
            tvPaymentTotal = itemView.findViewById(R.id.tvPaymentTotal);
            spinStatus = itemView.findViewById(R.id.spinStatus);
            btnSaveStatus = itemView.findViewById(R.id.btnSaveStatus);
        }
    }
}