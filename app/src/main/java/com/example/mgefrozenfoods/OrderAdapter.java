package com.example.mgefrozenfoods;


import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {


   private List<Order> orderList;


   public OrderAdapter(List<Order> orderList) {
       this.orderList = orderList;
   }


   @NonNull
   @Override
   public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
       return new OrderViewHolder(view);
   }


   @Override
   public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
       Order order = orderList.get(position);


       holder.tvOrderId.setText(order.getOrderId());
       holder.tvOrderTotal.setText(String.format("₱%.2f", order.getTotalPrice()));
       holder.tvOrderDate.setText("Date: " + order.getDate());
       holder.tvOrderItems.setText("Items: " + order.getItems());


       String status = order.getStatus();
       if (status == null || status.isEmpty()) {
           status = "Pending";
       }


       holder.tvOrderStatus.setText(status.toUpperCase());


       if (status.equalsIgnoreCase("Pending")) {
           holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#FFC107"));
           holder.tvOrderStatus.setTextColor(Color.BLACK);
       } else if (status.equalsIgnoreCase("Preparing") || status.equalsIgnoreCase("Accepted")) {
           holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#2196F3"));
           holder.tvOrderStatus.setTextColor(Color.WHITE);
       } else if (status.equalsIgnoreCase("Completed") || status.equalsIgnoreCase("Delivered")) {
           holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#4CAF50"));
           holder.tvOrderStatus.setTextColor(Color.WHITE);
       } else if (status.equalsIgnoreCase("Cancelled")) {
           holder.tvOrderStatus.setBackgroundColor(Color.parseColor("#F44336"));
           holder.tvOrderStatus.setTextColor(Color.WHITE);
       } else {
           holder.tvOrderStatus.setBackgroundColor(Color.GRAY);
           holder.tvOrderStatus.setTextColor(Color.WHITE);
       }


       holder.btnViewOrderInfo.setOnClickListener(v -> {
           AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
           builder.setTitle("Order Info: " + order.getOrderId());
           builder.setMessage("Customer: " + order.getUsername() + "\n\nAddress: " + order.getAddress() + "\n\nItems:\n" + order.getItems().replace(";", "\n") + "\n\nTotal: ₱" + String.format("%.2f", order.getTotalPrice()));
           builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
           builder.show();
       });
   }


   @Override
   public int getItemCount() {
       return orderList.size();
   }


   public static class OrderViewHolder extends RecyclerView.ViewHolder {
       TextView tvOrderId, tvOrderStatus, tvOrderTotal, tvOrderDate, tvOrderItems;
       Button btnViewOrderInfo;


       public OrderViewHolder(@NonNull View itemView) {
           super(itemView);
           tvOrderId = itemView.findViewById(R.id.tvOrderId);
           tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
           tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
           tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
           tvOrderItems = itemView.findViewById(R.id.tvOrderItems);
           btnViewOrderInfo = itemView.findViewById(R.id.btnViewOrderInfo);
       }
   }
}


