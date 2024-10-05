package com.app.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ead.R;
import com.app.service.NetworkRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy; // For caching
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.products.clear(); // Clear existing items
        this.products.addAll(products); // Add new items
        notifyDataSetChanged(); // Notify the adapter to refresh the list
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item, parent, false); // Ensure you have product_item layout
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productPrice;
        private TextView productBrand; // New field for Brand
        private TextView productCategory; // New field for Category
        private ImageView productImage;
        private TextView productAvailability;
        private Button addToCartButton; // Button for adding to cart
        private String URL;
        private NetworkRequest networkRequest;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name); // Ensure these IDs match your layout
            productPrice = itemView.findViewById(R.id.product_price);
            productImage = itemView.findViewById(R.id.product_image);
            productAvailability = itemView.findViewById(R.id.product_availability);
            productBrand = itemView.findViewById(R.id.product_brand); // Initialize brand TextView
            productCategory = itemView.findViewById(R.id.product_category); // Initialize category TextView
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button); // Initialize button
            networkRequest = new NetworkRequest(itemView.getContext());
            URL = itemView.getContext().getString(R.string.backend_api);
        }

        public void bind(Product product) {
            productName.setText(product.getName());
            productPrice.setText(String.format("Price: $%.2f", product.getPrice())); // Format the price
            productBrand.setText("Brand: " + product.getBrand()); // Bind brand data
            productCategory.setText("Category: " + product.getCategory()); // Bind category data
            productAvailability.setText("Availability: " + product.getStock()); // Show availability

            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_launcher_foreground) // Optional: placeholder image
                            .error(R.drawable.ic_launcher_background) // Optional: error image
                            .diskCacheStrategy(DiskCacheStrategy.ALL)) // Cache strategy
                    .into(productImage);
            addToCartButton.setOnClickListener(v -> {
                addToCart(product.getId());
            });
        }

        private void addToCart(String productId){
            try {
                JSONObject cartItem = new JSONObject();
                cartItem.put("productId", productId);
                cartItem.put("quantity", 1);

                String response = networkRequest.sendPostRequest(URL + "cart/add?userId=" + networkRequest.getCusId(), cartItem);
                if(response != null){
                    JSONObject jsonResponse = new JSONObject(response);
                    if(jsonResponse.getInt("status") == 200) {
                        Toast.makeText(itemView.getContext(), "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    Toast.makeText(itemView.getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
