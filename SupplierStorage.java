package com.example.wedwise_java;

import java.util.ArrayList;
import java.util.HashMap;

public class SupplierStorage {

    // Map category name -> list of suppliers in that category
    private static HashMap<String, ArrayList<Supplier>> suppliersByCategory = new HashMap<>();

    // Get suppliers for a category, create empty list if none exists
    public static ArrayList<Supplier> getSuppliers(String category) {
        if (!suppliersByCategory.containsKey(category)) {
            suppliersByCategory.put(category, new ArrayList<>());
        }
        return suppliersByCategory.get(category);
    }

    // Add supplier to a category
    public static void addSupplier(String category, Supplier supplier) {
        getSuppliers(category).add(supplier);
    }

    // Remove supplier from a category by position
    public static void removeSupplier(String category, int position) {
        ArrayList<Supplier> list = getSuppliers(category);
        if (position >= 0 && position < list.size()) {
            list.remove(position);
        }
    }
}