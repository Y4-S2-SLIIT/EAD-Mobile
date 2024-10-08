package com.app.ead.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ead.R;
import com.app.ead.cart.CartItem;
import com.app.ead.cart.CartItemAdapter;
import com.app.ead.databinding.FragmentCartBinding;
import com.app.ead.ui.checkout.CheckoutFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;
    private CartItemAdapter cartItemAdapter; // Adapter for RecyclerView

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the RecyclerView
        RecyclerView recyclerView = binding.recyclerView; // Ensure the ID matches your XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartItemAdapter = new CartItemAdapter(); // Initialize your adapter
        recyclerView.setAdapter(cartItemAdapter); // Set the adapter to the RecyclerView

        // Initialize ViewModel
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        cartViewModel.loadCartItems(requireContext()); // Load cart items with context

        // Observe the cart item list
        cartViewModel.getCartItems().observe(getViewLifecycleOwner(), cartItems -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                cartItemAdapter.setCartItems(cartItems); // Update the adapter with the new cart items
                binding.emptyStateLayout.setVisibility(View.GONE); // Hide empty state
                recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            } else {
                binding.emptyStateLayout.setVisibility(View.VISIBLE); // Show empty state
                recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            }
        });

        // Set up the Checkout button click listener
        binding.checkoutButton.setOnClickListener(v -> {
            List<CartItem> items = cartItemAdapter.getCartItems(); // Get the cart items from the adapter
            if (items != null && !items.isEmpty()) {
                List<String> itemIds = new ArrayList<>();
                for (CartItem item : items) {
                    itemIds.add(item.getProductId()); // Collect item IDs
                }
                // Navigate to CheckoutFragment using newInstance
                CheckoutFragment checkoutFragment = CheckoutFragment.newInstance((ArrayList<String>) itemIds);
                NavHostFragment.findNavController(this).navigate(R.id.action_cartFragment_to_checkoutFragment, checkoutFragment.getArguments());
                // Use the correct action ID for navigation
            } else {
                System.out.println("Cart is empty.");
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}