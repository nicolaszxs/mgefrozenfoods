package com.example.mgefrozenfoods;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private CartListener listener;

    public interface CartListener {
        void onRemoveItem(int position);
    }

    public CartAdapter(List<CartItem> cartItemList, CartListener listener) {
        this.cartItemList = cartItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.tvCartName.setText(item.getProduct().getName());
        holder.tvCartPrice.setText(item.getProduct().getPrice());
        holder.ivCartImage.setImageResource(item.getProduct().getImageResId());

        holder.etCartQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvCartSubtotal.setText(String.format("₱%.2f", item.getSubtotal()));

        holder.etCartQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int qty = Integer.parseInt(s.toString());
                    if (qty > 0) {
                        item.setQuantity(qty);
                    }
                } catch (Exception e) {}
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.btnRemoveCart.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCartImage;
        TextView tvCartName, tvCartPrice, tvCartSubtotal;
        EditText etCartQuantity;
        Button btnRemoveCart;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCartImage = itemView.findViewById(R.id.ivCartImage);
            tvCartName = itemView.findViewById(R.id.tvCartName);
            tvCartPrice = itemView.findViewById(R.id.tvCartPrice);
            tvCartSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
            etCartQuantity = itemView.findViewById(R.id.etCartQuantity);
            btnRemoveCart = itemView.findViewById(R.id.btnRemoveCart);
        }
    }
}