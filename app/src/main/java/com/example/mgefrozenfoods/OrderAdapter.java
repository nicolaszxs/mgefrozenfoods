package com.example.mgefrozenfoods;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderDate.setText("Date: " + order.getDate());
        holder.tvOrderTotal.setText(String.format("₱%.2f", order.getTotal()));
        holder.tvOrderItems.setText("Items: " + order.getItemsSummary());

        holder.btnViewOrderInfo.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Viewing info for " + order.getOrderId(), Toast.LENGTH_SHORT).show();
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