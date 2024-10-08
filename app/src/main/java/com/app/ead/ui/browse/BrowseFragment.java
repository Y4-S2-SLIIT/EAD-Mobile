package com.app.ead.ui.browse;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ead.MainBottomActivity;
import com.app.ead.R;
import com.app.ead.databinding.FragmentBrowseBinding;
import com.app.product.Product;
import com.app.product.ProductAdapter;
import com.app.service.NetworkRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BrowseFragment extends Fragment {

    private FragmentBrowseBinding binding;
    private BrowseViewModel browseViewModel;
    private ProductAdapter productAdapter;
    private Spinner categorySpinner;
    private SearchView searchView;
    private List<String> categories = new ArrayList<>(); // Category list

    @Override
    public void onResume() {
        super.onResume();
        // Hide the ActionBar when the BrowseFragment is resumed
        MainBottomActivity activity = (MainBottomActivity) getActivity();
        if (activity != null) {
            activity.hideActionBar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Show the ActionBar when leaving the BrowseFragment
        MainBottomActivity activity = (MainBottomActivity) getActivity();
        if (activity != null) {
            activity.showActionBar();
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrowseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set up RecyclerView
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter();
        recyclerView.setAdapter(productAdapter);

        // Set up ViewModel
        browseViewModel = new ViewModelProvider(this).get(BrowseViewModel.class);
        browseViewModel.loadProducts(requireContext());

        // Observe products
        browseViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                productAdapter.setProducts(products);
            } else {
                Toast.makeText(getContext(), "No products available", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up SearchView
        searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query, getSelectedCategory());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText, getSelectedCategory());
                return false;
            }
        });

        // Set up Spinner for categories
        categories = loadCategories(getContext());
        categorySpinner = binding.categorySpinner;
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Filter products based on category selection
                filterProducts(searchView.getQuery().toString(), categories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return root;
    }
    private List<String> loadCategories(Context context){
        NetworkRequest networkRequest = new NetworkRequest(context);
        String URL = context.getString(R.string.backend_api) + "category";
        List<String> categoryList = new ArrayList<>();

        try {
            String response = networkRequest.sendGetRequest(URL);
            categoryList.add("All");
            if (response != null) {
                JSONArray categoryArray = new JSONArray(response);
                for (int i = 0; i < categoryArray.length(); i++) {
                    JSONObject jsonObject = categoryArray.getJSONObject(i);
                    categoryList.add(jsonObject.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception// Set empty list on error
        }
        return categoryList;
    }

    // Get selected category from the spinner
    private String getSelectedCategory() {
        return categories.get(categorySpinner.getSelectedItemPosition());
    }

    // Method to filter products based on search query and selected category
    private void filterProducts(String query, String category) {
        browseViewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                List<Product> filteredProducts = new ArrayList<>();

                for (Product product : products) {
                    boolean matchesCategory = category.equals("All") || product.getCategory().equalsIgnoreCase(category);
                    boolean matchesQuery = product.getName().toLowerCase().contains(query.toLowerCase()) ||
                            product.getDescription().toLowerCase().contains(query.toLowerCase());

                    if (matchesCategory && matchesQuery) {
                        filteredProducts.add(product);
                    }
                }

                productAdapter.setProducts(filteredProducts);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // Show the ActionBar when leaving the BrowseFragment
        MainBottomActivity activity = (MainBottomActivity) getActivity();
        if (activity != null) {
            activity.showActionBar();
        }
    }
}
