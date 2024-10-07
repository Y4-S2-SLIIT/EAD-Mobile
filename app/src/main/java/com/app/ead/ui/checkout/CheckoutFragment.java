package com.app.ead.ui.checkout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.ead.R;
import com.app.ead.cart.CartItem;
import com.app.ead.ui.cart.CartViewModel;
import com.app.service.NetworkRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckoutFragment extends Fragment {

    private static final String ARG_CART_ITEM_IDS = "cart_item_ids"; // Key for the argument
    private List<String> cartItemIds; // Store the cart item IDs
    private CartViewModel cartViewModel; // CartViewModel to load cart items
    private List<CartItem> cartItems; // This would contain the CartItems including prices and quantities
    private NetworkRequest networkRequest;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    // Factory method to create a new instance of this fragment
    public static CheckoutFragment newInstance(ArrayList<String> cartItemIds) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_CART_ITEM_IDS, cartItemIds); // Pass the item IDs
        fragment.setArguments(args); // Set the arguments
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cartItemIds = getArguments().getStringArrayList(ARG_CART_ITEM_IDS); // Retrieve item IDs
        }

        // Initialize ViewModel
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        // Load cart items
        cartViewModel.loadCartItems(getContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_checkout, container, false);

        TextView totalPriceText = root.findViewById(R.id.total_price_text);
        TextView confirmationText = root.findViewById(R.id.confirmation_text);
        Button placeOrderButton = root.findViewById(R.id.place_order_button);

        // Set confirmation message
        confirmationText.setText("Confirm Order");

        // Observe cart items from ViewModel
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItemLists -> {
            // Update UI with loaded cart items
            cartItems = cartItemLists; // Update cartItems with the observed data
            double totalPrice = calculateTotalPrice(); // Calculate the total price

            totalPriceText.setText("Total Price: " + totalPrice);
            System.out.println("Total Price: " + totalPrice);
        });

        // Handle place order button click
        placeOrderButton.setOnClickListener(v -> {
            // Logic to place the order can be added here
            System.out.println("Placing order for items");
            if (cartItems != null && !cartItems.isEmpty()) {
                try {
                    // Construct the order JSON object
                    this.networkRequest = new NetworkRequest(getContext());

                    String customerId = networkRequest.getCusId(); // Replace with actual method to get userId
                    String deliveryAddress = "Your Delivery Address"; // Replace this with the actual delivery address

                    // Initialize total amount
                    int total = 0;

                    // Create the items array in the desired structure
                    // Create the items array in the desired structure
                    JSONArray orderItemsArray = new JSONArray();

                    for (CartItem item : cartItems) {
                        String productId = item.getProduct().getId(); // Assuming you have a method to get the product ID
                        int quantity = item.getQuantity();

                        // Update total
                        int price = (int) item.getProduct().getPrice();
                        total += price * quantity;

                        // Create order item object
                        JSONObject orderItem = new JSONObject();
                        orderItem.put("productId", productId);
                        orderItem.put("quantity", quantity);

                        // Check if vendor item already exists
                        JSONObject vendorItem = null;
                        for (int i = 0; i < orderItemsArray.length(); i++) {
                            JSONObject existingVendorItem = orderItemsArray.getJSONObject(i);
                            if (existingVendorItem.getString("venderId").equals(item.getProduct().getVendorId())) {
                                vendorItem = existingVendorItem;
                                break;
                            }
                        }

                        if (vendorItem == null) {
                            // Create a new vendor item object
                            vendorItem = new JSONObject();
                            vendorItem.put("venderId", item.getProduct().getVendorId()); // Assuming method exists
                            vendorItem.put("orderItems", new JSONArray()); // Initialize orderItems as an array
                            orderItemsArray.put(vendorItem);
                        }

                        // Add the current order item to the vendor's order items array
                        vendorItem.getJSONArray("orderItems").put(orderItem);
                    }

                    // Create the final order object
                    JSONObject finalOrder = new JSONObject();
                    finalOrder.put("customerId", customerId);
                    finalOrder.put("total", total);
                    finalOrder.put("deliveryAddress", deliveryAddress);
                    finalOrder.put("items", orderItemsArray);

                    // Print the final order object , finalOrder
                    System.out.println(finalOrder.toString());

                    boolean orderStatue = placeOrder(finalOrder);

                    if (orderStatue) {
                        Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Failed to place the order. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Handle exceptions appropriately
                }
            } else {
                System.out.println("No items in cart to place order");
            }
        });

        return root;
    }

    // Method to calculate total price
    private double calculateTotalPrice() {
        double total = 0.0; // Initialize total as double
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                total += item.getProduct().getPrice() * item.getQuantity(); // Ensure price is a double
            }
        }
        return total;
    }

    private boolean placeOrder(JSONObject finalOrder) {
        this.networkRequest = new NetworkRequest(getContext());
        String URL = getContext().getString(R.string.backend_api) + "order/";

        try {
            String response = networkRequest.sendPostRequest(URL, finalOrder);
//            Log.d("TAG", response);

            if (response != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}