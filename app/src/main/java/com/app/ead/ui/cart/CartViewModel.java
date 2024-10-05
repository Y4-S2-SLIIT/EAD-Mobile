package com.app.ead.ui.cart;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.ead.R;
import com.app.ead.cart.CartItem;
import com.app.product.Product;
import com.app.service.NetworkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final MutableLiveData<List<CartItem>> cartItems;
    private NetworkRequest networkRequest;
    private String URL;

    public CartViewModel() {
        cartItems = new MutableLiveData<>();
    }

    public void loadCartItems(Context context) {
        // Initialize networkRequest and URL
        this.networkRequest = new NetworkRequest(context);
        this.URL = context.getString(R.string.backend_api) + "cart/user/" + networkRequest.getCusId();

        try {
            String response = networkRequest.sendGetRequest(this.URL);
            Log.d("TAG", response);
            if (response != null) {
                List<CartItem> cartItemList = parseCartItems(response);
                cartItems.setValue(cartItemList);
            } else {
                cartItems.setValue(new ArrayList<>()); // Set empty list if response is null
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            cartItems.setValue(new ArrayList<>()); // Set empty list on error
        }
    }

    private List<CartItem> parseCartItems(String response) throws JSONException {
        List<CartItem> cartItemList = new ArrayList<>();
        JSONObject responseJSON = new JSONObject(response);
        JSONArray itemsArray = responseJSON.getJSONArray("items");

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject jsonObject = itemsArray.getJSONObject(i);

            // Extract cart item details
            String productId = jsonObject.getString("productId");
            int quantity = jsonObject.getInt("quantity");

            // Extract product information from "productDetails"
            JSONObject productJson = jsonObject.getJSONObject("productDetails");
            String id = productJson.getString("id");
            String name = productJson.getString("name");
            String brand = productJson.getString("brand");
            String description = productJson.getString("description");

            // Extract only the category name
            String categoryName = productJson.getJSONObject("category").getString("name");

            double price = productJson.getDouble("price");
            String imageUrl = productJson.getString("image");
            int stock = productJson.getInt("stock");

            // Create a Product object using the product details
            Product productDetails = new Product(id, name, brand, description, categoryName, price, imageUrl, stock);

            // Create a CartItem object using product details and quantity
            CartItem cartItem = new CartItem(productId, quantity, productDetails);
            cartItemList.add(cartItem);
        }

        return cartItemList;
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public void updateCartItemQuantity(Context context, String productId, int newQuantity) {
        this.networkRequest = new NetworkRequest(context);
        this.URL = context.getString(R.string.backend_api) + "cart/update?userId=" + networkRequest.getCusId();

        try {
            JSONObject cartItem = new JSONObject();
            cartItem.put("productId", productId);
            cartItem.put("quantity", newQuantity);

            String response = networkRequest.sendPostRequest(URL, cartItem);
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("status") == 200) {
                    // Successfully updated, now reload cart items
                    loadCartItems(context);
                } else {
                    Log.e("CartViewModel", "Error updating quantity");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeCartItem(Context context, String productId) {
        this.networkRequest = new NetworkRequest(context);
        this.URL = context.getString(R.string.backend_api) + "cart/remove?userId=" + networkRequest.getCusId();

        try {
            JSONObject cartItem = new JSONObject();
            cartItem.put("productId", productId);

            String response = networkRequest.sendPostRequest(URL, cartItem);
            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getInt("status") == 200) {
                    // Successfully removed, reload cart items
                    loadCartItems(context);
                } else {
                    Log.e("CartViewModel", "Error removing item");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
