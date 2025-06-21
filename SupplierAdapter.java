package com.example.wedwise_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SupplierAdapter extends ArrayAdapter<Supplier> {

    private Context context;
    private ArrayList<Supplier> suppliers;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public SupplierAdapter(Context context, ArrayList<Supplier> suppliers, OnDeleteClickListener listener) {
        super(context, 0, suppliers);
        this.context = context;
        this.suppliers = suppliers;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_supplier, parent, false);
        }

        Supplier supplier = getItem(position);

        TextView letterView = convertView.findViewById(R.id.supplierLetter);
        TextView nameView = convertView.findViewById(R.id.supplierName);
        TextView noteView = convertView.findViewById(R.id.supplierNote);
        TextView statusView = convertView.findViewById(R.id.bookingStatus);
        ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);

        // Set the values
        if (supplier != null) {
            letterView.setText(supplier.getFirstLetter()); // No error now
            nameView.setText(supplier.getName());
            noteView.setText(supplier.getNote());
            statusView.setText(supplier.isBooked() ? "Booked" : "Not Booked");

            // Set click listener for delete icon
            deleteIcon.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(position);
                }
            });
        }

        return convertView;
    }
}