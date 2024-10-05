package com.app.ead.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ead.cart.CartItemAdapter;
import com.app.ead.databinding.FragmentCartBinding;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
