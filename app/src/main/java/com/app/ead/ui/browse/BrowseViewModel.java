package com.app.ead.ui.browse;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.app.ead.R;
import com.app.product.Product;
import com.app.service.NetworkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BrowseViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> products;
    private NetworkRequest networkRequest;
    private String URL;

    public BrowseViewModel() {
        products = new MutableLiveData<>();
    }

    public void loadProducts(Context context) {
        // Initialize networkRequest and URL here
        this.networkRequest = new NetworkRequest(context);
        this.URL = context.getString(R.string.backend_api) + "product";

        try {
            String response = networkRequest.sendGetRequest(this.URL);
            Log.d("TAG", response);
            if (response != null) {
                List<Product> productList = parseProducts(response);
                products.setValue(productList);
            } else {
                products.setValue(new ArrayList<>()); // Set empty list if response is null
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            products.setValue(new ArrayList<>()); // Set empty list on error
        }
    }

    private List<Product> parseProducts(String response) throws JSONException {
        List<Product> productList = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(response);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // Extract product details
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            String brand = jsonObject.getString("brand");
            String description = jsonObject.getString("description");
            double price = jsonObject.getDouble("price");
            String image = jsonObject.getString("image");
            int stock = jsonObject.getInt("stock");
            String vendorId = jsonObject.getString("vendorId");

            // Extract only the category name
            String categoryName = jsonObject.getJSONObject("category").getString("name");

            // Create a Product object and add it to the list
            Product product = new Product(id, name, brand, description, categoryName, price, image, stock , vendorId);
            productList.add(product);
        }

        return productList;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }
}
