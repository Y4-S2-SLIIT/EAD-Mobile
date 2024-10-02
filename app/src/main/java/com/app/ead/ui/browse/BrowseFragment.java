package com.app.ead.ui.browse;

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

import com.app.ead.databinding.FragmentBrowseBinding;
import com.app.product.ProductAdapter;

public class BrowseFragment extends Fragment {

    private FragmentBrowseBinding binding;
    private BrowseViewModel browseViewModel;
    private ProductAdapter productAdapter; // Adapter for RecyclerView

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrowseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up the RecyclerView
        RecyclerView recyclerView = binding.recyclerView; // Ensure the ID is correct in the XML
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(); // Initialize your adapter
        recyclerView.setAdapter(productAdapter); // Set the adapter to the RecyclerView

        browseViewModel = new ViewModelProvider(this).get(BrowseViewModel.class);
        browseViewModel.loadProducts(requireContext()); // Pass context to loadProducts

        // Observe the product list
        browseViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products); // Update the adapter with the new product list
            } else {
                Toast.makeText(getContext(), "No products available", Toast.LENGTH_SHORT).show();
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
