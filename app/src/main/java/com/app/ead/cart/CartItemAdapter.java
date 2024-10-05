package com.app.ead.cart;

import static android.content.Intent.getIntent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ead.MainBottomActivity;
import com.app.ead.R;
import com.app.ead.ui.cart.CartFragment;
import com.app.ead.ui.cart.CartViewModel;
import com.app.service.NetworkRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItem> cartItems = new ArrayList<>();
    private Context context;

    // Set the cart items to the adapter and notify changes
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems.clear();
        this.cartItems.addAll(cartItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false); // Ensure correct cart_item layout
        return new CartItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName, productPrice, productQuantity, productBrand, productCategory, productStocks, subTotal;
        private final ImageView productImage;
        private final Button increaseQuantityButton, decreaseQuantityButton, removeFromCartButton;
        private final NetworkRequest networkRequest;
        private final String backendApiUrl;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name); // Ensure correct IDs
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            productBrand = itemView.findViewById(R.id.product_brand);
            productCategory = itemView.findViewById(R.id.product_category);
            productStocks = itemView.findViewById(R.id.product_availability);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            subTotal = itemView.findViewById(R.id.total_price);
            increaseQuantityButton = itemView.findViewById(R.id.increase_quantity);
            decreaseQuantityButton = itemView.findViewById(R.id.decrease_quantity);
            removeFromCartButton = itemView.findViewById(R.id.remove_from_cart_button);

            networkRequest = new NetworkRequest(itemView.getContext());
            backendApiUrl = itemView.getContext().getString(R.string.backend_api);
        }

        public void bind(CartItem cartItem) {
            double pricePerUnit = cartItem.getProduct().getPrice();
            int quantity = cartItem.getQuantity();
            productName.setText(cartItem.getProduct().getName());
            productPrice.setText(String.format("Price: $%.2f", pricePerUnit));
            productQuantity.setText(String.valueOf(quantity));
            productBrand.setText("Brand: " + cartItem.getProduct().getBrand());
            productCategory.setText("Category: " + cartItem.getProduct().getCategory());
            productStocks.setText("Availability: " + String.valueOf(cartItem.getProduct().getStock()));
            subTotal.setText(String.format("Total: $%.2f", pricePerUnit * (double) quantity));

            // Load product image using Glide
            Glide.with(itemView.getContext())
                    .load(cartItem.getProduct().getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_background)
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(productImage);

            // Handle button clicks
            setupClickListeners(cartItem);
        }

        private void setupClickListeners(CartItem cartItem) {
            // Increase quantity
            increaseQuantityButton.setOnClickListener(v -> {
                int qty = Integer.parseInt(productQuantity.getText().toString());
                int newQuantity = qty + 1;
                updateCartQuantity(cartItem.getProductId(), newQuantity, cartItem.getProduct().getPrice());
            });

            // Decrease quantity
            decreaseQuantityButton.setOnClickListener(v -> {
                int qty = Integer.parseInt(productQuantity.getText().toString());
                if (qty > 1) {
                    int newQuantity = qty - 1;
                    updateCartQuantity(cartItem.getProductId(), newQuantity, cartItem.getProduct().getPrice());
                }
            });

            // Remove item from cart
            removeFromCartButton.setOnClickListener(v -> {
                removeFromCart(cartItem.getProductId());

                // Restart the activity and navigate to CartFragment
                Context context = itemView.getContext();
                if (context instanceof MainBottomActivity) {
                    ((MainBottomActivity) context).getNavController().navigate(R.id.navigation_cart);
                }
            });
        }

        // Update cart item quantity with API call
        private void updateCartQuantity(String productId, int newQuantity, double pricePerUnit) {
            JSONObject cartItem = new JSONObject();
            try {
                cartItem.put("quantity", newQuantity);
                String response = networkRequest.sendPutRequest(backendApiUrl + "cart/update-quantity/" + productId + "?userId=" + networkRequest.getCusId(), cartItem);
                handleResponse(response, "Quantity updated successfully!", "Error updating quantity!");
                productQuantity.setText(String.valueOf(newQuantity));
                subTotal.setText(String.format("Total: $%.2f", pricePerUnit * (double) newQuantity));
            } catch (JSONException e) {
                showToast("Failed to create JSON data!");
            }
        }

        // Remove item from cart with API call
        private void removeFromCart(String productId) {
            try {
                String response = networkRequest.sendDeleteRequest(backendApiUrl + "cart/remove/" + productId + "?userId=" + networkRequest.getCusId());
                handleResponse(response, "Item removed from cart!", "Error removing item!");
            } catch (Exception e) {
                showToast("Failed to create JSON data!");
            }
        }

        // Handle network response and provide user feedback
        private void handleResponse(String response, String successMessage, String errorMessage) {
            try {
                if (response != null) {
                    JSONObject jsonResponse = new JSONObject(response);
                    if (jsonResponse.getInt("status") == 200) {
                        showToast(successMessage);
                    } else {
                        showToast(errorMessage);
                    }
                } else {
                    showToast(errorMessage);
                }
            } catch (JSONException e) {
                showToast("Failed to parse response!");
            }
        }

        // Helper method to show toast messages
        private void showToast(String message) {
            Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
